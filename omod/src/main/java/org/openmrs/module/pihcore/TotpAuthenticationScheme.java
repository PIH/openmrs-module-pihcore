/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.Authenticated;
import org.openmrs.api.context.BasicAuthenticated;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.authentication.AuthenticationCredentials;
import org.openmrs.module.authentication.AuthenticationUtil;
import org.openmrs.module.authentication.UserLogin;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.util.Security;

import java.util.Properties;

/**
 * This supports configuring and validating against a TOTP provider using something like Google Authenticator
 * See: <a href="https://github.com/samdjstevens/java-totp">https://github.com/samdjstevens/java-totp</a>
 */
public class TotpAuthenticationScheme extends WebAuthenticationScheme {

	// Configuration properties for secret and code
	public static final String SECRET_LENGTH = "secretLength";
	public static final String HASHING_ALGORITHM = "hashingAlgorithm";
	public static final String QR_CODE_ISSUER = "qrCodeIssuer";
	public static final String CODE_LENGTH = "codeLength";
	public static final String CODE_VALIDITY_PERIOD = "codeValidityPeriod";
	public static final String ALLOWED_DISCREPANCY = "allowedDiscrepancy";

	// Configuration properties for login page
	public static final String LOGIN_PAGE = "loginPage";
	public static final String CODE_PARAM = "codeParam";

	private int secretLength;
	private HashingAlgorithm hashingAlgorithm;
	private String qrCodeIssuer;
	private int codeLength;
	private int codeValidityPeriod;
	private int allowedDiscrepancy;
	private String loginPage;
	private String codeParam;

	@Override
	public void configure(String schemeId, Properties config) {
		super.configure(schemeId, config);
		secretLength = AuthenticationUtil.getInteger(config.getProperty(SECRET_LENGTH), 32);
		hashingAlgorithm = HashingAlgorithm.valueOf(config.getProperty(HASHING_ALGORITHM, "SHA1"));
		qrCodeIssuer = config.getProperty(QR_CODE_ISSUER, "PIHEMR");
		codeLength = AuthenticationUtil.getInteger(config.getProperty(CODE_LENGTH), 6);
		codeValidityPeriod = AuthenticationUtil.getInteger(config.getProperty(CODE_VALIDITY_PERIOD), 30);
		allowedDiscrepancy = AuthenticationUtil.getInteger(config.getProperty(ALLOWED_DISCREPANCY), 2);
		loginPage = config.getProperty(LOGIN_PAGE, "/pihcore/account/loginTotp.page");
		codeParam = config.getProperty(CODE_PARAM, "code");
	}

	@Override
	public boolean isUserConfigurationRequired(User user) {
		return StringUtils.isBlank(user.getUserProperty(getSecretUserPropertyName()));
	}

	@Override
	protected Authenticated authenticate(AuthenticationCredentials credentials, UserLogin userLogin) {
		// Ensure the credentials provided are of the expected type
		if (!(credentials instanceof TotpAuthenticationScheme.TotpCredentials)) {
			throw new ContextAuthenticationException("authentication.error.incorrectCredentialsForScheme");
		}
		TotpAuthenticationScheme.TotpCredentials c = (TotpAuthenticationScheme.TotpCredentials) credentials;

		if (c.user == null) {
			throw new ContextAuthenticationException("authentication.error.candidateUserRequired");
		}
		if (StringUtils.isBlank(c.code)) {
			throw new ContextAuthenticationException("authentication.totp.code.required");
		}
		if (userLogin.getUser() != null && !userLogin.getUser().equals(c.user)) {
			throw new ContextAuthenticationException("authentication.error.userDiffersFromCandidateUser");
		}
		String userSecret = c.user.getUserProperty(getSecretUserPropertyName());
		if (StringUtils.isBlank(userSecret)) {
			throw new ContextAuthenticationException("authentication.totp.noSecretConfiguredForUser");
		}
		String decodedSecret = Security.decrypt(userSecret);
		if (!verifyCode(decodedSecret, c.code)) {
			throw new ContextAuthenticationException("authentication.error.invalidCredentials");
		}

		return new BasicAuthenticated(c.user, credentials.getAuthenticationScheme());
	}

	@Override
	public String getChallengeUrl(AuthenticationSession session) {
		return loginPage;
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationSession session) {
		AuthenticationCredentials credentials = session.getUserLogin().getUnvalidatedCredentials(getSchemeId());
		if (credentials != null) {
			return credentials;
		}
		String code = session.getRequestParam(codeParam);
		if (StringUtils.isNotBlank(code)) {
			User candidateUser = session.getUserLogin().getUser();
			credentials = new TotpAuthenticationScheme.TotpCredentials(candidateUser, code);
			session.getUserLogin().addUnvalidatedCredentials(credentials);
			return credentials;
		}
		return null;
	}

	/**
	 * @return the name of the user property that stores the secret configured for this scheme
	 */
	public String getSecretUserPropertyName() {
		return "authentication." + getSchemeId() + ".secret";
	}

	/**
	 * @return a new secret that can be assigned to a user account and used to generate codes
	 */
	public String generateSecret() {
		return new DefaultSecretGenerator(secretLength).generate();
	}

	/**
	 * @param secret the secret to generate the QR code for
	 * @param label the label to associate with the QR code in the app (typically username)
	 * @return a url string that will render to a QR code in the browser for the given secret
	 */
	public String generateQrCodeUriForSecret(String secret, String label) {
		QrData data = new QrData.Builder()
				.label(label)
				.secret(secret)
				.issuer(qrCodeIssuer)
				.algorithm(hashingAlgorithm)
				.digits(codeLength)
				.period(codeValidityPeriod)
				.build();
		try {
			QrGenerator generator = new ZxingPngQrGenerator();
			byte[] imageData = generator.generate(data);
			String mimeType = generator.getImageMimeType();
			return Utils.getDataUriForImage(imageData, mimeType);
		}
		catch (Exception e) {
			throw new RuntimeException("An error occurred generating the QR Code", e);
		}
	}

	/**
	 * @param secret the user assigned secret
	 * @param code the code to verify
	 * @return true if the entered code is valid for the given secret
	 */
	public boolean verifyCode(String secret, String code) {
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator(hashingAlgorithm, codeLength);
		DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		verifier.setTimePeriod(codeValidityPeriod);
		verifier.setAllowedTimePeriodDiscrepancy(allowedDiscrepancy);
		return verifier.isValidCode(secret, code);
	}

	/**
	 * Credentials inner class, to enable access and visibility of credential details to be limited to scheme
	 */
	public class TotpCredentials implements AuthenticationCredentials {

		protected final User user;
		protected final String code;

		@Override
		public String getAuthenticationScheme() {
			return getSchemeId();
		}

		protected TotpCredentials(User user, String code) {
			this.user = user;
			this.code = code;
		}

		@Override
		public String getClientName() {
			return user == null ? null : user.getUsername();
		}
	}
}

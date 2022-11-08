package org.openmrs.module.pihcore.fragment.controller.account;

import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class ViewAccountFragmentController {

	public void controller(@SpringBean("pihCoreService") PihCoreService pihCoreService,
						   @FragmentParam("person") Person person,
						   FragmentModel model) {

		User currentUser = Context.getAuthenticatedUser();
		PihAccountDomainWrapper account = pihCoreService.newPihAccountDomainWrapper(person);
		boolean isSysAdmin = currentUser.hasPrivilege(PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION);
		boolean isOwnAccount = currentUser.getPerson().equals(person);
		boolean twoFactorAvailable = AuthenticationConfig.getAuthenticationScheme() instanceof TwoFactorAuthenticationScheme;

		model.put("currentUser", currentUser);
		model.put("personId", person.getPersonId());
		model.put("account", account);
		model.put("isSysAdmin", isSysAdmin);
		model.put("isOwnAccount", isOwnAccount);
		model.addAttribute("twoFactorAvailable", twoFactorAvailable);
	}
}

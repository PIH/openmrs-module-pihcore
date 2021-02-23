package org.openmrs.module.pihcore.identifier;

import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.BaseIdentifierSourceService;
import org.openmrs.module.pihcore.identifier.sierraLeone.ConfigureSierraLeoneIdGenerators;
import org.openmrs.module.pihcore.identifier.sierraLeone.KghIdGeneratorProcessor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * test class for {@link KghIdGeneratorProcessor}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class KghIdGeneratorProcessorTest {

	SequentialIdentifierGenerator generator;
	KghIdGeneratorProcessor processor;
	Date testDate;
	LogEntry lastLogEntry;
		
	@Before
	public void setup() throws Exception {
		mockStatic(Context.class);
		testDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-31");

		generator = new SequentialIdentifierGenerator();
		generator.setName("KGH ID Identifier Generator");
		generator.setUuid(ConfigureSierraLeoneIdGenerators.KGH_ID_IDENTIFIER_SOURCE_UUID);
		generator.setMinLength(11);
		generator.setMaxLength(12);
		generator.setBaseCharacterSet("1234567890");
		generator.setFirstIdentifierBase("0001");

		lastLogEntry = new LogEntry(generator, "KGH2019120012", testDate, new User(), "Test");

		processor = new KghIdGeneratorProcessor() {
			public Date getDate() {
				return testDate;
			}
		};
		processor.setIdentifierSourceService(new BaseIdentifierSourceService() {
			long nextSequenceNum = 1L;
			public void saveSequenceValue(SequentialIdentifierGenerator sequentialIdentifierGenerator, long l) {
				nextSequenceNum = l;
				lastLogEntry.setIdentifier("KGH" + new SimpleDateFormat("yyMM").format(testDate));
			}
			public Long getSequenceValue(SequentialIdentifierGenerator sequentialIdentifierGenerator) {
				return nextSequenceNum;
			}
			public LogEntry getMostRecentLogEntry(IdentifierSource source) throws APIException {
				return lastLogEntry;
			}
		});
	}

	@Test
	public void shouldGenerateIdentifiers() {
		List<String> identifiers = processor.getIdentifiers(generator, 5);
		Assert.assertThat(identifiers.size(), is(5));
		for (int i=0; i<identifiers.size(); i++) {
			Assert.assertThat(identifiers.get(i), is("KGH2001000" + (i+1)));
		}
	}

	@Test
	public void shouldSwitchToNewPrefixAndResetSequenceOnNewDate() throws Exception {
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010001"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010002"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010003"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010004"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010005"));
		testDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-01");
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020001"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020002"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020003"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020004"));
		Assert.assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020005"));
	}
}

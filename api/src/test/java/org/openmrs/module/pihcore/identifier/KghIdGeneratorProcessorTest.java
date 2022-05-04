package org.openmrs.module.pihcore.identifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.BaseIdentifierSourceService;
import org.openmrs.module.pihcore.identifier.sierraLeone.ConfigureSierraLeoneIdGenerators;
import org.openmrs.module.pihcore.identifier.sierraLeone.KghIdGeneratorProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * test class for {@link KghIdGeneratorProcessor}
 */
public class KghIdGeneratorProcessorTest {

	SequentialIdentifierGenerator generator;
	KghIdGeneratorProcessor processor;
	Date testDate;
	LogEntry lastLogEntry;
		
	@BeforeEach
	public void setup() throws Exception {
		testDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-31");

		generator = new SequentialIdentifierGenerator();
		generator.setName("KGH ID Identifier Generator");
		generator.setUuid(ConfigureSierraLeoneIdGenerators.KGH_ID_IDENTIFIER_SOURCE_UUID);
		generator.setPrefix("'KGH'yyMM");
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
		assertThat(identifiers.size(), is(5));
		for (int i=0; i<identifiers.size(); i++) {
			assertThat(identifiers.get(i), is("KGH2001000" + (i+1)));
		}
	}

	@Test
	public void shouldSwitchToNewPrefixAndResetSequenceOnNewDate() throws Exception {
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010001"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010002"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010003"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010004"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20010005"));
		testDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-01");
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020001"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020002"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020003"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020004"));
		assertThat(processor.getIdentifiers(generator, 1).get(0), is("KGH20020005"));
	}
}

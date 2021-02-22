/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.identifier.sierraLeone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.IdentifierSourceService;

/**
 * This extends and replaces the functionality of the SequentialIdentifierGeneratorProcessor
 * For any SequentialIdentifierGenerators that are not the KGH ID source, this delegates to standard functionality
 * For the KGH ID source, it determines if the next identifier's prefix differs from the last generated identifier,
 * and if it does, it first resets the sequence to the initial sequence value.
 */
public class KghIdGeneratorProcessor extends SequentialIdentifierGeneratorProcessor {

    private IdentifierSourceService identifierSourceService;

    /**
     * @param identifierSourceService
     */
    @Override
    public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
        this.identifierSourceService = identifierSourceService;
        super.setIdentifierSourceService(identifierSourceService);
    }

	@Override
	protected synchronized List<String> generateBatch(SequentialIdentifierGenerator seq, Long sequenceValue, int batchSize) {

		// If this is not the KGH ID, then defer to standard functionality in the superclass
		if (!seq.getUuid().equals(ConfigureSierraLeoneIdGenerators.KGH_ID_IDENTIFIER_SOURCE_UUID)) {
			return super.generateBatch(seq, sequenceValue, batchSize);
		}

		// The KGH ID does not use a checksum or have any reserved identifiers, so we can bypass those features here

		// Get the expected prefix based on the current date
		String prefix = getPrefix();

		// Get the last generated identifier.  If it does not start with the expected prefix, reset the sequence
		LogEntry mostRecentLogEntry = identifierSourceService.getMostRecentLogEntry(seq);
		if (mostRecentLogEntry != null) {
			if (!mostRecentLogEntry.getIdentifier().startsWith(prefix)) {
				sequenceValue = resetToFirstSequenceValue(seq);
			}
		}

		// Generate the batch starting from this sequence.
		// Override the default generator in order to more easily add our custom prefix without additional code
		List<String> identifiers = new ArrayList<>();
		for (int i=0; i<batchSize; i++) {
			String firstIdentifierBase = seq.getFirstIdentifierBase() == null ? "0001" : seq.getFirstIdentifierBase();
			char[] charSet = seq.getBaseCharacterSet().toCharArray();
			int seqLength = firstIdentifierBase.length();
			String baseIdentifier = IdgenUtil.convertToBase(sequenceValue, charSet, seqLength);
			String identifier = prefix + baseIdentifier;
			identifiers.add(identifier);
			sequenceValue++;
		}

		identifierSourceService.saveSequenceValue(seq, sequenceValue);

		return identifiers;
	}

	public String getPrefix() {
    	return "KGH" + new SimpleDateFormat("yyMM").format(new Date());
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihcore.reporting.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Takes in a List of Obs and checks for the presence of a specific question/answer combination.
 * If found, returns the configured value
 */
public class CodedObsConverter implements DataConverter {

	private List<Option> options;

    public CodedObsConverter() {}

    public void addOption(String questionUuid, String answerUuid, Object value) {
    	getOptions().add(new Option(questionUuid, answerUuid, value));
    }

    public List<Option> getOptions() {
    	if (options == null) {
    		options = new ArrayList<>();
	    }
    	return options;
    }

    @Override
    public Object convert(Object original) {
		Collection<Obs> obsList = (Collection<Obs>)original;
		if (obsList != null) {
			for (Obs obs : obsList) {
				for (Option option : getOptions()) {
					if (option.matches(obs)) {
						return option.getValue();
					}
				}
			}
		}
		return null;
	}

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

	public class Option {
		private String questionUuid;
		private String answerUuid;
		private Object value;

		public Option(String questionUuid, String answerUuid, Object value) {
			this.questionUuid = questionUuid;
			this.answerUuid = answerUuid;
			this.value = value;
		}

		public String getQuestionUuid() {
			return questionUuid;
		}

		public void setQuestionUuid(String questionUuid) {
			this.questionUuid = questionUuid;
		}

		public String getAnswerUuid() {
			return answerUuid;
		}

		public void setAnswerUuid(String answerUuid) {
			this.answerUuid = answerUuid;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public boolean matches(Obs o) {
			if (o.getConcept().getUuid().equals(questionUuid)) {
				if (o.getValueCoded() != null && o.getValueCoded().getUuid().equals(answerUuid)) {
					return true;
				}
			}
			return false;
		}
	}
}

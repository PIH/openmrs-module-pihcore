package org.openmrs.module.pihcore.htmlformentry.analysis.converter;

public interface Converter<I, O> {
    O convert(I input) throws Exception;
}

package org.openmrs.module.pihcore.htmlformentry.analysis.processor;

import org.openmrs.module.pihcore.htmlformentry.analysis.DataSet;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

import java.util.List;

public interface TagProcessor {
    DataSet process(List<HtmlFormTag> tags) throws Exception;
}

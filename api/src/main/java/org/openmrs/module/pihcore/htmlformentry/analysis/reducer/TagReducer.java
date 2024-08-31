package org.openmrs.module.pihcore.htmlformentry.analysis.reducer;

import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

public interface TagReducer<T> {
    T reduce(HtmlFormTag inputTag);
}

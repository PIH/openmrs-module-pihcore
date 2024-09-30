package org.openmrs.module.pihcore.htmlformentry.analysis.processor;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.DataSet;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
public class TagExporter implements TagProcessor {

    private File outputDirectory;

    private final Logger log = LoggerFactory.getLogger(TagExporter.class);

    public static final List<String> COLUMNS = Arrays.asList(
            "section", "tagName", "style", "groupingConceptId", "conceptId", "conceptIds", "valueCoded",
            "answerConceptId", "answerConceptIds", "answers", "answerConceptSetIds", "answerClasses",
            "answerDrugs", "answerLocationTags", "labelCode", "labelText", "conceptLabels",
            "answerCode", "answerLabel", "answerCodes", "answerLabels", "required",
            "obsCommentUsed", "showCommentField", "commentFieldLabel", "commentFieldCode",
            "dateLabel", "allowFutureDates", "showTime", "defaultValue", "whenValueThen", "toggle", "additionalAttributes"
    );

    public DataSet process(List<HtmlFormTag> tags) throws Exception {
        DataSet ret = new DataSet();
        ret.setHeaderRow("inputFile", "outputFile");
        for (HtmlFormTag tag : tags) {
            try {
                if (outputDirectory == null) {
                    outputDirectory = tag.getHtmlFormFile().getParentFile();
                }
                File outputFile = new File(outputDirectory, tag.getHtmlFormFile().getName() + ".csv");
                log.info("Processing tag " + tag + " into " + outputFile);
                List<String> columns = new ArrayList<>(getAllColumns(Collections.singletonList(tag)));
                DataSet formDataSet = new DataSet();
                formDataSet.setColumns(columns);
                formDataSet.setRows(getAllRows(columns, tag));
                formDataSet.toCsv(outputFile, ',');
                ret.addDataRow(tag.getHtmlFormFile().getAbsolutePath(), outputFile.getAbsolutePath());
            }
            catch (Exception e) {
                log.error("Error exporting tag " + tag, e);
            }
        }
        return ret;
    }

    private Set<String> getAllColumns(List<HtmlFormTag> tags) {
        Set<String> ret = new TreeSet<>(new ColumnComparator());
        ret.add("tagName");
        for (HtmlFormTag tag : tags) {
            ret.addAll(tag.getAttributes().keySet());
            ret.addAll(getAllColumns(tag.getChildTags()));
        }
        return ret;
    }

    private List<Map<String, String>> getAllRows(List<String> columns, HtmlFormTag tag) {
        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, String> row = new HashMap<>();
        Map<String, String> attributes = new HashMap<>(tag.getAttributes());
        attributes.put("tagName", tag.getName());
        for (String column : columns) {
            String value = attributes.remove(column);
            row.put(column, value == null ? "" : value);
        }
        row.put("additionalAttributes", HtmlFormUtils.mapToString(attributes, "=", ","));
        rows.add(row);
        for (HtmlFormTag childTag : tag.getChildTags()) {
            rows.addAll(getAllRows(columns, childTag));
        }
        return rows;
    }

    private static class ColumnComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            int index1 = COLUMNS.indexOf(s1);
            int index2 = COLUMNS.indexOf(s2);
            if (index1 == -1 && index2 != -1) {
                return 1;
            }
            if (index1 != -1 && index2 == -1) {
                return -1;
            }
            int ret = index1 - index2;
            if (ret == 0) {
                ret = s1.compareTo(s2);
            }
            return ret;
        }
    }
}

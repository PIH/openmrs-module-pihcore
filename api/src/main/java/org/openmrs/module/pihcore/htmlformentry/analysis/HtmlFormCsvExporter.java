package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.HtmlFormEntryGenerator;
import org.openmrs.module.htmlformentry.Translator;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.IfModeTagRemover;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.ObsGroupMapper;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.ParentToChildMerger;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.SectionMerger;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.SiblingMerger;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.TagMapper;
import org.openmrs.module.pihcore.htmlformentry.analysis.mapper.TagRemover;
import org.openmrs.module.pihcore.htmlformentry.analysis.reducer.TagCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class HtmlFormCsvExporter {

    private final Logger log = LoggerFactory.getLogger(HtmlFormCsvExporter.class);

    public void export(File file) {
        HtmlFormTag tag = loadFormFromFile(file);

        List<TagMapper> mappers = new ArrayList<>();

        // Remove any standard html tags
        for (Tags.Standard standardTag : Tags.Standard.values()) {
            mappers.add(new TagRemover(standardTag.getNodeName()));
        }

        // Remove tags that do not impact the data model and cannot be easily used as context for other elements
        mappers.add(new TagRemover("uimessage"));
        mappers.add(new TagRemover("lookup"));
        mappers.add(new TagRemover("submit"));

        // Remove any sections of the form that are added *just* for view mode
        mappers.add(new IfModeTagRemover());

        // Flatten any includeIf or excludeIf expressions down into the child tags
        mappers.add(new ParentToChildMerger("includeIf"));
        mappers.add(new ParentToChildMerger("excludeIf"));

        // Handle the complex web of velocity conditions for entry of date/location/provider
        mappers.add(new SiblingMerger("encounterDate"));
        mappers.add(new SiblingMerger("encounterLocation"));
        mappers.add(new SiblingMerger("encounterProviderAndRole"));

        // Merge obs groups attributes down into children, and add any hidden obs
        mappers.add(new ObsGroupMapper());

        // Flatten sections down into the child tags
        mappers.add(new SectionMerger());

        // Handle when/then tags
        mappers.add(new SiblingMerger("when"));
        tag = new TagRemover("controls").map(tag);

        for (TagMapper mapper : mappers) {
            tag = mapper.map(tag);
        }

        TagCounter tagCounter = new TagCounter();
        Map<String, Map<String, Integer>> counters = tagCounter.reduce(tag);
        for (String counter : counters.keySet()) {
            Map<String, Integer> counterValues = counters.get(counter);
            for (String key : counterValues.keySet()) {
                Integer count = counterValues.get(key);
                System.out.println(counter + ", " + key + ", " + count);
            }
        }

        //HtmlFormUtils.printTag(tag, "");
    }

    protected HtmlFormTag loadFormFromFile(File file) {
        HtmlFormParser parser = new HtmlFormParser() {
            @Override
            protected String preprocessXml(String xml) throws Exception {
                HtmlFormEntryGenerator generator = new HtmlFormEntryGenerator();
                xml = generator.stripComments(xml);
                xml = generator.applyMacros(xml);
                xml = generator.applyRepeats(xml);
                xml = generator.applyTranslations(xml, new FormEntryContext(FormEntryContext.Mode.EDIT) {
                    @Override
                    public Translator getTranslator() {
                        return new Translator() {
                            @Override
                            public String translate(String localeStr, String key) {
                                String ret = getTranslations(localeStr).get(key);
                                return (ret == null ? key : ret);
                            }
                        };
                    }
                });
                return xml;
            }
        };
        return parser.parseFile(file);
    }









    public static final List<String> AVAILABLE_COLUMNS = Arrays.asList(
            "sectionName", "tagName", "style", "groupingConceptId", "conceptId", "conceptIds", "valueCoded",
            "answerConceptId", "answerConceptIds", "answers", "answerConceptSetIds", "answerClasses",
            "answerDrugs", "answerLocationTags", "labelCode", "labelText", "conceptLabels",
            "answerCode", "answerLabel", "answerCodes", "answerLabels", "required",
            "obsCommentUsed", "showCommentField", "commentFieldLabel", "commentFieldCode",
            "dateLabel", "allowFutureDates", "showTime", "defaultValue", "whenValueThen", "toggle"
    );

    private final Set<String> columns = new TreeSet<>(new ColumnComparator());

    private final List<Map<String, String>> rows = new ArrayList<>();

    private final Stack<ProcessedTag> processedTagStack = new Stack<>();

    private void processTag(HtmlFormTag tag) {
        ProcessedTag processedTag = getProcessedTag(tag);
        if (!processedTag.getDataRows().isEmpty()) {
            Map<String, String> dataFromParents = new HashMap<>();
            for (ProcessedTag parentTag : processedTagStack) {
                dataFromParents.putAll(parentTag.getDataForChildren());
            }
            for (Map<String, String> row : processedTag.getDataRows()) {
                if (!row.isEmpty()) {
                    row.putAll(dataFromParents);
                    rows.add(row);
                    columns.addAll(row.keySet());
                }
            }
        }
        if (processedTag.isProcessChildren()) {
            processedTagStack.push(processedTag);
            for (HtmlFormTag childTag : tag.getChildTags()) {
                processTag(childTag);
            }
            processedTagStack.pop();
        }
    }

    // This exists in the event that some tag needs to produce multiple rows, most will simply delegate to getDataRow
    private ProcessedTag getProcessedTag(HtmlFormTag tag) {
        ProcessedTag processedTag = new ProcessedTag();
        log.trace("Processing tag: " + tag.getName() + ": " + tag.getAttributes());
        Map<String, String> dataRow = new HashMap<>();
        Map<String, String> unprocessedValues = new HashMap<>(tag.getAttributes());
        boolean tagHandled = true;
        switch (tag.getName()) {


            // section: only contribute section as a column on child rows. only sectionName is relevant
            case "section": {
                ignoreAttributes(unprocessedValues, "id", "headerTag", "sectionTag", "headerStyle");
                String headerCode = unprocessedValues.remove("headerCode");
                String headerLabel = unprocessedValues.remove("headerLabel");
                processedTag.getDataForChildren().put("sectionName", getDisplay(headerCode, headerLabel));
                break;
            }

            case "obs": {

                for (String obsField : AVAILABLE_COLUMNS) {
                    String value = unprocessedValues.remove(obsField);
                    if (StringUtils.isNotBlank(value)) {
                        dataRow.put(obsField, value);
                    }
                }

                // There are situations where only an empty commentFieldLabel is configured to display the comment box, so we try to handle this
                boolean obsCommentUsed = "true".equals(unprocessedValues.get("showCommentField"));
                obsCommentUsed = obsCommentUsed || unprocessedValues.containsKey("commentFieldLabel");
                obsCommentUsed = obsCommentUsed || unprocessedValues.containsKey("commentFieldCode");
                if (obsCommentUsed) {
                    dataRow.put("obsCommentUsed", "true");
                }

                // Roll up any when tags
                List<String> whenConditions = new ArrayList<>();
                for (Iterator<HtmlFormTag> i = tag.getChildTags().iterator(); i.hasNext();) {
                    HtmlFormTag controlsTag = i.next();
                    if (controlsTag.getName().equalsIgnoreCase("controls")) {
                        for (Iterator<HtmlFormTag> j = controlsTag.getChildTags().iterator(); j.hasNext();) {
                            HtmlFormTag whenTag = j.next();
                            if (whenTag.getName().equalsIgnoreCase("when")) {
                                whenConditions.add(HtmlFormUtils.mapToString(whenTag.getAttributes(), "=", ","));
                                j.remove();
                            }
                        }
                        if (controlsTag.getChildTags().isEmpty() && controlsTag.getAttributes().isEmpty()) {
                            i.remove();
                        }
                    }
                }
                dataRow.put("whenValueThen", String.join("|", whenConditions));

                break;
            }

            default: {
                tagHandled = false;
                log.debug("No specific handling found for tag: " + tag.getName());
            }
        }

        if (!dataRow.isEmpty() || !unprocessedValues.isEmpty() || !tagHandled) {
            if (shouldIncludeTag(tag)) {
                dataRow.put("tagName", tag.getName());
                dataRow.put("additionalConfiguration", HtmlFormUtils.mapToString(unprocessedValues, "=", ","));
                processedTag.getDataRows().add(dataRow);
            }
        }

        log.trace("Processed tag: " + processedTag);
        return processedTag;
    }

    private boolean shouldIncludeTag(HtmlFormTag tag) {
        for (Tags.Standard standardTag : Tags.Standard.values()) {
            if (standardTag.getNodeName().equalsIgnoreCase(tag.getName())) {
                return false;
            }
        }
        return true;
    }

    private String getDisplay(String messageCode, String messageText) {
        if (StringUtils.isNotBlank(messageCode)) {
            return messageCode; // TODO: Translate this?
        }
        return messageText == null ? "" : messageText;
    }

    private void ignoreAttributes(Map<String, String> attributes, String... keys) {
        for (String key : keys) {
            attributes.remove(key);
        }
    }

    public Set<String> getColumns() {
        return columns;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    private static class ProcessedTag {
        private boolean processChildren = true;
        private List<Map<String, String>> dataRows = new ArrayList<>();
        private Map<String, String> dataForChildren = new HashMap<>();

        public boolean isProcessChildren() {
            return processChildren;
        }

        public void setProcessChildren(boolean processChildren) {
            this.processChildren = processChildren;
        }

        public List<Map<String, String>> getDataRows() {
            return dataRows;
        }

        public void setDataRows(List<Map<String, String>> dataRows) {
            this.dataRows = dataRows;
        }

        public Map<String, String> getDataForChildren() {
            return dataForChildren;
        }

        public void setDataForChildren(Map<String, String> dataForChildren) {
            this.dataForChildren = dataForChildren;
        }
    }

    private static class ColumnComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            int index1 = AVAILABLE_COLUMNS.indexOf(s1);
            int index2 = AVAILABLE_COLUMNS.indexOf(s2);
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

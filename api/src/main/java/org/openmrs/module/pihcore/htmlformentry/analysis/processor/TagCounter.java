package org.openmrs.module.pihcore.htmlformentry.analysis.processor;

import org.openmrs.module.pihcore.htmlformentry.analysis.DataSet;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class TagCounter implements TagProcessor {

    @Override
    public DataSet process(List<HtmlFormTag> tags) throws Exception {
        Map<String, Map<String, Integer>> counters = new TreeMap<>();
        Stack<HtmlFormTag> stack = new Stack<>();
        for (HtmlFormTag tag : tags) {
            process(tag, counters, stack);
        }
        DataSet dataSet = new DataSet();
        dataSet.setHeaderRow("counter", "key", "value");
        for (String counter : counters.keySet()) {
            Map<String, Integer> counterValues = counters.get(counter);
            for (String key : counterValues.keySet()) {
                Integer count = counterValues.get(key);
                dataSet.addDataRow(counter, key, Integer.toString(count));
            }
        }
        return dataSet;
    }

    private void process(HtmlFormTag inputTag, Map<String, Map<String, Integer>> counters, Stack<HtmlFormTag> stack) {
        Map<String, Integer> tagCounter = counters.computeIfAbsent("tags", k -> new TreeMap<>());
        tagCounter.put(inputTag.getName(), tagCounter.getOrDefault(inputTag.getName(), 0) + 1);

        List<String> names = new ArrayList<>();
        for (HtmlFormTag tagInStack : stack) {
            names.add(tagInStack.getName());
        }
        names.add(inputTag.getName());
        String flatName = String.join(".", names);
        Map<String, Integer> flatTagCounter = counters.computeIfAbsent("flatTags", k -> new TreeMap<>());
        flatTagCounter.put(flatName, flatTagCounter.getOrDefault(flatName, 0) + 1);

        Map<String, Integer> attributeCounter = counters.computeIfAbsent("attributes", k -> new TreeMap<>());
        for (String attributeName : inputTag.getAttributes().keySet()) {
            String name = inputTag.getName() + "." + attributeName;
            attributeCounter.put(name, attributeCounter.getOrDefault(name, 0) + 1);
        }

        stack.push(inputTag);
        for (HtmlFormTag childTag : inputTag.getChildTags()) {
            process(childTag, counters, stack);
        }
        stack.pop();
    }
}

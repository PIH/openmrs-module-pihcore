package org.openmrs.module.pihcore.htmlformentry.analysis.reducer;

import lombok.Data;
import org.openmrs.module.pihcore.htmlformentry.analysis.HtmlFormTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

@Data
public class TagCounter implements TagReducer<Map<String, Map<String, Integer>>> {

    @Override
    public Map<String, Map<String, Integer>> reduce(HtmlFormTag inputTag) {
        Map<String, Map<String, Integer>> counters = new TreeMap<>();
        Stack<HtmlFormTag> stack = new Stack<>();
        reduce(inputTag, counters, stack);
        return counters;
    }

    private void reduce(HtmlFormTag inputTag, Map<String, Map<String, Integer>> counters, Stack<HtmlFormTag> stack) {
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
            reduce(childTag, counters, stack);
        }
        stack.pop();
    }
}

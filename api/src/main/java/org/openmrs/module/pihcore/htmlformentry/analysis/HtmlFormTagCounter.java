package org.openmrs.module.pihcore.htmlformentry.analysis;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HtmlFormTagCounter {

    private final Map<String, Integer> tagCounter = new TreeMap<>();
    private final Map<String, Integer> flatTagCounter = new TreeMap<>();
    private final Map<String, Integer> attributeCounter = new TreeMap<>();

    public HtmlFormTagCounter() { }

    public void analyze(File fileOrDirectory) {
        HtmlFormParser parser = new HtmlFormParser() {
            @Override
            protected void handleTagStart(HtmlFormTag tag) {
                super.handleTagStart(tag);
                tagCounter.put(tag.getName(), tagCounter.getOrDefault(tag.getName(), 0) + 1);
                List<String> names = new ArrayList<>();
                for (HtmlFormTag tagInStack : tagStack) {
                    names.add(tagInStack.getName());
                }
                names.add(tag.getName());
                String flatName = String.join(".", names);
                flatTagCounter.put(flatName, tagCounter.getOrDefault(flatName, 0) + 1);
            }
            @Override
            protected void handleTagAttribute(HtmlFormTag tag, String attributeName, String attributeValue) {
                super.handleTagAttribute(tag, attributeName, attributeValue);
                String name = tag.getName() + "." + attributeName;
                attributeCounter.put(name, attributeCounter.getOrDefault(name, 0) + 1);
            }
        };
        if (fileOrDirectory.isFile()) {
            parser.parseFile(fileOrDirectory);
        }
        else if (fileOrDirectory.isDirectory()) {
            List<File> files = HtmlFormUtils.getNestedFilesBySuffix(fileOrDirectory, "xml");
            for (File file : files) {
                parser.parseFile(file);
            }
        }
    }

    public void writeToCsv(File outputFile) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            try (CSVWriter csvWriter = new CSVWriter(fileWriter)) {
                String[] csvColumns = {"counter", "tagName", "attributeName", "count"};
                csvWriter.writeNext(csvColumns);
                for (String element : tagCounter.keySet()) {
                    csvWriter.writeNext(new String[] {"tags", element, "", Integer.toString(tagCounter.get(element))});
                }
                for (String element : flatTagCounter.keySet()) {
                    csvWriter.writeNext(new String[] {"flatTags", element, "", Integer.toString(flatTagCounter.get(element))});
                }
                for (String element : attributeCounter.keySet()) {
                    String[] split = element.split("\\.");
                    csvWriter.writeNext(new String[] {"tagAttributes", split[0], split[1], Integer.toString(attributeCounter.get(element))});
                }
            }
        }
    }

    public Map<String, Integer> getTagCounter() {
        return tagCounter;
    }

    public Map<String, Integer> getAttributeCounter() {
        return attributeCounter;
    }
}

package org.openmrs.module.pihcore.htmlformentry.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A tag represents a specific xml node in a html form
 */
public class HtmlFormUtils {

    public static List<File> getNestedFilesBySuffix(File directory, String suffix) {
        List<File> files = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] filesInDirectory = directory.listFiles();
            if (filesInDirectory != null) {
                for (File f : filesInDirectory) {
                    if (f.isDirectory()) {
                        files.addAll(getNestedFilesBySuffix(f, suffix));
                    } else {
                        if (f.getName().endsWith(suffix)) {
                            files.add(f);
                        }
                    }
                }
            }
        }
        return files;
    }

    public static String mapToString(Map<String, String> m, String keyValueSeparator, String entrySeparator) {
        List<String> ret = new ArrayList<>();
        for (String key : m.keySet()) {
            ret.add(key + keyValueSeparator + m.get(key));
        }
        return String.join(entrySeparator, ret);
    }

    public static void printTag(HtmlFormTag tag, String prefix) {
        System.out.println(prefix + tag.toString());
        for (HtmlFormTag childTag : tag.getChildTags()) {
            printTag(childTag, prefix + "  ");
        }
    }
}

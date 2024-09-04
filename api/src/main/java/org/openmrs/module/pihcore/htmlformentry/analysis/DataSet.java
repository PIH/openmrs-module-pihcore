package org.openmrs.module.pihcore.htmlformentry.analysis;

import com.opencsv.CSVWriter;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataSet {

    private String name;
    private List<String> columns = new ArrayList<>();
    private List<Map<String, String>> rows = new ArrayList<>();

    public DataSet() {}

    @Override
    public String toString() {
        return name + " (" + columns.size() + " x " + rows.size() + ")";
    }

    public void setHeaderRow(String... values) {
        columns = Arrays.asList(values);
    }

    public void addDataRow(String... values) {
        if (values == null || columns == null || values.length != columns.size()) {
            throw new IllegalArgumentException("Values and columns must have same length");
        }
        Map<String, String> row = new LinkedHashMap<>();
        for (int i=0; i<columns.size(); i++) {
            row.put(columns.get(i), values[i]);
        }
        rows.add(row);
    }

    public void toCsv(File outputFile, char separator) throws Exception {
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            try (CSVWriter writer = new CSVWriter(fileWriter, separator)) {
                writer.writeNext(columns.toArray(new String[0]));
                for (Map<String, String> row : rows) {
                    String[] r = new String[columns.size()];
                    for (int i = 0; i < columns.size(); i++) {
                        r[i] = row.get(columns.get(i));
                    }
                    writer.writeNext(r);
                }
            }
        }
    }

    public void print(String separator) {
        if (StringUtils.isNotBlank(name)) {
            System.out.println(name);
        }
        System.out.println(String.join(separator, columns));
        for (Map<String, String> row : rows) {
            List<String> values = new ArrayList<>();
            for (String c : columns) {
                values.add(row.get(c));
            }
            System.out.println(String.join(separator, values));
        }
    }
}

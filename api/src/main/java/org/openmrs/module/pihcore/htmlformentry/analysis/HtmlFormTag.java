package org.openmrs.module.pihcore.htmlformentry.analysis;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A tag represents a specific xml node in a html form
 */
@Data
public class HtmlFormTag {

    private String name;
    private final Map<String, String> attributes = new HashMap<>();
    private String data;
    private final List<HtmlFormTag> childTags = new ArrayList<>();

    public HtmlFormTag() {
    }

    public HtmlFormTag cloneWithoutChildren() {
        HtmlFormTag clone = new HtmlFormTag();
        clone.setName(name);
        clone.setData(data);
        for (String key : attributes.keySet()) {
            clone.getAttributes().put(key, attributes.get(key));
        }
        return clone;
    }

    public String getAttributeValue(String name) {
        return getAttributes().get(name);
    }

    public void addAttribute(String name, String value, boolean recursively) {
        String existingValue = getAttributes().get(name);
        if (existingValue != null && !existingValue.equals(value)) {
            throw new IllegalStateException("Attribute with name " + name + " already exists in: " + this);
        }
        attributes.put(name, value);
        if (recursively) {
            for (HtmlFormTag child : childTags) {
                child.addAttribute(name, value, true);
            }
        }
    }

    public boolean hasAttribute(String name, String value) {
        String v = attributes.get(name);
        return v != null && v.equalsIgnoreCase(value);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public List<HtmlFormTag> getAllTagsByName(String name) {
        List<HtmlFormTag> ret = new ArrayList<>();
        if (getName().equalsIgnoreCase(name)) {
            ret.add(this);
        }
        for (HtmlFormTag childTag : getChildTags()) {
            ret.addAll(childTag.getAllTagsByName(name));
        }
        return ret;
    }

    @Override
    public String toString() {
        return getName() + getAttributes();
    }
}

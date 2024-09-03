package org.openmrs.module.pihcore.htmlformentry.analysis;

import org.openmrs.module.pihcore.htmlformentry.analysis.converter.Converter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.file.FileToStringConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.FlattenObsGroupsConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.FlattenOrExcludeIfModeConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.MergeParentToChildrenConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.MergeSectionToChildrenConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.MergeSiblingsConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.tag.RemoveTagByNameConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml.ApplyMacrosConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml.ApplyRepeatsConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml.ApplyTranslationsConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml.StripCommentsConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.converter.xml.XmlToTagConverter;
import org.openmrs.module.pihcore.htmlformentry.analysis.processor.TagProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HtmlFormAnalyzer {

    private final Logger log = LoggerFactory.getLogger(HtmlFormAnalyzer.class);

    public <T> DataSet analyze(File fileOrDirectory, TagProcessor processor) throws Exception {
        log.info("Analysis started for " + fileOrDirectory.getAbsolutePath() + " with processor: " + processor);
        List<HtmlFormTag> tags = new ArrayList<>();
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.isFile()) {
                tags.add(convertFileToTag(fileOrDirectory));
            }
            else if (fileOrDirectory.isDirectory()) {
                for (File file : HtmlFormUtils.getNestedFilesBySuffix(fileOrDirectory, "xml")) {
                    try {
                        tags.add(convertFileToTag(file));
                    }
                    catch (Exception e) {
                        log.error("Unable to convert file to tag at: " + file, e);
                    }
                }
            }
        }
        log.info("Successfully converted " + tags.size() + " forms to tags.  Executing processor...");
        return processor.process(tags);
    }

    public HtmlFormTag convertFileToTag(File formFile) {
        try {
            log.debug("Processing htmlform file: " + formFile.getAbsolutePath());
            String xml = new FileToStringConverter().convert(formFile);
            for (Converter<String, String> converter : getXmlConverters()) {
                log.debug("Converting xml: " + converter);
                xml = converter.convert(xml);
            }
            log.debug("Converting xml to tags");
            HtmlFormTag tag = new XmlToTagConverter().convert(xml);
            for (Converter<HtmlFormTag, HtmlFormTag> converter : getTagConverters()) {
                log.debug("Converting tag: " + converter);
                tag = converter.convert(tag);
            }
            tag.setHtmlFormFile(formFile);
            return tag;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load form from " + formFile.getName(), e);
        }
    }

    public List<Converter<String, String>> getXmlConverters() {
        List<Converter<String, String>> l = new ArrayList<>();
        l.add(new StripCommentsConverter());
        l.add(new ApplyMacrosConverter());
        l.add(new ApplyRepeatsConverter());
        l.add(new ApplyTranslationsConverter());
        return l;
    }

    public List<Converter<HtmlFormTag, HtmlFormTag>> getTagConverters() {
        List<Converter<HtmlFormTag, HtmlFormTag>> l = new ArrayList<>();

        // Remove any standard html tags
        for (Tags.Standard standardTag : Tags.Standard.values()) {
            l.add(new RemoveTagByNameConverter(standardTag.getNodeName()));
        }

        // Remove tags that do not impact the data model and cannot be easily used as context for other elements
        l.add(new RemoveTagByNameConverter("uimessage"));
        l.add(new RemoveTagByNameConverter("lookup"));
        l.add(new RemoveTagByNameConverter("submit"));

        // Remove any sections of the form that are added *just* for view mode
        l.add(new FlattenOrExcludeIfModeConverter());

        // Flatten any includeIf or excludeIf expressions down into the child tags
        l.add(new MergeParentToChildrenConverter("includeIf", " AND "));
        l.add(new MergeParentToChildrenConverter("excludeIf", " OR "));

        // Handle the complex web of velocity conditions for entry of date/location/provider
        l.add(new MergeSiblingsConverter("encounterDate"));
        l.add(new MergeSiblingsConverter("encounterLocation"));
        l.add(new MergeSiblingsConverter("encounterProviderAndRole"));

        // Merge obs groups attributes down into children, and add any hidden obs
        l.add(new FlattenObsGroupsConverter());

        // Flatten sections down into the child tags
        l.add(new MergeSectionToChildrenConverter());

        // Handle when/then tags
        l.add(new MergeSiblingsConverter("when"));
        l.add(new RemoveTagByNameConverter("controls"));

        return l;
    }
}

package org.openmrs.module.pihcore.htmlformentry;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.InvalidActionException;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.htmlformentry.widget.CheckboxWidget;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.module.htmlformentry.widget.TextFieldWidget;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.common.MessageUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Usage:
 * <familyHistoryRelativeCheckboxes concept="uuid-of-asthma" label="Asthma" relatives="uuid-of-father,uuid-of-mother"/>
 * => Asthma: [ ]Father  [ ]Mother
 * <familyHistoryRelativeCheckboxes concept="uuid-of-cancer" label="Cancer" relatives="uuid-of-father,uuid-of-mother" specify="true"/>
 * => Cancer, specify:________   [ ]Father  [ ]Mother
 *
 * For each relative box that is checked, an obs group will be stored with concept CIEL:160593 and members:
 * * which disease? CIEL:160592 = @concept
 * * which relative? CIEL:1560 = @relatives[i]
 * * present? CIEL:1729 = YES
 * * comments? CIEL:160618 = comment text
 */
public class FamilyHistoryRelativeCheckboxesTagHandler extends SubstitutionTagHandler {

    private MessageSourceService messageSourceService;

    public FamilyHistoryRelativeCheckboxesTagHandler() {
        this.messageSourceService = Context.getMessageSourceService();
    }

    @Override
    protected List<AttributeDescriptor> createAttributeDescriptors() {
        return Collections.unmodifiableList(Arrays.asList(
                new AttributeDescriptor("concept", Concept.class),
                new AttributeDescriptor("relatives", Concept.class)
        ));
    }

    @Override
    protected String getSubstitution(FormEntrySession session, FormSubmissionController controller, Map<String, String> attributes) throws BadFormDesignException {
        Concept concept = HtmlFormEntryUtil.getConcept(attributes.get("concept"));
        if (concept == null) {
            throw new IllegalArgumentException("Concept not found: " + attributes.get("concept"));
        }
        List<Concept> relatives = new ArrayList<Concept>();
        for (String id : attributes.get("relatives").split(",")) {
            Concept c = HtmlFormEntryUtil.getConcept(id);
            if (c == null) {
                throw new IllegalArgumentException("Relative type not found: " + id);
            }
            relatives.add(c);
        }
        if (relatives.isEmpty()) {
            throw new IllegalArgumentException(PihCoreConstants.HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME + " tag must have at least one relative specified");
        }

        boolean includeCommentField = parseBooleanAttribute(attributes.get("specify"), false);
        String label = attributes.get("label");
        String translateLabel = null;
        if (label == null) {
            translateLabel = concept.getName().getName();
        } else {
            translateLabel = MessageUtil.translate(label);
        }

        Concept familyHistoryConstructConcept = HtmlFormEntryUtil.getConcept(PihCoreConstants.PATIENT_FAMILY_HISTORY_LIST_CONSTRUCT);
        Concept relationShipConcept = HtmlFormEntryUtil.getConcept(PihCoreConstants.RELATIONSHIP_OF_RELATIVE_TO_PATIENT);
        Concept diagnosisConcept = HtmlFormEntryUtil.getConcept(PihCoreConstants.FAMILY_HISTORY_DIAGNOSIS);
        Concept commentsConcept = HtmlFormEntryUtil.getConcept(PihCoreConstants.FAMILY_HISTORY_COMMENT);

        FormEntryContext context = session.getContext();
        TextFieldWidget textFieldWidget = null;
        ErrorWidget errorWidget = new ErrorWidget();
        // maps from wiget to existing obs for this widget
        Map<CheckboxWidget, Obs> relativeWidgets = new LinkedHashMap<CheckboxWidget, Obs>();
        for (Concept relative : relatives) {
            // group: family hx construct, member = which relative, member value = $relative
            // note: this assumes that all instances of this obs group have "is present = true"
            Obs existingObs = findObsGroupForDiagnosisAndRelativeInExistingObs(context, familyHistoryConstructConcept, diagnosisConcept, concept, relationShipConcept, relative);

            CheckboxWidget widget = new CheckboxWidget(relative.getName().getName(), relative.getUuid());
            widget.setInitialValue(existingObs);
            relativeWidgets.put(widget, existingObs);
            context.registerWidget(widget);
        }

        if (includeCommentField) {
            textFieldWidget = new TextFieldWidget();
            textFieldWidget.setPlaceholder(messageSourceService.getMessage("zl.pastMedicalHistory.specifyLabel"));
            Obs existingComments = findDiagnosisCommentInExistingDiagnosisObsGroups(relativeWidgets, commentsConcept);
            if (existingComments != null) {
                String valueText = existingComments.getValueText();
                if (StringUtils.isNotBlank(valueText)) {
                    textFieldWidget.setInitialValue(valueText);
                }
            }

            context.registerWidget(textFieldWidget);
            context.registerErrorWidget(textFieldWidget, errorWidget);
        }
        String clazz = attributes.get("class");
        String elementId = attributes.get("id");

        StringBuilder html = new StringBuilder();
        html.append("<div ");
        if (StringUtils.isNotBlank(elementId)) {
            html.append("id=\"").append(elementId).append("\" ");
        }
        html.append("class=\"family-history-item");
        if (StringUtils.isNotBlank(clazz)) {
            html.append(" ").append(clazz);
        }
        html.append("\">");
        html.append("<div class=\"label\">");
        html.append(translateLabel);
        if (includeCommentField) {
            html.append(messageSourceService.getMessage("zl.familyHistoryRelativeCheckboxes.specifyLabel"));
            html.append(textFieldWidget.generateHtml(context));
            html.append(errorWidget.generateHtml(context));
        }
        html.append("</div>");

        for (Map.Entry<CheckboxWidget, Obs> entry : relativeWidgets.entrySet()) {
            html.append("<div class=\"relative\">");
            CheckboxWidget widget = entry.getKey();
            html.append(widget.generateHtml(context));
            html.append("</div>");
        }
        html.append("</div>");

        session.getSubmissionController().addAction(new Action(concept, relativeWidgets, textFieldWidget));
        return html.toString();
    }

    private Obs findObsGroupForDiagnosisAndRelativeInExistingObs(FormEntryContext context, Concept familyHistoryConstructConcept,
                                                                 Concept diagnosisConcept, Concept diagnosisValue, Concept relationShipConcept, Concept relativeValue) {

        Obs existingObs = null;

        for (Map.Entry<Obs, Set<Obs>> entry : context.getExistingObsInGroups().entrySet()) {
            Obs candidateGroup = entry.getKey();
            if (candidateGroup.getConcept().equals(familyHistoryConstructConcept)) {
                boolean diagnosisFound = false;
                boolean relativeFound = false;
                for (Obs member : entry.getValue()) {
                    if (member.getConcept().equals(diagnosisConcept)
                            && member.getValueCoded().equals(diagnosisValue)) {
                        diagnosisFound = true;
                    }
                    if (member.getConcept().equals(relationShipConcept)
                            && member.getValueCoded().equals(relativeValue)) {
                        relativeFound = true;
                    }
                    if (diagnosisFound && relativeFound) {
                        existingObs = candidateGroup;
                        break;
                    }
                }
            }
        }

        if (existingObs != null) {
            context.getExistingObsInGroups().remove(existingObs);
        }

        return existingObs;
    }

    private Obs findDiagnosisCommentInExistingDiagnosisObsGroups(Map<CheckboxWidget, Obs> relativeWidgets, Concept commentsConcept) {

        // this is a bit of a hack--we pass in all the obs groups for a specific diagnosis, and return the first comment found;
        // note that in our current model, the same comment will be stored in each obs group associated with a concept--so
        // although we are only pulling the first for display purposes, we need to make sure we update all of them

        Obs existingObs = null;

        for (Obs diagnosisObsGroup : relativeWidgets.values()) {
            if (diagnosisObsGroup != null) {
                for (Obs member : diagnosisObsGroup.getGroupMembers(false)) {
                    if (member.getConcept().equals(commentsConcept)) {
                        existingObs = member;
                        break;
                    }
                }
            }
        }

        return existingObs;
    }

    private class Action implements FormSubmissionControllerAction {
        private Concept concept;
        private Map<CheckboxWidget, Obs> relativeWidgets;
        private TextFieldWidget commentsWidget;

        public Action(Concept concept, Map<CheckboxWidget, Obs> relativeWidgets, TextFieldWidget commentsWidget) {
            this.concept = concept;
            this.relativeWidgets = relativeWidgets;
            this.commentsWidget = commentsWidget;
        }

        @Override
        public Collection<FormSubmissionError> validateSubmission(FormEntryContext formEntryContext, HttpServletRequest httpServletRequest) {
            // The UI doesn't allow any invalid states
            return null;
        }

        @Override
        public void handleSubmission(FormEntrySession session, HttpServletRequest request) {
            FormSubmissionActions submissionActions = session.getSubmissionActions();
            FormEntryContext context = session.getContext();

            Concept construct = HtmlFormEntryUtil.getConcept("CIEL:160593");
            Concept which = HtmlFormEntryUtil.getConcept("CIEL:160592");
            Concept whichRelative = HtmlFormEntryUtil.getConcept("CIEL:1560");
            Concept present = HtmlFormEntryUtil.getConcept("CIEL:1729");
            Concept comments = HtmlFormEntryUtil.getConcept("CIEL:160618");

            String commentsValue = commentsWidget == null ? null : commentsWidget.getValue(context, request);

            for (Map.Entry<CheckboxWidget, Obs> entry : relativeWidgets.entrySet()) {
                CheckboxWidget widget = entry.getKey();
                Obs existingObs = entry.getValue();

                String relativeUuidIfChecked = (String) widget.getValue(context, request);
                boolean isPresent = StringUtils.isNotEmpty(relativeUuidIfChecked);

                if (existingObs != null) {
                    if (!isPresent) {
                        // if unchecked, we just void the whole group
                        submissionActions.modifyObs(existingObs, null, null, null, null);
                    }
                    else {
                        // assume the only thing that can change is comments (since this widget doesn't let you change anything else)
                        Obs existingComment = findMember(existingObs, comments);
                        if (existingComment != null) {
                            try {
                                submissionActions.beginObsGroup(existingObs);
                                submissionActions.modifyObs(existingComment, comments, commentsValue, null, null);
                                submissionActions.endObsGroup();
                            } catch (InvalidActionException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                        else if (commentsValue != null) {
                            // I didn't find a better way to add an obs to an existing group
                            try {
                                submissionActions.beginObsGroup(existingObs);
                                submissionActions.createObs(comments, commentsValue, null, null);
                                submissionActions.endObsGroup();
                            } catch (InvalidActionException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }
                }
                else {
                    // create a new obs group is this is checked
                    if (isPresent) {
                        Obs group = new Obs();
                        group.setConcept(construct);

                        Obs whichObs = new Obs();
                        whichObs.setConcept(which);
                        whichObs.setValueCoded(concept);
                        group.addGroupMember(whichObs);

                        Obs whichRelativeObs = new Obs();
                        whichRelativeObs.setConcept(whichRelative);
                        whichRelativeObs.setValueCoded(HtmlFormEntryUtil.getConcept(relativeUuidIfChecked));
                        group.addGroupMember(whichRelativeObs);

                        Obs presentObs = new Obs();
                        presentObs.setConcept(present);
                        presentObs.setValueCoded(HtmlFormEntryUtil.getConcept(PihEmrConfigConstants.CONCEPT_YES_UUID));
                        group.addGroupMember(presentObs);

                        if (StringUtils.isNotBlank(commentsValue)) {
                            Obs commentsObs = new Obs();
                            commentsObs.setConcept(comments);
                            commentsObs.setValueText(commentsValue);
                            group.addGroupMember(commentsObs);
                        }

                        try {
                            submissionActions.beginObsGroup(group);
                            submissionActions.endObsGroup();
                        } catch (InvalidActionException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }
    }

    // put this somewhere reusable
    private Obs findMember(Obs group, Concept memberConcept) {
        for (Obs candidate : group.getGroupMembers(false)) {
            if (candidate.getConcept().equals(memberConcept)) {
                return candidate;
            }
        }
        return null;
    }
}

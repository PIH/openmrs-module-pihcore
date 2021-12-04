package org.openmrs.module.pihcore.apploader.apps;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil;
import org.openmrs.module.pihcore.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.app;

@Component
public class GraphFactory {

    private Config config;

    @Autowired
    public GraphFactory(Config config) {
        this.config = config;
    }

    public AppDescriptor getBloodPressureGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.BLOOD_PRESSURE_GRAPH + extensionPoint,
                "pih.app.bloodPressure.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.bloodPressure.graph.title",
                        "conceptId", CustomAppLoaderConstants.DIASTOLIC_BP_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.SYSTOLIC_BP_CONCEPT_UUID,
                        "maxResults", "10"
                ));
    }

    public AppDescriptor getBmiGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.BMI_GRAPH + extensionPoint,
                "pih.app.bmiGraph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.bmiGraph.title",
                        "showLegend", false,
                        "conceptId", CustomAppLoaderConstants.WEIGHT_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.HEIGHT_CONCEPT_UUID,
                        "hideConcepts", CustomAppLoaderConstants.WEIGHT_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.HEIGHT_CONCEPT_UUID,
                        "function", "(bmi, "
                                + CustomAppLoaderConstants.HEIGHT_CONCEPT_UUID + ", "
                                + CustomAppLoaderConstants.WEIGHT_CONCEPT_UUID + ");", // the order of the parameters is important
                        "maxResults", "12"  // TODO what should this be?
                ));
    }

    public AppDescriptor getCholesterolGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.CHOLESTEROL_GRAPH + extensionPoint,
                "pih.app.cholesterolGraph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.cholesterolGraph.title",
                        "conceptId", CustomAppLoaderConstants.TOTAL_CHOLESTEROL_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.HDL_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.LDL_CONCEPT_UUID,
                        "maxRecords", "10"
                ));
    }

    public AppDescriptor getWHODASGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.WHODAS_GRAPH + extensionPoint,
                "pih.app.whodas.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.whodas.graph.title",
                        "conceptId", CustomAppLoaderConstants.WHODAS,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getZLDSIGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.ZLDSI_GRAPH + extensionPoint,
                "pih.app.zldsi.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.zldsi.graph.title",
                        "conceptId", CustomAppLoaderConstants.ZLDSI,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getSeizureFrequencyGraph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.SEIZURE_FREQUENCY_GRAPH + extensionPoint,
                "pih.app.seizure.frequency.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.seizure.frequency.graph.title",
                        "conceptId", CustomAppLoaderConstants.SEIZURE_FREQUENCY,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getPHQ9Graph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.PHQ9_GRAPH + extensionPoint,
                "pih.app.phq9.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.phq9.graph.title",
                        "conceptId", CustomAppLoaderConstants.PHQ9,
                        "maxRecords", "12"
                ));
    }

    public AppDescriptor getGAD7Graph(String extensionPoint) {
        return CustomAppLoaderUtil.app(
                CustomAppLoaderConstants.Apps.GAD7_GRAPH + extensionPoint,
                "pih.app.gad7.graph.title",
                "icon-bar-chart",
                null,
                null,
                CustomAppLoaderUtil.objectNode(
                        "widget", "obsgraph",
                        "icon", "icon-bar-chart",
                        "label", "pih.app.gad7.graph.title",
                        "conceptId", CustomAppLoaderConstants.GAD7,
                        "maxRecords", "12"
                ));
    }

}

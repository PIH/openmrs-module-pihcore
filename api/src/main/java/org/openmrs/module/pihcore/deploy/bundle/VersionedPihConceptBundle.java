package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle.ConceptClasses;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle.ConceptDatatypes;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle.ConceptMapTypes;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle.ConceptSources;

import java.util.Locale;

/**
 * Convenience superclass that loads existing datatypes, classes, etc, from {@link org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle}.
 * Any concrete subclass needs to do @Required(CoreConeptMetadataBundle.class)
 */
public abstract class VersionedPihConceptBundle extends VersionedMetadataBundle {

    // capitalized this way so they look as close as possible to Locale.ENGLISH in code
    protected Locale locale_HAITI = new Locale("ht");
    protected Locale locale_SPANISH = new Locale("es");
    protected Locale locale_SWAHILI = new Locale("sw");

    protected ConceptDatatype notApplicable;
    protected ConceptDatatype text;
    protected ConceptDatatype coded;
    protected ConceptDatatype booleanDatatype;
    protected ConceptDatatype numeric;
    protected ConceptDatatype datetime;
    protected ConceptDatatype date;
    protected ConceptDatatype time;

    protected ConceptClass finding;
    protected ConceptClass diagnosis;
    protected ConceptClass symptom;
    protected ConceptClass question;
    protected ConceptClass misc;
    protected ConceptClass convSet;
    protected ConceptClass medSet;
    protected ConceptClass drug;
    protected ConceptClass test;
    protected ConceptClass procedure;
    protected ConceptClass frequency;

    protected ConceptMapType sameAs;
    protected ConceptMapType narrowerThan;
    protected ConceptMapType broaderThan;

    protected ConceptSource pih;
    protected ConceptSource pihMalawi;
    protected ConceptSource ciel;
    protected ConceptSource snomedCt;
    protected ConceptSource snomedNp;
    protected ConceptSource snomedMvp;
    protected ConceptSource rxNorm;
    protected ConceptSource imoProcedureIT;
    protected ConceptSource imoProblemIT;
    protected ConceptSource ampath;
    protected ConceptSource emrapi;
    protected ConceptSource mdrtb;
    protected ConceptSource icd10who;
    protected ConceptSource loinc;
    protected ConceptSource mirebalaisReport;

    @Override
    public void install() throws Exception {
        retrieveCoreMetadata();
        super.install();
    }

    /**
     * Most concept bundles won't want to do anything every time, so this default implementation is just a no-op.
     * @throws Exception
     */
    @Override
    protected void installEveryTime() throws Exception {
        // do nothing
    }

    protected void retrieveCoreMetadata() throws Exception {
        notApplicable = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.N_A);
        text = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.TEXT);
        coded = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.CODED);
        booleanDatatype = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.BOOLEAN);
        numeric = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.NUMERIC);
        datetime = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.DATETIME);
        date = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.DATE);
        time = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatypes.TIME);

        finding = MetadataUtils.existing(ConceptClass.class, ConceptClasses.FINDING);
        diagnosis = MetadataUtils.existing(ConceptClass.class, ConceptClasses.DIAGNOSIS);
        symptom = MetadataUtils.existing(ConceptClass.class, ConceptClasses.SYMPTOM);
        question = MetadataUtils.existing(ConceptClass.class, ConceptClasses.QUESTION);
        misc = MetadataUtils.existing(ConceptClass.class, ConceptClasses.MISC);
        convSet = MetadataUtils.existing(ConceptClass.class, ConceptClasses.CONV_SET);
        medSet = MetadataUtils.existing(ConceptClass.class, ConceptClasses.MED_SET);
        drug = MetadataUtils.existing(ConceptClass.class, ConceptClasses.DRUG);
        test = MetadataUtils.existing(ConceptClass.class, ConceptClasses.TEST);
        procedure = MetadataUtils.existing(ConceptClass.class, ConceptClasses.PROCEDURE);
        frequency = MetadataUtils.existing(ConceptClass.class, ConceptClasses.FREQUENCY);

        sameAs = MetadataUtils.existing(ConceptMapType.class, ConceptMapTypes.SAME_AS);
        narrowerThan = MetadataUtils.existing(ConceptMapType.class, ConceptMapTypes.NARROWER_THAN);
        broaderThan = MetadataUtils.existing(ConceptMapType.class, ConceptMapTypes.BROADER_THAN);

        pih = MetadataUtils.existing(ConceptSource.class, ConceptSources.PIH);
        pihMalawi = MetadataUtils.existing(ConceptSource.class, ConceptSources.PIH_MALAWI);
        ciel = MetadataUtils.existing(ConceptSource.class, ConceptSources.CIEL);
        snomedCt = MetadataUtils.existing(ConceptSource.class, ConceptSources.SNOMED_CT);
        snomedNp = MetadataUtils.existing(ConceptSource.class, ConceptSources.SNOMED_NP);
        snomedMvp = MetadataUtils.existing(ConceptSource.class, ConceptSources.SNOMED_MVP);
        rxNorm = MetadataUtils.existing(ConceptSource.class, ConceptSources.RX_NORM);
        imoProcedureIT = MetadataUtils.existing(ConceptSource.class, ConceptSources.IMO_PROCEDURE_IT);
        imoProblemIT = MetadataUtils.existing(ConceptSource.class, ConceptSources.IMO_PROBLEM_IT);
        ampath = MetadataUtils.existing(ConceptSource.class, ConceptSources.AMPATH);
        emrapi = MetadataUtils.existing(ConceptSource.class, ConceptSources.EMRAPI_MODULE);
        mdrtb = MetadataUtils.existing(ConceptSource.class, ConceptSources.MDRTB_MODULE);
        icd10who = MetadataUtils.existing(ConceptSource.class, ConceptSources.ICD10);
        loinc = MetadataUtils.existing(ConceptSource.class, ConceptSources.LOINC);
        mirebalaisReport = MetadataUtils.existing(ConceptSource.class, ConceptSources.HUM_REPORTS_MODULE);
    }

}

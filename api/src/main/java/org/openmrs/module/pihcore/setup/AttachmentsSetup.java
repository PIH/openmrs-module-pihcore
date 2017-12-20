package org.openmrs.module.pihcore.setup;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.module.attachments.AttachmentsConstants;
import org.openmrs.module.attachments.obs.DefaultAttachmentHandler;
import org.openmrs.module.attachments.obs.ImageAttachmentHandler;

import java.util.Locale;

public class AttachmentsSetup {

    // migrate the Concepts for attachments from old visit documents convention
    public static void migrateAttachmentsConceptsIfNecessary(ConceptService conceptService) {

        {
            final String name = AttachmentsConstants.MODULE_SHORT_ID + " DEFAULT ATTACHMENT";
            final String desc = "Concept complex for 'default attachment' complex obs.";
            final String uuid = AttachmentsConstants.CONCEPT_DEFAULT_UUID;

            Concept concept = conceptService.getConceptByUuid(uuid);

            if (concept != null && concept.getPreferredName(Locale.ENGLISH).getName().equals("VDUI PATIENT DOCUMENT")) {

                ConceptComplex conceptComplex = (ConceptComplex) concept;

                // set the new names, as well as update the handler
                conceptComplex.setHandler(DefaultAttachmentHandler.class.getSimpleName());
                ConceptName conceptName = new ConceptName(name, Locale.ENGLISH);
                conceptComplex.setFullySpecifiedName(conceptName);
                conceptComplex.setPreferredName(conceptName);
                conceptComplex.setConceptClass(conceptService.getConceptClassByUuid(ConceptClass.QUESTION_UUID));
                conceptComplex.setDatatype(conceptService.getConceptDatatypeByUuid(ConceptDatatype.COMPLEX_UUID));
                conceptComplex.addDescription(new ConceptDescription(desc, Locale.ENGLISH));

                conceptService.saveConcept(conceptComplex);
            }
        }
        {
            final String name = AttachmentsConstants.MODULE_SHORT_ID + " IMAGE ATTACHMENT";
            final String desc = "Concept complex for 'image attachments with thumbnails' complex obs.";
            final String uuid = AttachmentsConstants.CONCEPT_IMAGE_UUID;

            Concept concept = conceptService.getConceptByUuid(uuid);

            if (concept != null && concept.getPreferredName(Locale.ENGLISH).getName().equals("VDUI PATIENT IMAGE")) {

                ConceptComplex conceptComplex = (ConceptComplex) concept;

                // set the new names, as well as update the handler
                conceptComplex.setHandler(ImageAttachmentHandler.class.getSimpleName());
                ConceptName conceptName = new ConceptName(name, Locale.ENGLISH);
                conceptComplex.setFullySpecifiedName(conceptName);
                conceptComplex.setPreferredName(conceptName);
                conceptComplex.setConceptClass(conceptService.getConceptClassByUuid(ConceptClass.QUESTION_UUID));
                conceptComplex.setDatatype(conceptService.getConceptDatatypeByUuid(ConceptDatatype.COMPLEX_UUID));
                conceptComplex.addDescription(new ConceptDescription(desc, Locale.ENGLISH));

                conceptService.saveConcept(conceptComplex);
            }
        }


    }
}

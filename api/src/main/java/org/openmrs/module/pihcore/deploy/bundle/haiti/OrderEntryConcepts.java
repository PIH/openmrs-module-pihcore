package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihConceptBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors1_10.orderFrequency;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class OrderEntryConcepts extends VersionedPihConceptBundle {

    @Autowired
    private OrderService orderService;

    public static final class Concepts {
        public static final String ROUTES_OF_ADMINISTRATION = "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String TIME_UNITS = "f1904502-319d-4681-9030-e642111e7ce2";
        public static final String DOSING_UNITS = "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String DISPENSING_UNITS = "162402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        public final static String EVERY_TWO_HOURS = "059ac2ba-ce52-41e5-bac5-46707bac285a";
        public final static String EVERY_THREE_HOURS = "f295e71a-0d7b-4a70-a193-2a6c3a73e9ed";
        public final static String EVERY_FOUR_HOURS = "4c443187-7db1-4678-81b5-83a0c4213cfa";
        public final static String EVERY_SIX_HOURS = "513e8901-7909-4ad3-9bea-1874ed9a6ba2";
        public final static String EVERY_TWELVE_HOURS = "894ef033-1135-4c52-8284-7de9b157e824";
        public final static String ONCE_A_DAY = "37328251-6759-4270-8a2e-8cab2c0b315b";
        public final static String TWICE_A_DAY = "586f1146-8737-4827-8cfa-3b72d71ebd8b";
        public final static String THREE_TIMES_A_DAY = "46a4d3d6-86d0-491f-be00-d151246caec3";
        public final static String FOUR_TIMES_A_DAY = "bb025463-8631-4475-952d-1c5c83d8aa32";
        public final static String FIVE_TIMES_A_DAY = "ee751fa5-1531-4722-ac64-f2e48898d989";
        public final static String SIX_TIMES_A_DAY = "b928c4d8-7b3f-40db-9a62-c98a3ba1c9aa";
        public final static String SEVEN_TIMES_A_DAY = "9b0068ac-4104-4bea-ba76-851e5faa9f2a";
        public final static String EIGHT_TIMES_A_DAY = "7cc0fa66-0467-4552-8dd7-501f2b325319";
        public final static String NINE_TIMES_A_DAY = "f28e1f49-2b56-4639-9312-fbe856fa0c51";
        public final static String IN_THE_MORNING = "28231877-bbf2-45bb-b6dd-3d4b083fdde1";
        public final static String AT_NIGHT = "023bee9c-2dbb-483c-8401-46f0ccf6b333";
        public final static String WHEN_REQUIRED = "160857AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public final static String IMMEDIATELY = "3cdc5794-26fe-102b-80cb-0017a47871b2";

        public final static String TABLET = "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public final static String CAPSULE = "1608AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() throws Exception {
        setGlobalProperty(OpenmrsConstants.GP_DRUG_ROUTES_CONCEPT_UUID, Concepts.ROUTES_OF_ADMINISTRATION);
        setGlobalProperty(OpenmrsConstants.GP_DURATION_UNITS_CONCEPT_UUID, Concepts.TIME_UNITS);
        setGlobalProperty(OpenmrsConstants.GP_DRUG_DOSING_UNITS_CONCEPT_UUID, Concepts.DOSING_UNITS);
        setGlobalProperty(OpenmrsConstants.GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID, Concepts.DISPENSING_UNITS);
    }

    @Override
    protected void installNewVersion() throws Exception {
        installOrderFrequencies();

        // concepts are currently included in the HUM Dispensing MDS package
    }

    private void installOrderFrequencies() {
        install(orderFrequency(Concepts.ONCE_A_DAY, 1.0, "fb8068a0-7647-4aec-93d0-14658533dfdf"));
        install(orderFrequency(Concepts.IN_THE_MORNING, 1.0, "682a53f3-5614-4e64-88bd-42855cff4be3"));
        install(orderFrequency(Concepts.AT_NIGHT, 1.0, "19c83062-7e1f-4d3f-8b97-335d089666bd"));
        install(orderFrequency(Concepts.TWICE_A_DAY, 2.0, "e08de81e-3bed-4471-a26f-180417d543d9"));
        install(orderFrequency(Concepts.THREE_TIMES_A_DAY, 3.0, "e51a7a00-7f29-410b-b8ab-2c3cd6a2a5ad"));
        install(orderFrequency(Concepts.FOUR_TIMES_A_DAY, 4.0, "89765cb8-4539-45e5-90bb-9ac8272f8972"));
        install(orderFrequency(Concepts.FIVE_TIMES_A_DAY, 5.0, "f96d8e96-754c-4858-980e-b53d7ff3fec1"));
        install(orderFrequency(Concepts.SIX_TIMES_A_DAY, 6.0, "bb3a1445-36c9-4fdb-a1bc-ca52a5c687a9"));
        install(orderFrequency(Concepts.SEVEN_TIMES_A_DAY, 7.0, "3a3c6594-a0c7-42c7-9027-b7ec2a4404e0"));
        install(orderFrequency(Concepts.EIGHT_TIMES_A_DAY, 8.0, "227e3c01-5343-448e-9611-4af1336b2e4e"));
        install(orderFrequency(Concepts.NINE_TIMES_A_DAY, 9.0, "902b2159-422d-4b3c-b71f-bda6ea5b7814"));
        install(orderFrequency(Concepts.EVERY_TWO_HOURS, 2d/24d, "63c4bafb-ae18-4ce6-9cd3-13c4b7a54e51"));
        install(orderFrequency(Concepts.EVERY_THREE_HOURS, 0.125, "a0667be8-f9c0-4b5b-afcd-6bf29c6497fe"));
        install(orderFrequency(Concepts.EVERY_FOUR_HOURS, 4d/24d, "34dbf28e-da5c-4c63-a7fd-451c9d33a59a"));
        install(orderFrequency(Concepts.EVERY_SIX_HOURS, 0.25, "26921f5f-2188-494e-af52-9373c54adca7"));
        install(orderFrequency(Concepts.EVERY_TWELVE_HOURS, 0.5, "0d99917c-ee7b-49e7-b55c-5d2457d9c40a"));
        install(orderFrequency(Concepts.WHEN_REQUIRED, null, "da558e86-3fbc-427a-8e4e-06d61c38d813"));
        install(orderFrequency(Concepts.IMMEDIATELY, null, "8487d0db-3378-49b8-b24c-18f121dfbfd0"));
    }

    /**
     * OrderFrequency is not allowed to be resaved after it has an order pointing to it, or it will throw the exception:
     * "org.openmrs.api.APIException: This order frequency cannot be edited because it is already in use"
     *
     * This method only calls the base install method if a field has actually changed, to prevent against spuriously
     * getting this error.
     *
     * @param orderFrequency
     * @return
     */
    private OrderFrequency install(OrderFrequency orderFrequency) {
        OrderFrequency existing = orderService.getOrderFrequencyByUuid(orderFrequency.getUuid());
        if (existing != null
                && existing.getConcept().getUuid().equals(orderFrequency.getConcept().getUuid())
                && OpenmrsUtil.nullSafeEquals(existing.getFrequencyPerDay(), orderFrequency.getFrequencyPerDay())) {
            return existing;
        }
        return super.install(orderFrequency);
    }

}

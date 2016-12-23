
<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->

<span class="obs-span">
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.pastMedicalHistoryConstruct">
        {{ obs | valueFirstGroupMember:[ Concepts.asthma, Concepts.heartDisease, Concepts.surgery, Concepts.traumaticInjury, Concepts.epilepsy, Concepts.haemoglobinopathy,
            Concepts.hypertension, Concepts.sexuallyTransmittedInfection, Concepts.congenitalMalformation, Concepts.malnutrition, Concepts.weightLoss,
            Concepts.measles, Concepts.tuberculosis, Concepts.varicella, Concepts.diphtheria, Concepts.acuteRheumaticFever, Concepts.diabetes,
            Concepts.prematureBirth, Concepts.otherNonCoded ] | omrsDisplay}}{{ (\$last && (encounter.obs | byConcept:Concepts.currentMedications).length == 0) ? "" : ", " }}
    </span>
    <span> {{ encounter.obs | byConcept:Concepts.currentMedications | omrsDisplay }}</span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />

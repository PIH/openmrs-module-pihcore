<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span ng-repeat="obs in encounter.obs | byConcept:Concepts.pastMedicalHistoryConstruct">
    {{ obs | valueGroupMember:Concepts.asthma | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.heartDisease | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.surgery | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.traumaticInjury | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.epilepsy | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.haemoglobinopathy | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.hypertension | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.sexuallyTransmittedInfection | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.congenitalMalformation | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.weightLoss | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.measles | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.tuberculosis | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.varicella | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.diphtheria | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.acuteRheumaticFever | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.diabetes | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.prematureBirth | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.otherNonCoded | omrsDisplay}}
    {{ \$last ? "" : "," }}
</span>
<span ng-show="(encounter.obs|byConcept:Concepts.pastMedicalHistoryConstruct).length > 0 && (encounter.obs | byConcept:Concepts.currentMedications).length > 0" >,&nbsp;</span>
<span> {{ encounter.obs | byConcept:Concepts.currentMedications | omrsDisplay }}</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />

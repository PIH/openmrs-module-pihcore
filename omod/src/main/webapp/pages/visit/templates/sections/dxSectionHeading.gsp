<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->

<!-- display primary diagnosis first -->
<span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
    {{ diag | diagnosisShort }}
    {{ \$last ? "" : "," }}
</span>

<!-- add a comma between diagnoses -->
<span ng-show="(encounter.obs|byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder).length > 0 && (encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder).length > 0" >,&nbsp;</span>

<span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
    {{ diag | diagnosisShort }}
    {{ \$last ? "" : "," }}
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
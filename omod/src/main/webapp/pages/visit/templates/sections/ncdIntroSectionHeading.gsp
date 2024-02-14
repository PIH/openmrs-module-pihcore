<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.familyPlanningVisit">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>

    <!-- add a comma between reason for visit and diagnoses -->
        <span ng-show="(encounter.obs | byConcept:Concepts.familyPlanningVisit).length > 0 && (encounter.obs | byConcept:Concepts.diagnosisConstruct).length > 0))" >--&nbsp;</span>

    <!-- display primary diagnosis first -->
    <span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisShort }}{{ \$last && (encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder).length == 0 ? "" : ", " }}
    </span>

    <span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisShort }}{{ \$last ? "" : "," }}
    </span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
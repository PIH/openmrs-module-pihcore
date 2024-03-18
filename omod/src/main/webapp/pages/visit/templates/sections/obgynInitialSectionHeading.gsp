<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.reasonForVisit">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>

    <!-- add a comma between reason for visit and family planning visit -->
    <span ng-show="(encounter.obs | byConcept:Concepts.reasonForVisit).length > 0 && (encounter.obs | byConcept:Concepts.familyPlanningVisit).length > 0" >--&nbsp;</span>

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.familyPlanningVisit">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>

    <!-- add a hyphen between reason for visit and visit type -->
    <span ng-show="(encounter.obs | byConcept:Concepts.reasonForVisit).length > 0 && (encounter.obs | byConcept:Concepts.visitType).length > 0" >--&nbsp;</span>

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.visitType">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
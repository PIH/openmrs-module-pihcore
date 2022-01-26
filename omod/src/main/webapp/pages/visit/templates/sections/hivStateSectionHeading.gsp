<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">

    <!-- ToDo:  Add current ARV drug name
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.medicationOrders">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>
    -->

    <!-- add character between ARV and WHO stage
    <span ng-show="(encounter.obs | byConcept:Concepts.medicationOrders).length > 0 && (encounter.obs | byConcept:Concepts.whoHIVStages).length > 0))" >;&nbsp;</span>
    -->

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.whoHIVStages">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>
</span>
<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
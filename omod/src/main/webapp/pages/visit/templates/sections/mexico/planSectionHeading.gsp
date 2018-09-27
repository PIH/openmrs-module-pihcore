<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.labTestOrderedNonCoded">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>

    <!-- add a comma between tests and medications -->
    <span ng-show="(encounter.obs|byConcept:Concepts.labTestOrderedNonCoded).length > 0 && (encounter.obs | byConcept:Concepts.prescriptionConstruct).length > 0" >--&nbsp;</span>

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.prescriptionConstruct">
        {{ (obs | groupMember:Concepts.medicationOrders).value.concept | omrsDisplay }}{{ \$last ? "" : "," }}
    </span>
</span>
<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
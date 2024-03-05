<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">
    <!-- For ZL when there's no obsgroup -->
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.ncdCategory">
        {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
    </span>

    <!-- For SL with obsgroup-->
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.ncdConditionSet ">
        {{ (obs | groupMember:Concepts.ncdCategory).value | omrsDisplay  }}{{ \$last ? "" : "," }}
    </span>

</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
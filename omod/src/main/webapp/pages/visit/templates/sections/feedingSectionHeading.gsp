<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span ng-repeat="obs in encounter.obs | byConcept:Concepts.feedingHistoryConstruct">
    {{ obs | valueGroupMember:Concepts.breastedExclusively | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.infantFormula | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.mixedFeeding | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.weaned | omrsDisplay}}
    {{ \$last ? "" : "," }}
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
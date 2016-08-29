<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span ng-repeat="obs in encounter.obs | byConcept:Concepts.supplementHistoryConstruct">
    {{ obs | valueGroupMember:Concepts.vitaminA | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.ferrousSulfate | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.iode | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.deworming | omrsDisplay}}
    {{ obs | valueGroupMember:Concepts.zinc | omrsDisplay}}
    {{ \$last ? "" : "," }}
</span>
<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
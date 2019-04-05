<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.feedingHistoryConstruct | withCodedMember:Concepts.feedingMethodPresent">
        {{ obs | valueFirstGroupMember: [ Concepts.breastedExclusively, Concepts.infantFormula, Concepts.mixedFeeding, Concepts.weaned ] | omrsDisplay }}{{ \$last ? "" : "," }}
    </span>
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.supplementHistoryConstruct">
        {{ obs | valueFirstGroupMember:[Concepts.vitaminA, Concepts.ferrousSulfate, Concepts.iode, Concepts.deworming, Concepts.zinc  ] | omrsDisplay }}{{ \$last ? "" : "," }}
    </span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
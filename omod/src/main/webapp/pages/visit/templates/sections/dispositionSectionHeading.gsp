<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->

<span>{{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }} </span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
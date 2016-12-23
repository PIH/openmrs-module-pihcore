<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.chiefComplaint">
        {{ obs |  obs:"value" | limitTo: 60 }}
        {{ (((obs |  obs:"value").length) > 60) ? ' ...' : '' }}
    </span>
</span>
<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
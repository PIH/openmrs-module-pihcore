
<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->

<span class="obs-span">
    <!-- add character between ARV and WHO stage -->
    <span ng-show="(encounter.obs | byConcept:Concepts.estimatedDueDate).length > 0" >{{ 'pihcore.dpa' | translate  }}:</span>

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.estimatedDueDate">
        {{ obs |  obs:"value" | serverDateLocalized:DatetimeFormats.dateLocalized }}{{ \$last ? "" : "," }}
    </span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />


<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->

<span class="obs-span">
    <span ng-show="(encounter.obs | byConcept:Concepts.estimatedDueDate).length > 0" >
        {{ 'pihcore.pregnancy.dueDate' | translate }}:
    </span>

    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.estimatedDueDate">
        {{ obs |  obs:"value" | serverDateLocalized:DatetimeFormats.dateLocalized }}{{ \$last ? "" : "," }}
    </span>

    <!-- add a comma between adverse event and tb site -->
    <span ng-show="(encounter.obs | byConcept:Concepts.adverseReaction).length > 0 && (encounter.obs | byConcept:Concepts.estimatedDueDate).length > 0" >;</span>

    <span ng-show="(encounter.obs | byConcept:Concepts.adverseReaction).length > 0" >
        {{ 'pihcore.adverseEvent' | translate }}
    </span>
</span>

<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />

<div class="header">
    <span class="one-third" ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="one-half details">
            <span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
                {{ diag | admissionShort }}
            </span>
    </span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
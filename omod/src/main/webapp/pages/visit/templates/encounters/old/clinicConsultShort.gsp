<div class="header">
    <span ng-include="'templates/encounters/defaultEncounterHeading.page'"></span>
    <span class="one-third details">
        <span>
            ${ ui.message('coreapps.clinicianfacing.diagnosis') }:
            <span ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
                {{ diag | diagnosisShort }}
            </span>
        </span>
        <span>
            ${ ui.message('coreapps.consult.disposition') }:
                {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionShort }}
        </span>
    </span>
    <span class="overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></span>
</div>
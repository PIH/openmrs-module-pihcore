<div class="header">
    <span ng-include="'templates/standardEncounterHeading.page'"></span>
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
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
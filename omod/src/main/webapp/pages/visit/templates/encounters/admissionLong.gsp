<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content">
    <h5>${ ui.message('coreapps.clinicianfacing.diagnoses') }:</h5>
    <div ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisLong }}
    </div>

    <div ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisLong }}
    </div>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
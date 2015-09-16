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

    <div ng-show="encounter.orders && encounter.orders.length">
        <h5>${ ui.message('pihcore.orders') }:</h5>
        <p ng-repeat="order in encounter.orders | orderBy:'dateActivated'" class="aligned">
            {{ order.dateActivated | serverDate:DatetimeFormats.date  }}
            {{ order | orderInstructions }}
        </p>
    </div>

    <div>
        <h5>${ ui.message('coreapps.consult.disposition') }:</h5>
        {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }}
    </div>

    <div>
        <h5>${ ui.message('pihcore.consult.returnVisitDate') }:</h5>
        {{ encounter.obs | byConcept:Concepts.returnVisitDate:true | obs:"value" | serverDate:DatetimeFormats.date }}
    </div>

    <div>
        <h5>${ ui.message('pihcore.consult.clinicalImpressions') }:</h5>
        {{ encounter.obs | byConcept:Concepts.clinicalImpressions:true | obs:"value" }}
    </div>


    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
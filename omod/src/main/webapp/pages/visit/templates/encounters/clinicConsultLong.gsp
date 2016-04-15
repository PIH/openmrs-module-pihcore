<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<!--encounter-summary-long class currently just used for selenium tests -->
<div class="content encounter-summary-long">
    <h6>${ ui.message('coreapps.clinicianfacing.diagnoses') }</h6>
    <div class="diagnosisLongClass" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisLong }}
    </div>

    <div class="diagnosisLongClass" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        {{ diag | diagnosisLong }}
    </div>

    <div ng-show="encounter.orders && encounter.orders.length">
        <h6>${ ui.message('pihcore.orders') }</h6>
        <p ng-repeat="order in encounter.orders | orderBy:'dateActivated'" class="aligned">
            {{ order.dateActivated | serverDate:DatetimeFormats.date  }}
            <!-- {{ order | orderInstructions }} (this no longer works with order functionality removed)-->
        </p>
    </div>

    <div>
        <h6>${ ui.message('pihcore.consult.clinicalImpressions') }</h6>
        {{ encounter.obs | byConcept:Concepts.clinicalImpressions:true | obs:"value" }}
    </div>


    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
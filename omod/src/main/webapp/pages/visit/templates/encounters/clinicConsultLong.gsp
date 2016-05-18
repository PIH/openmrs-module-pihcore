<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<!--encounter-summary-long class currently just used for selenium tests -->
<div class="content encounter-summary-long">
    <p class="aligned" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        <label><span ng-show="\$first">${ ui.message('coreapps.clinicianfacing.diagnoses') }</span></label>
        {{ diag | diagnosisLong }}
    </p>

    <p class="aligned" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
        <label></label>
        {{ diag | diagnosisLong }}
    </p>

    <!-- TODO: make sure this looks okay with radiology orders? -->
    <p class="aligned" ng-show="encounter.orders && encounter.orders.length">
        <label>${ ui.message('pihcore.orders') }</label>
        <p ng-repeat="order in encounter.orders | orderBy:'dateActivated'" class="aligned">
            {{ order.dateActivated | serverDate:DatetimeFormats.date  }}
            <!-- {{ order | orderInstructions }} (this no longer works with order functionality removed)-->
        </p>
    </p>

    <p class="aligned">
        <label>${ ui.message('coreapps.consult.disposition') }</label>
        {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }}
    </p>


    <p class="aligned">
        <label>${ ui.message('pihcore.consult.returnVisitDate') }</label>
            {{ encounter.obs | byConcept:Concepts.returnVisitDate:true | obs:"value" | serverDate:DatetimeFormats.date }}
    </p>

    <p class="aligned">
        <label>${ ui.message('pihcore.consult.clinicalImpressions') }</label>
        <p>{{ encounter.obs | byConcept:Concepts.clinicalImpressions:true | obs:"value" }}</p>
    </p>


    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
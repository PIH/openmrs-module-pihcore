<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->

    <p class="aligned">
        <label>${ui.message("labtrackingapp.clinicalhistorylabel")}</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.pastMedicalHistoryComment:true | obs:"value"}}</span>
    </p>

    <p ng-repeat="obs in encounter.obs | byConcepts:[Concepts.procedurePerformed, Concepts.procedurePerformedNonCoded]" class="aligned">
        <label>
            <span ng-show="\$first">
                ${ui.message("labtrackingapp.proceduresitelabel")}
            </span>
        </label>
        <span class="value">{{ obs | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.sampledatelabel")}</label>
        <span class="value">{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.locationlabel")}</label>
        <span class="value">{{ encounter.location | omrsDisplay }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.postopdiagnosislabel")}</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.postPathologyDiagnosisConstruct:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label> ${ui.message("labtrackingapp.orderdetails.specimandetailslabel")}</label>
        <span class="value">1. {{ encounter.obs | byConcept:Concepts.specimenOneComment:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label></label>
        <span class="value">2. {{ encounter.obs | byConcept:Concepts.specimenTwoComment:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label></label>
        <span class="value">3. {{ encounter.obs | byConcept:Concepts.specimenThreeComment:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label></label>
        <span class="value">4. {{ encounter.obs | byConcept:Concepts.specimenFourComment:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.attendingsurgeonlabel")}</label>
        <span class="value">{{ encounter.encounterProviders | encounterRole:EncounterRoles.attendingSurgeon.uuid | getProviderName }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.residentlabel")}</label>
        <span class="value">{{ encounter.encounterProviders | encounterRole:EncounterRoles.assistingSurgeon.uuid | getProviderName }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.urgentreviewlabel")}</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.urgentReview:true | obs:'value' }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.resultsdatelabel")}</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.testResultsDate:true | obs:'value' | serverDate:DatetimeFormats.date  }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.noteslabel")}</label>
        <span class="value">{{ encounter.obs | byConcept:Concepts.specimenResultsComment:true | obs:'value' }}</span>
    </p>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>

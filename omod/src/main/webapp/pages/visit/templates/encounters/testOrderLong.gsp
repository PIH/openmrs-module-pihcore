<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->

    <p class="aligned">
        <label>${ ui.message("coreapps.patientDashBoard.orderNumber")}</label>
        <span class="value">{{ encounter.orders[0].orderNumber }}</span>
    </p>

    <p ng-repeat="obs in encounter.obs | byConcepts:[Concepts.procedureOrdered, Concepts.procedureOrderedNonCoded]" class="aligned">
        <label>
            <span ng-show="\$first">
                ${ui.message("labtrackingapp.proceduresitelabel")}
            </span>
        </label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.preoathologydiagnosislabel")}</label>
        <span class="value">{{ encounter.orders[0].orderReason.display }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.instructionslabel")}</label>
        <span class="value">{{ encounter.orders[0].instructions }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("labtrackingapp.clinicalhistorylabel")}</label>
        <span class="value">{{ encounter.orders[0].clinicalHistory }}</span>
    </p>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
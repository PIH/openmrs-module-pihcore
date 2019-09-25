<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'"></div>

<div ng-repeat="ord in encounter.orders" class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->

    <p class="aligned">
        <label>${ ui.message("coreapps.patientDashBoard.orderNumber")}</label>
        <span class="value">{{ ord.orderNumber }}</span>
    </p>

    <p class="aligned">
        <label>${ui.message("pihcore.test")}</label>
        <span class="value">{{ ord.concept.display }}</span>
    </p>

    <p ng-show="ord.orderReason.display" class="aligned">
        <label>${ui.message("labtrackingapp.orderdetails.preoathologydiagnosislabel")}</label>
        <span class="value">{{ ord.orderReason.display }}</span>
    </p>

    <p ng-show="ord.instructions" class="aligned">
        <label>${ui.message("labtrackingapp.instructionslabel")}</label>
        <span class="value">{{ ord.instructions }}</span>
    </p>

    <p ng-show="ord.clinicalHistory" class="aligned">
        <label>${ui.message("labtrackingapp.clinicalhistorylabel")}</label>
        <span class="value">{{ ord.clinicalHistory }}</span>
    </p>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
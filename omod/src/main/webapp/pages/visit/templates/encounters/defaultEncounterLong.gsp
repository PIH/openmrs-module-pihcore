<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept | omrsDisplay }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
    <div ng-show="encounter.orders && encounter.orders.length">
        <span ng-repeat="order in encounter.orders">
            <p class="aligned">
                <label>${ ui.message("coreapps.patientDashBoard.orderNumber")}</label>
                <span class="value">{{ order.orderNumber }}</span>
            </p>
            <p class="aligned">
                <label>${ ui.message("coreapps.patientDashBoard.order")}</label>
                <span class="value">{{ order.display }}</span>
            </p>
            <p class="aligned">
                <label>${ ui.message("radiologyapp.order.timing")}</label>
                <span>{{ (order.urgency == 'ROUTINE') ? ('radiologyapp.order.timing.routine' | translate) : ('radiologyapp.order.timing.urgent' | translate) }}</span>
            </p>
        </span>
    </div>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
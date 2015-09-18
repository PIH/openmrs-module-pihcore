<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept | omrs.display }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
    <div ng-show="encounter.orders && encounter.orders.length">
        <h4>${ ui.message("uicommons.orders") }</h4>
        <p ng-repeat="order in encounter.orders" class="aligned">
            <small>${ ui.message("coreapps.patientDashBoard.orderNumber")}</small>
            {{ order.orderNumber }}<br>
            <small>${ ui.message("coreapps.patientDashBoard.order")}</small>
            {{ order.display }}<br>
        </p>
    </div>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
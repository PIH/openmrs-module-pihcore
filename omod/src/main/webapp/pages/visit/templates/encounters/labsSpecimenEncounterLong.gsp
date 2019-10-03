<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->
    <p ng-repeat="obs in encounter.obs | byConcept:Concepts.testOrderNumber " class="aligned">
        <label>{{ obs.concept | omrsDisplay }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
    <p class="aligned">
        <label></label>
        <span>
            <button ng-click="goToLabResults()">
                ${ ui.message("pihcore.viewLabs.overallAction.label")}
            </button>
        </span>

    </p>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>

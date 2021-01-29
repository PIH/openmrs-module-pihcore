<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">  <!-- encounter-summary-long currently only used for Selenium tests -->
    <p class="bold">
        ${ ui.message("pihcore.hiv.legacyDataNotMigrated") }
    </p>
    <p ng-repeat="obs in encounter.obs" class="aligned">
        <label>{{ obs.concept | omrsDisplay }}</label>
        <span class="value">{{ obs | obs:"value" }}</span>
    </p>
    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>

<!-- a template to display the "view" version of an html form provided by the htmlfromentryui getAsHtml method -->

<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

    <div class="content encounter-summary-long" ng-bind-html="html">  <!-- encounter-summary-long currently only used for Selenium tests -->

    </div>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
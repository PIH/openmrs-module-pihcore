<!-- a template to display the "view" version of an html form provided by the htmlfromentryui getAsHtml method -->

<style>

    htmlform legend {
        display: none;
    }

    htmlform fieldset {
        border: 0px;
        margin: 0px;
        padding: 0px;
        -webkit-column-break-inside: avoid;
        page-break-inside: avoid;
        break-inside: avoid;
    }

    htmlform h3 {
        margin: 2px 0px;
        font-size: 1em;
    }

    htmlform section {
        -webkit-column-count: 3; /* Chrome, Safari, Opera */
        -moz-column-count: 3; /* Firefox */
        column-count: 3;
    }

    htmlform .title {
        display: none;
    }

    htmlform .encounter-date {
        display: none;
    }

    htmlform .encounter-provider {
        display: none;
    }

    htmlform .encounter-location {
        display: none;
    }

</style>


<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">

    <div  ng-bind-html="html">  <!-- encounter-summary-long currently only used for Selenium tests -->

    </div>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
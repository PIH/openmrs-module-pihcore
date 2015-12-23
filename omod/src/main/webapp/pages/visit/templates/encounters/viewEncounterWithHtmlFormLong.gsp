<!-- a template to display the "view" version of an html form provided by the htmlfromentryui getAsHtml method
     specifically designed to reformat our "one-question-per-screen" forms to should line-by-line obs -->

<style>

    htmlform legend {
        display: none;
    }

    htmlform fieldset {
        margin: 0px;
        border: 0px;
        padding: 0px;
    }

    htmlform h3 {
        font-size: 1em;
        width: 200px;
        display: inline-block;
        margin: 0px;
        margin-right: 10px;
        color: #501d3d;
        vertical-align: top;
    }

    htmlform p {
        vertical-align: top;
        display: inline-block;
        float: none;
    }

    htmlform .left {
        vertical-align: top;
        display: inline-block;
        float: none;
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
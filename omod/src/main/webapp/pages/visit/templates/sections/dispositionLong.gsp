
<style>

.disposition-header {
    font-size: 1em;
    display: inline-block;
    width: 200px;
    margin: 0px;
    margin-right: 10px;
    color: #501d3d;
    vertical-align: top;
}

.left-column {
    float:left;
    width:210px;
}

.right-column {
    margin-left: 210px;
}

.clear {
    clear:both;
}

</style>

<div class="header" ng-include="'templates/sections/defaultSectionHeader.page'">
</div>

<!--encounter-summary-long class currently just used for selenium tests -->
<div class="content encounter-summary-long">

    <div>
        <div class="left-column">
            <h3 class="disposition-header">${ ui.message('coreapps.consult.disposition') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }}
            </p>
        </div>
    </div>

    <div class="clear">
        <div class="left-column">
            <h3 class="disposition-header">${ ui.message('pihcore.comment') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.dispositionComment:true | obs:"value" }}
            </p>
        </div>
    </div>

    <div class="clear">
        <div class="left-column">
            <h3 class="disposition-header">${ ui.message('pihcore.consult.returnVisitDate') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.returnVisitDate:true | obs:"value" | serverDate:DatetimeFormats.date }}
            </p>
        </div>
    </div>


    <div class="clear book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
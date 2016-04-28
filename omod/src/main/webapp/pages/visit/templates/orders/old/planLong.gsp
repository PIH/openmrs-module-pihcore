
<style>

.plan-header {
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

.book-keeping, .comments-div {
    clear:both;
}

</style>

<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<!--encounter-summary-long class currently just used for selenium tests -->
<div class="content encounter-summary-long">


    <div>
        <div class="left-column">
            <h3 class="plan-header">${ ui.message('pihcore.consult.clinicalManagementPlan') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.clinicalManagementPlanComment:true | obs:"value" }}
            </p>
        </div>
    </div>

    <div ng-show="encounter.orders && encounter.orders.length">
        <div class="left-column">
            <h3 class="plan-header">${ ui.message('pihcore.orders') }</h3>
        </div>
        <div class="right-column">
            <p ng-repeat="order in encounter.orders | orderBy:'dateActivated'" class="aligned">
                {{ order.display }}
            </p>
        </div>
    </div>

    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
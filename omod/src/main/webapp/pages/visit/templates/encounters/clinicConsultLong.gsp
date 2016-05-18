
<style>

.consult-header {
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

.book-keeping, .comments-div, .clear {
    clear:both;
}

</style>

<div class="header" ng-include="'templates/encounters/defaultEncounterHeader.page'">
</div>

<!--encounter-summary-long class currently just used for selenium tests -->
<div class="content encounter-summary-long">

    <div>
        <div class="left-column">
            <h3 class="consult-header">${ ui.message('coreapps.clinicianfacing.diagnoses') }</h3>
        </div>
        <div class="right-column">
            <p class="diagnosis" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
                {{ diag | diagnosisLong }}
            </p>
            <p class="diagnosis" ng-repeat="diag in encounter.obs | byConcept:Concepts.diagnosisConstruct | withoutCodedMember:Concepts.diagnosisOrder:Concepts.primaryOrder">
                {{ diag | diagnosisLong }}
            </p>
        </div>
    </div>

    <div>
        <div class="left-column">
            <h3 class="consult-header">${ ui.message('coreapps.consult.disposition') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }}
            </p>
        </div>
    </div>


    <div class="clear">
        <div class="left-column">
            <h3 class="consult-header">${ ui.message('pihcore.consult.returnVisitDate') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.returnVisitDate:true | obs:"value" | serverDate:DatetimeFormats.date }}
            </p>
        </div>
    </div>
    
    <div class="comments-div">
        <div class="left-column">
            <h3 class="consult-header">${ ui.message('emr.consult.freeTextComments') }</h3>
        </div>
        <div class="right-column">
            <p>
                {{ encounter.obs | byConcept:Concepts.clinicalImpressions:true | obs:"value" }}
            </p>
        </div>
    </div>


    <div class="book-keeping" ng-include="'templates/encounters/defaultEncounterBookkeeping.page'"></div>
</div>
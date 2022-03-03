
<div ng-hide="visit"><i class="icon-spinner icon-spin icon-2x"></i></div>

<div ng-show="visit">

    <div class="row">
        <!-- note that this filters out to just the encounter that matches "encounterUuid" -->
        <div ng-repeat="encounter in visit.encounters | filter:{voided:false, uuid: encounterUuid}" class="col-12">
            <encounter encounter="encounter" visit="visit" selected="true" encounter-date-format="encounterDateFormat" country="country" site="site"></encounter>
        </div>
    </div>

</div>

<div>
    <input id="return-to-forms-list" ng-click="returnToEncounterList()" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>

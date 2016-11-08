
<span ng-show="visit">

    <div id="visit-details">
        <visit-details visit="visit"></visit-details>
    </div>

    <div ng-repeat="encounter in visit.encounters | filter:{voided:false}">
        <encounter encounter="encounter" visit="visit" selected="encounter.uuid == encounterUuid" encounter-date-format="encounterDateFormat" expand-on-load="true"></encounter>
    </div>

</span>


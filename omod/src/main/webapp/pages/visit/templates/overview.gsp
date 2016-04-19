
<div ng-hide="visit"><i class="icon-spinner icon-spin icon-2x"></i></div>

<span ng-show="visit">

    <div id="visit-details">
        <visit-details visit="visit"></visit-details>
    </div>

    <div id="print-actions">
        <button id="print-button" ng-click="print()" ng-disabled="printButtonDisabled" title="${ ui.message('pihcore.visitNote.print') }"><i class="icon-print"/></button>
    </div>

    <div id="expand-all" ng-show="!allExpanded">
        <button id="expand-all-button" ng-click="expandAll()" title="${ ui.message('pihcore.visitNote.expandAll') }"><i class="icon-double-angle-down"/></button>
    </div>

    <div id="contract-all" ng-show="allExpanded">
        <button id="contract-all-button" ng-click="contractAll()" title="${ ui.message('pihcore.visitNote.collapseAll') }"><i class="icon-double-angle-up"/></button>
    </div>

    <div id="visit-actions">
        <visit-actions-dropdown></visit-actions-dropdown>
    </div>

    <div id="choose-another-visit">
        <visit-list-dropdown></visit-list-dropdown>
    </div>

    <div ng-repeat="encounter in visit.encounters | filter:{voided:false}">
        <encounter encounter="encounter" visit="visit" show-sections="encounter.uuid == encounterUuid" encounter-date-format="encounterDateFormat"></encounter>
    </div>

</span>


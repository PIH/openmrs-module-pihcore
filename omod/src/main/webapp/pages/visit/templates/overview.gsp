
<div ng-hide="visit"><i class="icon-spinner icon-spin icon-2x"></i></div>

<div ng-show="visit">

    <div class="row justify-content-between">

        <div id="visit-details" class="col-6">
            <visit-details visit="visit"></visit-details>
        </div>

        <div id="visit-action-buttons" class="col-6 text-right">

            <span id="contract-all" ng-show="allExpanded">
                <button id="contract-all-button" ng-click="contractAll()" title="${ ui.message('pihcore.visitNote.collapseAll') }"><i class="icon-double-angle-up"/></button>
            </span>

            <span id="choose-another-visit">
                <visit-list-dropdown></visit-list-dropdown>
            </span>

            <span id="visit-actions">
                <visit-actions-dropdown></visit-actions-dropdown>
            </span>

            <span id="expand-all" ng-show="!allExpanded">
                <button id="expand-all-button" ng-click="expandAll()" title="${ ui.message('pihcore.visitNote.expandAll') }"><i class="icon-double-angle-down"/></button>
            </span>

            <span id="print-actions">
                <button id="print-button" ng-click="print()" ng-disabled="printButtonDisabled" title="${ ui.message('pihcore.visitNote.print') }"><i class="icon-print"/></button>
            </span>

        </div>

    </div>

    <div class="row">
        <div ng-repeat="visitEvent in visit.visitEvents" class="col-12">
            <div ng-switch on="visitEvent.eventType">
                <div ng-switch-when="queueEntry">
                    <div class="visit-element">
                        <div class="header queue-entry">
                            <queue-entry queue-entry="visitEvent" visit="visit" selected="false" queue-entry-date-format="encounterDateFormat"></queue-entry>
                        </div>
                    </div>
                </div>
                <div ng-switch-when="encounter">
                    <encounter class="encounterType-{{visitEvent.encounterType.uuid}}" encounter="visitEvent" visit="visit" selected="visitEvent.uuid == encounterUuid" encounter-date-format="encounterDateFormat" country="country" site="site"></encounter>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <visit-actions></visit-actions>
    </div>

</div>




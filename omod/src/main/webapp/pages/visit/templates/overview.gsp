
<div id="visit-type">
    <choose-visit-template></choose-visit-template>
</div>

<div id="visit-details">
    <visit-details visit="visit"></visit-details>
</div>

<div id="expand-all" ng-show="!allExpanded">
    <button id="expand-all-button" ng-click="expandAll()" title="${ ui.message('pihcore.visitNote.expandAll') }"><i class="icon-double-angle-up"/></button>
</div>

<div id="contract-all" ng-show="allExpanded">
    <button id="contract-all-button" ng-click="contractAll()" title="${ ui.message('pihcore.visitNote.collapseAll') }"><i class="icon-double-angle-down"/></button>
</div>

<div id="visit-actions">
    <visit-actions-dropdown></visit-actions-dropdown>
</div>

<div id="choose-another-visit">
    <visit-list-dropdown></visit-list-dropdown>
</div>

<div ng-repeat="element in visitTemplate.elements">
    <display-element></display-element>
</div>

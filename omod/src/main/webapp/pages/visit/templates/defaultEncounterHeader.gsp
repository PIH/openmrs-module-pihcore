<div ng-show="canExpand()" ng-click="expand()">
    <span ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
<div ng-show="canContract()" ng-click="contract()">
    <span ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
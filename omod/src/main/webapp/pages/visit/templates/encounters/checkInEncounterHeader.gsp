<span class="ninety-percent" ng-show="canExpand()==null" ng-include="'templates/encounters/checkInEncounterHeading.page'"></span>
<span class="selectable ninety-percent" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/encounters/checkInEncounterHeading.page'"></span>
<span class="selectable ninety-percent" ng-show="canContract()" ng-click="contract()" ng-include="'templates/encounters/checkInEncounterHeading.page'"></span>
<span class="overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></span>

<span class="ninety-percent" ng-show="canExpand()==null" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="show-pointer ninety-percent" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="show-pointer ninety-percent" ng-show="canContract()" ng-click="contract()" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>

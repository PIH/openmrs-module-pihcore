<span ng-show="canExpand()==null" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="show-pointer" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="show-pointer" ng-show="canContract()" ng-click="contract()" ng-include="'templates/standardEncounterHeading.page'"></span>
<span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>

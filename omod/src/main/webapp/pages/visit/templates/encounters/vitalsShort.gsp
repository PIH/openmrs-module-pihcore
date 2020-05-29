<div class="header">
    <span ng-show="canExpand()==null" ng-include="'templates/encounters/vitalsEncounterHeading.page'"></span>
    <span class="selectable" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/encounters/vitalsEncounterHeading.page'"></span>
    <span class="selectable" ng-show="canContract()" ng-click="contract()" ng-include="'templates/encounters/vitalsEncounterHeading.page'"></span>
</div>

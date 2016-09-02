<!-- view with Html Form is our default (and currently only) section long view -->
<div class="header">
    <span class="ninety-percent" ng-show="canExpand()==null" ng-include="'templates/sections/defaultSectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/sections/defaultSectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canContract()" ng-click="contract()" ng-include="'templates/sections/defaultSectionHeading.page'"></span>
    <span class="overall-actions" ng-include="'templates/sections/defaultPlanSectionActions.page'"></span>
</div>

<div class="content encounter-summary-long">

    <div  ng-bind-html="html">  <!-- encounter-summary-long currently only used for Selenium tests -->

    </div>

</div>
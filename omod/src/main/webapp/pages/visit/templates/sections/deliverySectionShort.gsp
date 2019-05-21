<div class="header" id="{{section.id}}">
    <span class="ninety-percent" ng-show="canExpand()==null" ng-include="'templates/sections/deliverySectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/sections/deliverySectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canContract()" ng-click="contract()" ng-include="'templates/sections/deliverySectionHeading.page'"></span>
    <span class="overall-actions" ng-include="'templates/sections/defaultSectionActions.page'"></span>
</div>
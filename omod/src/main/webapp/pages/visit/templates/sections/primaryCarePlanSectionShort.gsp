<div class="header" id="{{section.id}}">
    <span class="ninety-percent" ng-show="canExpand()==null" ng-include="'templates/sections/primaryCarePlanSectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canExpand()" ng-click="expand()" ng-include="'templates/sections/primaryCarePlanSectionHeading.page'"></span>
    <span class="selectable ninety-percent" ng-show="canContract()" ng-click="contract()" ng-include="'templates/sections/primaryCarePlanSectionHeading.page'"></span>
    <span class="overall-actions">
        <a class="expand-encounter" ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="canContract()" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a class="edit-encounter" ng-show="canEdit()" ng-click="edit()"><i class="icon-pencil"></i></a>
        <a class="edit-encounter" ng-show="!canEdit()"><i class="icon-delete-blank"></i></a>
        <a class="delete-encounter" ng-show="(encounter.obs | byConcept:Concepts.prescriptionConstruct).length > 0" ng-click="printPrescriptions()"><i class="icon-print"></i></a>
        <a class="delete-encounter" ng-show="(encounter.obs | byConcept:Concepts.prescriptionConstruct).length < 1"><i class="icon-delete-blank"></i></a>
    </span>
</div>
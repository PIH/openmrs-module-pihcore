<div class="pull-right">
    <a class="expand-encounter" ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-right"></i></a>
    <a class="contract-encounter" ng-show="canContract()" ng-click="contract()"><i class="icon-caret-down"></i></a>
    <a class="edit-encounter" ng-show="canEdit()" ng-click="edit()"><i class="icon-pencil"></i></a>
    <a class="edit-encounter" ng-show="!canEdit()"><i class="icon-delete-blank"></i></a>
    <a class="delete-encounter" ng-show="(encounter.obs | byConcept:Concepts.prescriptionConstruct).length > 0"
       ng-click="printPrescriptions()"><i class="icon-print"
                                          title="${ui.message('pihcore.visitNote.printPrescriptions')}"></i></a>
    <a class="delete-encounter" ng-show="(encounter.obs | byConcept:Concepts.prescriptionConstruct).length < 1"><i
            class="icon-delete-blank"></i></a>
</div>
<a ng-show="canExpand()" ng-click="expand()">${ ui.message("pihcore.visitNote.showDetails") } <i class="icon-caret-right"></i></a>
<a ng-show="canContract()" ng-click="contract()">${ ui.message("pihcore.visitNote.hideDetails") } <i class="icon-caret-down"></i></a>
<a ng-show="canEdit()" ng-click="edit()"><i class="icon-pencil"></i></a>
<a ng-show="canDelete()" ng-click="delete()"><i class="icon-remove"></i></a>
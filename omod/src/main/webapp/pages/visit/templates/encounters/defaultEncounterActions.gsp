<a class="expand-encounter" ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-right"></i></a>
<a class="contract-encounter" ng-show="canContract()" ng-click="contract()"><i class="icon-caret-down"></i></a>
<a class="view-encounter" ng-show="canView()" ng-click="view()"><i class="icon-caret-right"></i></a>
<a class="edit-encounter" ng-show="canEdit()" ng-click="edit()"><i class="icon-pencil"></i></a>
<a class="edit-encounter" ng-show="!canEdit()"><i class="icon-delete-blank"></i></a>
<a class="delete-encounter" ng-show="canDelete()" ng-click="delete()"><i class="icon-remove"></i></a>
<a class="delete-encounter" ng-show="!canDelete()" ng-click="delete()"><i class="icon-delete-blank"></i></a>
<!-- classes only used for smoke tests at this point -->
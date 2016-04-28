<div class="selectable header" ng-click="">
    <span class="selectable ninety-percent">
        <span class="title encounter-name encounter-span">
            <i class="icon-list-ol"></i>
            <span class="title">${ ui.message("pihcore.visitNote.plan") }</span>
        </span>
        <span>

        </span>
    </span>
    <span class="overall-actions">
        <a class="expand-encounter" ng-show="!showAlergiesDetails" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="showAlergiesDetails" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a ui-sref="editPlan"><i class="icon-pencil"></i></a>
        <a><i class="icon-delete-blank"></i></a>

    </span>
</div>
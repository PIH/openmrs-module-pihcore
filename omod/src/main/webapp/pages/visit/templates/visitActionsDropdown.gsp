
<span dropdown dropdown-append-to-body>
    <button dropdown-toggle>${ ui.message("pihcore.visitNote.visitActions") } <i class="icon-caret-down"></i></button>
    <ul id="visit-actions-dropdown" class="dropdown-menu" aria-labelledby="visit-actions-dropdown">
        <li class="list-element" ng-repeat="action in visitActions | allowedWithContext:visit" ng-click="visitAction(action)">
            <span><i class="{{ action.icon }}"></i>{{ action.label | translate }}</span>
        </li>
    </ul>
</span>


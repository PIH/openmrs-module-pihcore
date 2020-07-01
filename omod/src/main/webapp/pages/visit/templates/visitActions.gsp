<div class="visit-actions">
    <a ng-repeat="action in visitActions | allowedWithContext:visit" class="button task" ng-click="visitAction(action)" id="{{ action.uuid }}">
        <i class="{{ action.icon }}"></i>
        {{ action.label | translate }}
    </a>
</div>

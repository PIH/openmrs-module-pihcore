<div class="header">
    <span ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="details">
        <span>
            <div ng-show="encounter.orders && encounter.orders.length">
                <span ng-repeat="order in encounter.orders">
                    {{ order.display }}
                </span>
            </div>
        </span>
    </span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
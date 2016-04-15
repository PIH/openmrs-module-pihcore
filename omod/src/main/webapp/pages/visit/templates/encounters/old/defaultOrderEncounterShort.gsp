<div class="header">
    <span ng-include="'templates/encounters/defaultEncounterHeading.page'"></span>
    <span class="details">
        <span>
            <div ng-show="encounter.orders && encounter.orders.length">
                <span ng-repeat="order in encounter.orders">
                    {{ order.display }}
                </span>
            </div>
        </span>
    </span>
    <span class="overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></span>
</div>
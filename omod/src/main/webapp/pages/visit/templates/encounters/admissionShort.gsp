<div class="header">
    <span class="one-third" ng-include="'templates/standardEncounterHeading.page'"></span>
    <span class="one-half details">
        <span class="one-half admission-location">
            {{encounter.location | omrs.display }}
        </span>
    </span>
    <span class="overall-actions" ng-include="'templates/standardEncounterActions.page'"></span>
</div>
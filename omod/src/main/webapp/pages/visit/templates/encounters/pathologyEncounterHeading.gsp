<div class="row justify-content-between">
    <div class="encounter-type encounter-name col-8">
        <i ng-show="icon" class="{{ icon }}"></i> {{ encounter.encounterType.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span">
            <i class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.dateLocalized }}
        </span>
        <span class="time-span">
            <i class="icon-time"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.timeLocalized }}
        </span>
    </div>
</div>
<div class="row justify-content-between">
    <div class="col-8">
        <span class="encounter-card-summary provider-and-location-span encounter-provider"
              ng-show="encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName">
            {{ 'coreapps.by' | translate }}
            {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}
        </span>
        <span class="encounter-card-summary admission-location provider-and-location-span"
              ng-hide="encounter.location.uuid == encounter.visit.location.uuid">
            ${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}
        </span>
    </div>
    <div class="col-4 text-right overall-actions">
        <a class="expand-encounter" ng-show="canExpand()" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="canContract()" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a class="view-encounter" ng-show="canView()" ng-click="view()"><i class="icon-caret-right"></i></a>
        <a class="edit-encounter" ng-show="canEdit()" ng-click="edit()"><i class="icon-pencil"></i></a>
    </div>
</div>

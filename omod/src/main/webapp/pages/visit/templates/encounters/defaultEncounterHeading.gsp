<div class="row justify-content-between">
    <div class="encounter-type encounter-name col-8">
        <i ng-show="icon" class="{{ icon }}"></i> {{ encounter.encounterType.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span">
            <i class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}
        </span>
        <span class="time-span">
            <i class="icon-time"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.time }}
        </span>
    </div>
</div>
<div class="row justify-content-between">
    <div class="col-8">
        <span class="encounter-card-summary provider-and-location-span encounter-provider">
            {{ 'emr.by' | translate }}
            {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}
        </span>
        <span class="encounter-card-summary admission-location provider-and-location-span"
              ng-hide="encounter.location.uuid == encounter.visit.location.uuid">
            ${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}
        </span>
    </div>
    <div class="col-4 text-right overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></div>
</div>

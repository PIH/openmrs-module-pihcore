<span class="date-span">
    <i ng-show="encounter.encounterDatetime" class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}
</span>
<span class="time-span">
    <i ng-show="encounter.encounterDatetime" class="icon-time"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.time }}
</span>
<span class="encounter-provider provider-and-location-span">
    <span ng-show="encounter.encounterProviders">{{ 'emr.by' | translate }}</span>
    {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}
</span>
<span class="encounter-provider admission-location provider-and-location-span" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">
    ${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}
</span>

<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert" ></i>
</span> <!-- encounter-type class added for smoke tests -->
<span ng-show="showEncounterDetails">
    <span class="date-span">
        <i ng-show="encounter.encounterDatetime" class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.dateLocalized }}
    </span>
    <span class="time-span">
        <i ng-show="encounter.encounterDatetime" class="icon-time"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.timeLocalized }}
    </span>
    <span class="encounter-card-summary provider-and-location-span encounter-provider">
        <span ng-show="encounter.encounterProviders">{{ 'coreapps.by' | translate }}</span>
        {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}
    </span>
    <span class="encounter-card-summary admission-location provider-and-location-span" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">
        ${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}
    </span>
</span>

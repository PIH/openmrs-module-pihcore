<span class="date-span"><i class="icon-calendar small"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}</span>
<span class="time-span"><i class="icon-time small"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.time }}</span>
<i ng-show="icon" class="{{ icon }}"></i>
<span class="encounter-name title encounter-span">{{ encounterStub.encounterType | omrs.display }}</span> <!-- encounter-type class added for smoke tests -->
<span class="encounter-provider provider-and-location-span">{{ 'emr.by' | translate }} {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderNameFromDisplayString }}</span>
<span class="encounter-provider admission-location provider-and-location-span" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">${ ui.message("uicommons.at") } {{encounter.location | omrs.display}}</span>
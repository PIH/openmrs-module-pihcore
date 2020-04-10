<span class="encounter-name title encounter-span"><i ng-show="icon" class="{{ icon }}"></i> {{ encounter.encounterType.display }}</span> <!-- encounter-type class added for smoke tests -->
<span class="date-span"><i class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}</span>
<span class="time-span"><i class="icon-time"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.time }}</span>
<span class="encounter-provider provider-and-location-span">{{ 'emr.by' | translate }} {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}</span>
<span class="encounter-provider admission-location provider-and-location-span" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}</span>

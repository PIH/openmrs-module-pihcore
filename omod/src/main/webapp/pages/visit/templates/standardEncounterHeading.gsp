<i ng-show="icon" class="{{ icon }}"></i>
<span class="encounter-name title">{{ encounterStub.encounterType | omrs.display }}</span> <!-- encounter-type class added for smoke tests -->
<span class="encounter-provider">{{ 'emr.by' | translate }} {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderNameFromDisplayString }}</span>
<span class="encounter-provider" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">${ ui.message("uicommons.at") } {{encounter.location | omrs.display}}</span>
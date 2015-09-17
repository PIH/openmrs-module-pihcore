<i ng-show="icon" class="{{ icon }}"></i>
<span class="encounter-name title">{{ encounterStub.encounterType | omrs.display }}</span> <!-- encounter-type class added for smoke tests -->
<span class="encounter-provider">{{ 'emr.by' | translate }} {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderNameFromDisplayString }}</span>
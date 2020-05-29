<div class="row justify-content-between">
    <div class="encounter-type col-8">
        <i ng-show="icon" class="{{ icon }}"></i> {{ encounter.encounterType.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span"><i class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.date }}</span>
        <span class="time-span"><i class="icon-time"></i>{{ encounter.encounterDatetime | serverDate:DatetimeFormats.time }}</span>
    </div>
</div>
<div class="row col-12">
    <span class="encounter-card-summary">{{ encounter.obs | byConcept:Concepts.triageQueueStatus:true | obs }}</span>
</div>
<div class="row justify-content-between">
    <div class="col-8">
        <span class="encounter-card-summary provider-and-location-span">{{ 'edtriageapp.enteredBy' | translate }} {{ encounter.encounterProviders | orderBy: 'dateCreated'| encounterRole:primaryEncounterRoleUuid | getProviderName }}</span>
        <span class="encounter-card-summary admission-location provider-and-location-span" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}</span>
    </div>
    <div class="col-4 text-right overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></div>
</div>

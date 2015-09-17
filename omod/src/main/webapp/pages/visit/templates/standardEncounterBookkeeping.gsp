<span>{{encounter.encounterDatetime | serverDate:encounterDateFormat}}</span>
<span class="encounter-location" ng-hide="encounter.location.uuid == encounter.visit.location.uuid">${ ui.message("uicommons.at") } {{encounter.location | omrs.display}}</span>
<span ng-hide="encounter.encounterDatetime == encounter.auditInfo.dateCreated">
    * ${ ui.message("pihcore.visitNote.enteredOn") } {{encounter.auditInfo.dateCreated | serverDate:encounterDateFormat}}
</span>
<span>
    * ${ ui.message("pihcore.visitNote.enteredBy") } {{ encounter.auditInfo.creator | omrs.display }}
</span>
<span ng-show="encounter.auditInfo.dateChanged">
    * ${ ui.message("pihcore.visitNote.editedOn") } {{encounter.auditInfo.dateChanged | serverDate:encounterDateFormat}}
</span>
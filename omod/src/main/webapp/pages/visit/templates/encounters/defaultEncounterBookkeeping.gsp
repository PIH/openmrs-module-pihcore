<span>{{encounter.encounterDatetime | serverDate:encounterDateFormat}}</span>

<span ng-hide="encounter.encounterDatetime == encounter.auditInfo.dateCreated">
    * ${ ui.message("pihcore.visitNote.enteredOn") } {{encounter.auditInfo.dateCreated | serverDate:encounterDateFormat}}
</span>
<span>
    * ${ ui.message("pihcore.visitNote.enteredBy") } {{ encounter.auditInfo.creator | omrsDisplay }}
</span>
<span ng-show="encounter.auditInfo.dateChanged">
    * ${ ui.message("pihcore.visitNote.editedOn") } {{encounter.auditInfo.dateChanged | serverDate:encounterDateFormat}}
</span>
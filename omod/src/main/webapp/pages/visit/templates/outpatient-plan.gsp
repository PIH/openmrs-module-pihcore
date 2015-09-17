<div ng-repeat="encounter in visit.encounters | filter:{voided:false} | with:'encounterType':EncounterTypes.consultationPlan | orderBy:'encounterDatetime'">
    <encounter encounter="encounter"></encounter>
</div>

<div class="new-encounter-button">
    <a class="button" ui-sref="editPlan" ng-class="{'confirm': hasDraftOrders()}">
        <i class="icon-list-ol"></i>
        ${ ui.message("pihcore.visitNote.outpatientPlan") }
    </a>
    <strong ng-show="hasDraftOrders()">${ ui.message("pihcore.visitNote.unsavedDraft") }</strong>
</div>
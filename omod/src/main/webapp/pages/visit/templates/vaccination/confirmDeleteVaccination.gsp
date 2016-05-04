<div class="dialog-header">
    <h3>${ ui.message("pihcore.visitNote.confirmDeleteVaccination.header") }</h3>
</div>
<div class="dialog-content">
    <h4>
        ${ ui.message("pihcore.visitNote.confirmDeleteVaccination.confirm") }
    </h4>
    <div>
        {{ vaccination.label | translate }} - {{ sequence.label | translate }} - {{ dateObs | obs:"value" | serverDate : 'dd-MMM-yyyy'  }}
        <br/>
        <br/>
    </div>
    <div>
        <!-- note: vaccination-confirm is simply a helper class for smoke tests -->
        <button class="vaccination-confirm confirm right" ng-click="confirm()">${ ui.message("uicommons.delete") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
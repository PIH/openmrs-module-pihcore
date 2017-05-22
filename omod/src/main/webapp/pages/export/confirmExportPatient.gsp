<div class="dialog-header">
    <h3>${ ui.message("uicommons.confirm") }</h3>
</div>
<div class="dialog-content">
    <br/>
        <textarea ng-model="currentPatientJson" id="export-patient">
        </textarea>
    <br/>
    <br/>
    <div>
        <button class="confirm" ng-click="confirm()">${ ui.message("uicommons.downloadButtonLabel") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
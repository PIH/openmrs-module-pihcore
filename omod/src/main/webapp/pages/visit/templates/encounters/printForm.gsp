<div class="dialog-header print-form-dialog-header">
    <h3>{{ encounterTitle }}</h3>
</div>
<div class="dialog-content">
    <!-- the form's HFE "view" html is injected here so the orderWidget scripts run and the medications/images render before printing -->
    <div class="print-form-content"></div>
    <div class="print-form-buttons">
        <button class="confirm right" ng-click="doPrint()" ng-disabled="!printFormReady">${ ui.message("pihcore.print") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>

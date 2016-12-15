
<!-- TODO: no longer used, eventually remove! -->

<div class="dialog-header">
    <h3>${ ui.message("uicommons.confirm") }</h3>
</div>
<div class="dialog-content">
    <p>
        ${ ui.message("pihcore.confirmRemoveFromQueue") }
    </p>
    <br/>
    <div>
        <button class="confirm" ng-click="confirm()">${ ui.message("pihcore.beginConsult") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
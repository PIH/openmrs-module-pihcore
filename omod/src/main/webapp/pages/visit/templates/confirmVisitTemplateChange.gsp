<div class="dialog-header">
    <h3>${ ui.message("pihcore.visitNote.changeVisitTemplate") }</h3>
</div>
<div class="dialog-content">
    <h4>
        ${ ui.message("pihcore.visitNote.confirmTemplateChange") }!
    </h4>

    <br/>
    <div>
        <button class="confirm right" ng-click="confirm()">${ ui.message("mirebalais.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
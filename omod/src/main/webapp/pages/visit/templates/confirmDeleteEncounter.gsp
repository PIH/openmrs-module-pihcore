<!-- delete-encounter-dialog and delete-encounter-button classes currently solely used for selenium tests -->
<div class="dialog-header delete-encounter-dialog">
    <h3>${ ui.message("pihcore.visitNote.confirmDeleteEncounter.header") }</h3>
</div>
<div class="dialog-content">
    <h4>
        ${ ui.message("pihcore.visitNote.confirmDeleteEncounter.confirm") }
    </h4>
    <div ng-show="activeOrders && activeOrders.length" class="note-container">
        <div class="note warning">
            <i class="icon-warning-sign medium"></i>
            ${ ui.message("pihcore.visitNote.confirmDeleteEncounter.encounterHasActiveOrdersWarning") }
            <br/>
            <br/>
            ${ ui.message("pihcore.visitNote.confirmDeleteEncounter.confirmDeleteActiveOrders") }
            <!--  <ul class="small">
                <li ng-repeat="order in activeOrders">
                    {{ order | orderInstructions }}
                </li>  (this no longer works with order functionality removed)-->
            </ul>

        </div>
    </div>
    <div>
        <button class="confirm right delete-encounter-button" ng-click="confirm()">${ ui.message("uicommons.delete") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>

<fieldset>
    <legend>${ ui.message("pihcore.visitNote.orders.activePrescriptions") } ({{ orderContext.careSetting | omrs.display }})</legend>

    <span ng-hide="activeDrugOrders.\$resolved">${ ui.message("uicommons.loading.placeholder") }</span>

    <span ng-show="activeDrugOrders.\$resolved && activeDrugOrders.length == 0">${ ui.message("uicommons.none") }</span>

    <span ng-show="activeDrugOrders.\$resolved && activeDrugOrders.length > 0">
        {{ activeOrderConcepts() | omrs.display }}
    </span>

    <a ng-hide="state == 'long'" ng-click="expand()">${ ui.message("uicommons.expand") }</a>

</fieldset>
<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "orderEntry.css")
    ui.includeCss("pihcore", "labOrder.css")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.labOrders") }" , link: '${ui.pageLink("pihcore", "patient/labOrders", ["patientId": patient.id])}'}
    ];
    function discontinueOrder(orderUuid) {
        const discontinueDialog = emr.setupConfirmationDialog({
            selector: '#discontinue-order-dialog',
            actions: {
                confirm: function() {
                    const discontinueReason = jq("#discontinue-reason-field").val();

                },
                cancel: function() {
                    jq("#discontinue-reason-field").val("")
                }
            }
        });
        discontinueDialog.show();
    }
</script>

<div class="row">
    <div class="col-8">
        <h3>${ ui.message("pihcore.labOrders.active") }</h3>
    </div>
    <div class="col-4 text-right">
        <a href="${ui.pageLink("pihcore", "patient/labOrder", ["patient": patient.patient.uuid])}"><input type="button" value="${ui.message("pih.app.labs.ordering")}"/></a>
    </div>
</div>

<table id="active-orders-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("pihcore.orderDate") }</th>
            <th>${ ui.message("pihcore.orderNumber") }</th>
            <th>${ ui.message("pihcore.labTest") }</th>
            <th>${ ui.message("pihcore.testOrderedBy") }</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    <% if (activeOrders.isEmpty()) { %>
        <tr>
            <td colspan="6">${ ui.message("coreapps.none") }</td>
        </tr>
    <% } %>
    <% activeOrders.each { labOrder -> %>
        <tr>
            <td>${ ui.formatDatetimePretty(labOrder.effectiveStartDate) }</td>
            <td>${ ui.format(labOrder.orderNumber) }</td>
            <td>
                <% if (ui.format(labOrder.urgency) == 'STAT') { %>
                    <i class="fas fa-fw fa-exclamation" style="color: red;"></i>
                <% } %>
                ${ pihui.getBestShortName(labOrder.concept) }</td>
            <td>${ ui.format(labOrder.orderer) }</td>
            <td class="order-actions-btn" style="text-align: center;">
                <span>
                    <a href="#" onclick="discontinueOrder('${labOrder.uuid}')"><i class="icon-remove scale" title="${ui.message("pihcore.discontinue")}"></i></a>
                </span>
            </td>
        </tr>
    <% } %>
    </tbody>
</table>

<br/>

<h3>${ ui.message("pihcore.labOrders.inactive") }</h3>

<table id="inactive-orders-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("pihcore.orderDate") }</th>
        <th>${ ui.message("pihcore.orderNumber") }</th>
        <th>${ ui.message("pihcore.labTest") }</th>
        <th>${ ui.message("pihcore.testOrderedBy") }</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <% if (inactiveOrders.isEmpty()) { %>
    <tr>
        <td colspan="6">${ ui.message("coreapps.none") }</td>
    </tr>
    <% } %>
    <% inactiveOrders.each { labOrder -> %>
    <tr>
        <td>${ ui.formatDatePretty(labOrder.effectiveStartDate) }</td>
        <td>${ ui.format(labOrder.orderNumber) }</td>
        <td>${ pihui.getBestShortName(labOrder.concept) }</td>
        <td>${ ui.format(labOrder.orderer) }</td>
        <td class="order-actions-btn"></td>
    </tr>
    <% } %>
    </tbody>
</table>

<div id="discontinue-order-dialog" class="dialog" style="display: none;">
    <div class="dialog-header">
        <i class="icon-remove"></i>
        <h3>${ui.message("pihcore.discontinueOrder")}</h3>
    </div>
    <div class="dialog-content form">
        ${ui.message("pihcore.discontinueReason")}
        <br>
        <textarea id="discontinue-reason-field" type="text" rows="3" cols="40"></textarea>
        <br><br>
        <button class="cancel">${ ui.message("coreapps.cancel") }</button>
        <button class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
    </div>
</div>
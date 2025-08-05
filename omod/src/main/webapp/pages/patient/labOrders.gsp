<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "orderEntry.css")
    ui.includeCss("pihcore", "labOrder.css")
    ui.includeJavascript("uicommons", "moment.min.js")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.encodeJavaScript(ui.message("pihcore.labOrders")) }" , link: '${ui.pageLink("pihcore", "patient/labOrders", ["patientId": patient.id])}'}
    ];
    function discontinueOrder(orderUuid, orderableUuid) {
        const discontinueDialog = emr.setupConfirmationDialog({
            selector: '#discontinue-order-dialog',
            actions: {
                confirm: function() {

                    const discontinueReason = jq("#discontinue-reason-field").val();
                    const discontinueDate = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
                    const patient = '${patient.patient.uuid}';
                    const orderer = '${sessionContext.currentProvider.uuid}';
                    jq.get(openmrsContextPath + "/ws/rest/v1/pihcore/labOrderConfig", function(labOrderConfig) {
                        const encounterPayload = {
                            patient: patient,
                            encounterType: labOrderConfig.encounterType,
                            encounterDatetime: discontinueDate,
                            location: '${sessionContext.sessionLocation.uuid}',
                            encounterProviders: [ { encounterRole: labOrderConfig.encounterRole, provider: orderer } ],
                            orders: [
                                {
                                    type: 'testorder',
                                    action: 'DISCONTINUE',
                                    previousOrder: orderUuid,
                                    patient: patient,
                                    orderer: orderer,
                                    concept: orderableUuid,
                                    urgency: 'ROUTINE',
                                    orderReasonNonCoded: discontinueReason,
                                    careSetting: labOrderConfig.careSetting.INPATIENT,
                                    dateActivated: discontinueDate,
                                }
                            ]
                        };
                        jq.ajax({
                            url: openmrsContextPath + '/ws/rest/v1/encounter',
                            type: 'POST',
                            contentType: 'application/json; charset=utf-8',
                            data: JSON.stringify(encounterPayload),
                            dataType: 'json', // Expect JSON response
                            success: function(response) {
                                emr.successMessage('${ui.encodeJavaScript(ui.message("pihcore.discontinueSuccessMessage"))}');
                                document.location.href = '${ui.pageLink('pihcore', 'patient/labOrders', [patient: patient.id])}>';
                            },
                            error: function(xhr, status, error) {
                                const message = xhr.responseJSON?.error?.message ?? error ?? xhr.responseText;
                                emr.errorMessage('${ui.encodeJavaScript(ui.message("pihcore.discontinueErrorMessage"))}: ' + message);
                            }
                        });
                    })
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
    <div class="col-6">
        <h3>${ ui.message("pihcore.labOrders.active") }</h3>
    </div>
    <div class="col-6 text-right">
        <a href="${ui.pageLink("pihcore", "patient/labOrder", ["patient": patient.patient.uuid])}">
            <input type="button" value="${ui.message("pihcore.addLabOrders")}" style="max-width: unset;"/>
        </a>
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
                    <a href="#" onclick="discontinueOrder('${labOrder.uuid}', '${labOrder.concept.uuid}')"><i class="icon-remove scale" title="${ui.message("pihcore.discontinue")}"></i></a>
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
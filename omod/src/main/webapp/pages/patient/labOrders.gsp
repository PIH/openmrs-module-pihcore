<%
    ui.decorateWith("appui", "standardEmrPage")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.labOrders") }" , link: '${ui.pageLink("pihcore", "patient/labOrders", ["patientId": patient.id])}'}
    ];
</script>

<h3>${ ui.message("pihcore.labOrders.active") }</h3>

<table id="active-orders-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("pihcore.orderDate") }</th>
            <th>${ ui.message("pihcore.orderNumber") }</th>
            <th>${ ui.message("pihcore.labTest") }</th>
            <th>${ ui.message("pihcore.testOrderedBy") }</th>
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
            <td>${ ui.formatDatePretty(labOrder.effectiveStartDate) }</td>
            <td>${ ui.format(labOrder.orderNumber) }</td>
            <td>
                <% if (ui.format(labOrder.urgency) == 'STAT') { %>
                    <i class="fas fa-fw fa-exclamation" style="color: red;"></i>
                <% } %>
                ${ pihui.getBestShortName(labOrder.concept) }</td>
            <td>${ ui.format(labOrder.orderer) }</td>
        </tr>
    <% } %>
    </tbody>
</table>
<div style="padding-top:10px;">
    <a href="${ui.pageLink("pihcore", "patient/labOrder", ["patient": patient.patient.uuid])}"><input type="button" value="${ui.message("pih.app.labs.ordering")}"/></a>
</div>

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
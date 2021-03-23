<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<style>
    .category-row {
        font-weight: bold;
        background-color: #501d3d;
        color: white;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<h3>${ ui.message("pihcore.drugOrders.activeTreatments") }</h3>

<table id="active-orders-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("pihcore.drugOrders.concept") }</th>
            <th>${ ui.message("pihcore.drugOrders.drug") }</th>
            <th>${ ui.message("pihcore.drugOrders.startDate") }</th>
            <th>${ ui.message("pihcore.drugOrders.expireDate") }</th>
            <th>${ ui.message("pihcore.drugOrders.dosing") }</th>
        </tr>
    </thead>
    <tbody>
    <% if (activeOrders.isEmpty()) { %>
        <tr>
            <td colspan="6">${ ui.message("coreapps.none") }</td>
        </tr>
    <% } %>
    <%  activeOrdersByCategory.keySet().each { category ->
        List ordersInCategory = activeOrdersByCategory.get(category);
        if (ordersInCategory != null && !ordersInCategory.isEmpty()) { %>
            <tr>
                <td class="category-row" colspan="6">${ category.equals("") ? ui.message("pihcore.other") : category }</td>
            </tr>
            <%  activeOrdersByCategory.get(category).each { drugOrder -> %>
                <tr>
                    <td>${ ui.format(drugOrder.concept) }</td>
                    <td>${ drugOrder.drug != null ? drugOrder.drug.name : (drugOrder.drugNonCoded != null ? drugOrder.drugNonCoded : "") }</td>
                    <td style="white-space: nowrap;">
                        <a href="${ ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi",
                                    [
                                        "patientId": drugOrder.patient.uuid,
                                        "encounterId": drugOrder.encounter.uuid,
                                        "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "meds/drugOrders", [ "patient": drugOrder.patient.uuid] )),
                                    ]
                        )}" />
                            ${ ui.format(drugOrder.effectiveStartDate) }
                        </a>
                    </td>
                    <td style="white-space: nowrap;">${ ui.format(drugOrder.autoExpireDate) }</td>
                    <td>
                        <% if (drugOrder.getDosingType().getSimpleName().equals("FreeTextDosingInstructions")) { %>
                            ${ drugOrder.getDosingInstructions() }
                        <% } else if (drugOrder.getDosingType().getSimpleName().equals("SimpleDosingInstructions")) { %>
                            ${ drugOrder.getDose() } ${ ui.format(drugOrder.getDoseUnits() )}
                            ${ ui.format(drugOrder.getFrequency().getConcept()) } ${ ui.format(drugOrder.getRoute()) }
                        <% } %>
                    </td>
                </tr>
            <% } %>
        <% } %>
    <% } %>

    </tbody>
</table>

<br/>
<h3>${ ui.message("pihcore.drugOrders.completedTreatments") }</h3>

<table id="active-orders-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("pihcore.drugOrders.concept") }</th>
        <th>${ ui.message("pihcore.drugOrders.drug") }</th>
        <th>${ ui.message("pihcore.drugOrders.startDate") }</th>
        <th>${ ui.message("pihcore.drugOrders.expireDate") }</th>
        <th>${ ui.message("pihcore.drugOrders.discontinueDate") }</th>
        <th>${ ui.message("pihcore.drugOrders.discontinueReason") }</th>
        <th>${ ui.message("pihcore.drugOrders.dosing") }</th>
    </tr>
    </thead>
    <tbody>
    <% if (completedOrders.isEmpty()) { %>
        <tr>
            <td colspan="7">${ ui.message("coreapps.none") }</td>
        </tr>
    <% } %>
    <%  completedOrdersByCategory.keySet().each { category ->
        List ordersInCategory = completedOrdersByCategory.get(category);
        if (ordersInCategory != null && !ordersInCategory.isEmpty()) { %>
            <tr>
                <td class="category-row" colspan="7">${ category.equals("") ? ui.message("pihcore.other") : category }</td>
            </tr>
            <%  completedOrdersByCategory.get(category).each { drugOrder -> %>
                <tr>
                    <td>${ ui.format(drugOrder.concept) }</td>
                    <td>${ drugOrder.drug != null ? drugOrder.drug.name : (drugOrder.drugNonCoded != null ? drugOrder.drugNonCoded : "") }</td>
                    <td style="white-space: nowrap;">
                        <a href="${ ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi",
                                [
                                        "patientId": drugOrder.patient.uuid,
                                        "encounterId": drugOrder.encounter.uuid,
                                        "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "meds/drugOrders", [ "patient": drugOrder.patient.uuid] )),
                                ]
                        )}">
                            ${ ui.format(drugOrder.effectiveStartDate) }
                        </a>
                    </td>
                    <td style="white-space: nowrap;">${ ui.format(drugOrder.autoExpireDate) }</td>
                    <% def dcOrder = ordersToDiscontinueOrders.get(drugOrder) %>
                    <td>
                        <% if (dcOrder != null) { %>
                            <a href="${ ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi",
                                    [
                                            "patientId": drugOrder.patient.uuid,
                                            "encounterId": drugOrder.encounter.uuid,
                                            "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "meds/drugOrders", [ "patient": drugOrder.patient.uuid] )),
                                    ]
                            )}">
                        <% } %>
                            ${ ui.format(drugOrder.dateStopped) }</td>
                        <% if (dcOrder != null) { %>
                            </a>
                        <% } %>
                    <td>
                        <% if (dcOrder != null) { %>
                            ${ dcOrder.orderReason != null ? ui.format(dcOrder.orderReason) : dcOrder.orderReasonNonCoded }
                        <% } %>
                    </td>
                    <td>
                        <% if (drugOrder.getDosingType().getSimpleName().equals("FreeTextDosingInstructions")) { %>
                            ${ drugOrder.getDosingInstructions() }
                        <% } else if (drugOrder.getDosingType().getSimpleName().equals("SimpleDosingInstructions")) { %>
                            ${ drugOrder.getDose() } ${ ui.format(drugOrder.getDoseUnits() )}
                            ${ ui.format(drugOrder.getFrequency().getConcept()) } ${ ui.format(drugOrder.getRoute()) }
                        <% } %>
                    </td>
                </tr>
            <% } %>
        <% } %>
    <% } %>

    </tbody>
</table>

<% if (!medicationCommentObs.isEmpty()) { %>
    <br/>
    <h3>${ ui.message("pihcore.drugOrders.comments") }</h3>
    <table id="comments-list" width="100%" border="1" cellspacing="0" cellpadding="2">
        <thead>
        <tr>
            <th>${ ui.message("pihcore.drugOrders.comment") }</th>
            <th>${ ui.message("pihcore.drugOrders.enteredBy") }</th>
            <th>${ ui.message("pihcore.drugOrders.dateEntered") }</th>
        </tr>
        </thead>
    <tbody>
        <%  medicationCommentObs.each { commentObs -> %>
            <tr>
                <td>${ ui.format(commentObs.valueText) }</td>
                <td style="white-space: nowrap;">${ ui.format(commentObs.creator) }</td>
                <td style="white-space: nowrap;">${ ui.format(commentObs.dateCreated) }</td>
            </tr>
        <% } %>
    </tbody>
</table>
<% } %>

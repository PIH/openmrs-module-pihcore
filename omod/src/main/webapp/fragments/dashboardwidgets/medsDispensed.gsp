<div class="info-section meds-dispensed-widget">
    <div class="info-header">
        <i class="fas fa-fw fa-pills"></i>
        <h3>${ ui.message("mirebalais.dispensing.title") }</h3>
        <a href="${ ui.urlBind("/" + contextPath + "/" + detailsUrl, [ "patient.uuid": patient.uuid ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <% if (medsToDisplay.isEmpty()) { %>
        <span>${ui.message('coreapps.none')}</span>
        <% } else { %>
            <table>
                <thead>
                    <tr>
                        <th class="column-header">${ui.message("coreapps.date")}</th>
                        <th class="column-header">${medsDispensedHeaderName}</th>
                    </tr>
                </thead>
                <tbody>
                    <% medsToDisplay.each { medToDisplay -> %>
                        <tr>
                            <td>${ui.formatDatePretty(medToDisplay.dispenseDate)}</td>
                            <td>${medToDisplay.dispenseConceptName}</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>

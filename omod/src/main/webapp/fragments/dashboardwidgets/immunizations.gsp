<div class="info-section immunizations-widget ${app.id}">
    <div class="info-header">
        <i class="fas fa-fw ${app.icon}"></i>
        <h3>${ ui.message("pihcore.immunizations.title") }</h3>
        <% if (app.url) { %>
            <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.uuid ]) }" class="right">
                <i class="icon-share-alt edit-action" title="Edit"></i>
            </a>
        <% } %>
    </div>
    <div class="info-body">
        <% if (immunizationsByConcept.isEmpty()) { %>
            <span>${ui.message("coreapps.none")}</span>
        <% } else { %>
            <table>
                <tbody>
                    <% immunizationsByConcept.keySet().each { immunizationConcept ->
                        def immunization = immunizationsByConcept.get(immunizationConcept) %>
                        <tr>
                            <td>
                                ${ui.message("pihcore.concept.name." + immunizationConcept.uuid)}
                            </td>
                            <td>
                                <% if (immunization.immunizationObs) { %>

                                    <% if (immunization.sequenceNumberObs) { %>
                                        ${ui.message("pihcore.vaccination.sequence.dose")} ${ui.format(immunization.sequenceNumberObs.valueNumeric)}
                                        ${immunization.dateObs ? " - " : ""}
                                    <% } %>

                                    <% if (immunization.dateObs) { %>
                                        ${ui.formatDatePretty(immunization.dateObs.valueDatetime)}
                                    <% } %>

                                <% } else { %>

                                    ${ui.message("coreapps.none")}

                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>

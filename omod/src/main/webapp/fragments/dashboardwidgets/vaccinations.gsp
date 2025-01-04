<div class="info-section vaccinations-widget ${app.id}">
    <div class="info-header">
        <i class="fas fa-fw ${app.icon}"></i>
        <h3>${ ui.message("pihcore.vaccinations.title") }</h3>
        <% if (app.url) { %>
            <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.uuid ]) }" class="right">
                <i class="icon-share-alt edit-action" title="Edit"></i>
            </a>
        <% } %>
    </div>
    <div class="info-body">
        <% if (vaccinationsByConcept.isEmpty()) { %>
            <span>${ui.message("coreapps.none")}</span>
        <% } else { %>
            <table>
                <tbody>
                    <% vaccinationsByConcept.keySet().each { vaccinationConcept ->
                        def vaccination = vaccinationsByConcept.get(vaccinationConcept) %>
                        <tr>
                            <td>
                                ${ui.message("pihcore.concept.name." + vaccinationConcept.uuid)}
                            </td>
                            <td>
                                <% if (vaccination.vaccinationObs) { %>

                                    <% if (vaccination.sequenceNumberObs) { %>
                                        ${ui.message("pihcore.vaccination.sequence.dose")} ${ui.format(vaccination.sequenceNumberObs.valueNumeric)}
                                        ${vaccination.dateObs ? " - " : ""}
                                    <% } %>

                                    <% if (vaccination.dateObs) { %>
                                        ${ui.formatDatePretty(vaccination.dateObs.valueDatetime)}
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

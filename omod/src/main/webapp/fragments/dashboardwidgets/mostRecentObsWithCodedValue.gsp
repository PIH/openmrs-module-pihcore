<div class="info-section most-recent-obs-with-coded-value-widget ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + "/" + detailsUrl, [ "patient.uuid": patient.uuid ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <% if (obsList.isEmpty()) { %>
            <span>${ui.message('coreapps.none')}</span>
        <% } else { %>
            <table>
                <thead>
                    <tr>
                        <th class="column-header">${ui.message("coreapps.date")}</th>
                        <th class="column-header">${ui.message(codedValueHeader)}</th>
                    </tr>
                </thead>
                <tbody>
                    <% obsList.each { obs -> %>
                        <tr>
                            <td>${ui.formatDatePretty(obs.obsDatetime)}</td>
                            <td>${ui.format(obs.valueCoded)}</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>

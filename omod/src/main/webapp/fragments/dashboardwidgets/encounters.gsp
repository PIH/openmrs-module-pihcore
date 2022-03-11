
<div class="info-section encounters-by-type-widget">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.id ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <% encounters.each{encounter -> %>
            <div>
                <span class="encounter-date">
                    <% if (encounter.visit) { %>
                        <a class="visit-link" href="${ ui.urlBind("/" + contextPath + encounterTypeToUrlMap.get(encounter.encounterType), [ "patient.uuid": patient.id, "visit.uuid": encounter.visit.uuid ]) }">
                            ${ui.formatDatePretty(encounter.encounterDatetime)}
                        </a>
                    <% } else { %>
                        ${ui.formatDatePretty(encounter.encounterDatetime)}
                    <% } %>
                </span>
                <div class="tag">${ui.format(encounter.location)}</div>
            </div>
        <% } %>
    </div>
</div>

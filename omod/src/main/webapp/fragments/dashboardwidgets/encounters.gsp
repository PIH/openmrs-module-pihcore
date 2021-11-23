
<div class="info-section encounters-by-type-widget">
    <div class="info-header">
        <i class="icon-calendar"></i>
        <h3>${ ui.message(headerLabel) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + listUrl, [ "patient.uuid": patient.id ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <% encounters.each{encounter -> %>
            <div>
                <span class="encounter-date">
                    <a class="visit-link" href="${ ui.urlBind("/" + contextPath + detailsUrl, [ "patient.uuid": patient.id, "visit.uuid": encounter.visit.uuid ]) }">
                        ${ui.formatDatePretty(encounter.encounterDatetime)}
                    </a>
                </span>
                <div class="tag">${ui.format(encounter.location)}</div>
            </div>
        <% } %>
    </div>
</div>

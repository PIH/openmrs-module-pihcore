
<div class="info-section">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.id ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <% fields.each { concept, obs -> %>
            <div>
                <span style="font-family: 'OpenSansBold'";>${ ui.message(obs.label) }:</span>
                <% if ( obs.minValue && obs.obsValue && obs.minValue.isNumber() && obs.obsValue.isNumber() &&
                        (new BigDecimal(obs.obsValue) < new BigDecimal(obs.minValue))) {%>
                    <span style="color: red;">${ obs.obsValue }</span>
                <% } else { %>
                    <span>${ obs.obsValue }</span>
                <% } %>
            </div>
        <% } %>
    </div>
</div>

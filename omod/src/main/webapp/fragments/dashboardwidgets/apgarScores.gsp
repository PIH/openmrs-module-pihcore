
<div class="info-section">
    <div class="info-header">
        <i class="fas fa-fw fa-child"></i>
        <h3>${ ui.message("pihcore.apgar.scores") }</h3>
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


<div class="info-section">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% fields.each { concept, obj -> %>
            <div>
                <span style="font-family: 'OpenSansBold'";>${ ui.message(obj.label) }:</span>
                <% if ( obj.minValue && obj.obs.valueNumeric &&
                        (new BigDecimal(obj.obs.valueNumeric) < new BigDecimal(obj.minValue))) {%>
                    <span style="color: red;">${ obj.obs.valueNumeric }</span>
                <% } else { %>
                    <span>${ ui.format(obj.obs) }</span>
                <% } %>
            </div>
        <% } %>
    </div>
</div>

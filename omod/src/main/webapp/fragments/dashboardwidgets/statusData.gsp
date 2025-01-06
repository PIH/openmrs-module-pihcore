
<div class="info-section status-data-widget ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% statusData.each{status ->
            def displayValue = status.displayValue
            if (displayValue != null) {
                displayValue = displayValue.replace('{{contextPath}}', contextPath)
            }
        %>
            <div>
                <span class="patient-dashboard-widget-label">${status.label}${status.label ? ': ' : ''}</span>
                <span class="patient-dashboard-widget-value ${status.displayFormat}">${displayValue}</span>
            </div>
        <% } %>
    </div>
</div>

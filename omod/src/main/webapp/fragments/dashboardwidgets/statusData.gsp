
<div class="info-section status-data-widget ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% statusData.each{status -> %>
            <div>
                <span class="patient-dashboard-widget-label">${status.label}${status.label ? ': ' : ''}</span>
                <span class="patient-dashboard-widget-value ${status.displayFormat}">${status.displayValue}</span>
            </div>
        <% } %>
    </div>
</div>

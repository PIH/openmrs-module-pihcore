
<div class="info-section status-data-widget">
    <div class="info-header">
        <i class="fas fa-fw fa-exclamation-circle"></i>
        <h3>${ ui.message(headerLabel) }</h3>
    </div>
    <div class="info-body">
        <% statusData.each{status -> %>
            <div>
                <span class="status-data-widget-label">${status.label}: </span>
                <span class="status-data-widget-value ${status.displayFormat}">${status.displayValue}</span>
            </div>
        <% } %>
    </div>
</div>

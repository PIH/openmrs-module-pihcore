<div class="info-section status-data-widget ${app.id}">
    <div class="info-header">
        <i class="${icon}"></i>
        <h3>${ ui.message(label) }</h3>
    </div>
    <div class="info-body">
        <%
            statusData.each { status ->
                def formattedValue
                def displayValue = status.displayValue

                if (displayValue != null && displayValue instanceof String) {
                    displayValue = displayValue.replace('{{contextPath}}', contextPath)
                }

                if (displayValue instanceof Date) {
                    formattedValue = ui.formatDatePretty(displayValue)
                } else if (displayValue instanceof String) {
                    try {
                        // updated format for "2025-09-10T00:00"
                        def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                        def parsedDate = sdf.parse(displayValue)
                        formattedValue = ui.formatDatePretty(parsedDate)
                    } catch (Exception e) {
                        // fallback to raw string if parsing fails
                        formattedValue = displayValue
                    }
                } else {
                    formattedValue = displayValue
                }
        %>
        <div>
            <span class="patient-dashboard-widget-label">
                ${status.label}${status.label ? ': ' : ''}
            </span>
            <span class="patient-dashboard-widget-value ${status.displayFormat}">
                ${formattedValue}
            </span>
        </div>
        <% } %>
    </div>
</div>

<% if (patientStatus.length() > 0 ) { %>
    <div class="info-section patient-location ${app.id}">
        <div class="info-header">
            <i class="icon-map-marker"></i>
            <h3>${ ui.message("pihcore.patient.location") }</h3>
        </div>
        <div class="info-body">
            <div>
                <% if (inpatientLocation != null ) { %>
                    <span class="patient-dashboard-widget-label">${ ui.message("pihcore.current.location") }:</span>
                    <span class="patient-dashboard-widget-value">
                        <% if (inpatientLocation.id == sessionContext.sessionLocationId) { %>
                            <a href="/${contextPath}/spa/home/ward">${ui.format(inpatientLocation)}</a>
                        <% } else { %>
                            ${ui.format(inpatientLocation)}
                        <% } %>
                    </span>
                <% } else { %>
                    <span class="patient-dashboard-widget-label">${ ui.message("pihcore.queue.name") }:</span>
                    <span class="patient-dashboard-widget-value">${queueName}</span>
                <% } %>
            </div>
            <div>
                <span class="patient-dashboard-widget-label">${ ui.message("pihcore.status") }:</span>
                <span class="patient-dashboard-widget-value">${patientStatus}</span>
            </div>
        </div>
    </div>
<% } %>

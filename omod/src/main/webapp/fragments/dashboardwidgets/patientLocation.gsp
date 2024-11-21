<% if (patientStatus.length() > 0 ) { %>
    <div class="info-section patient-location ${app.id}">
        <div class="info-header">
            <i class="icon-map-marker"></i>
            <h3>${ ui.message("pihcore.patient.location") }</h3>
        </div>
        <div class="info-body">
            <div>
                <% if (inpatientLocation != null ) { %>
                    <span>${ ui.message("pihcore.current.location") }:</span>
                    <div class="tag">${inpatientLocation}</div>
                <% } else { %>
                    <span>${ ui.message("pihcore.queue.name") }:</span>
                    <div class="tag">${queueName}</div>
                <% } %>
            </div>
            <div>
                <span>${ ui.message("pihcore.status") }:</span>
                <div class="tag">${patientStatus}</div>
            </div>
        </div>
    </div>
<% } %>
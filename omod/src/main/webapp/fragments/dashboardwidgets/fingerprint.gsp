
<div class="info-section status-data-widget ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label).toUpperCase() }</h3>
        <% if (detailsUrl) { %>
                <i class="icon-pencil edit-action right" title="${ ui.message("coreapps.edit") }"
                onclick="location.href='${ ui.urlBind("/" + contextPath + "/"+ detailsUrl, [ "patient.uuid": config.patientUuid, "patient.patientId": config.patientId ]) }';"></i>
        <% } %>   
    </div>
    <div class="info-body">
        <div class="fingerprint-message">
            <% if (fingerprintMessage != null) { %>
                <span class="patient-dashboard-widget-label">${ui.message("zl.fingerprint-status")}</span>
                <span class="patient-dashboard-widget-value">${ui.message(fingerprintMessage)}</span>
            <% } %>
        </div>
        <div class="fingerprint-dateCollected">
            <% if (fingerprintDateCollected != null) { %>
                <span class="patient-dashboard-widget-label">${ui.message("zl.collected.on")}</span>
                <span class="tag patient-dashboard-widget-value">${ui.formatDatePretty(fingerprintDateCollected)}</span>
            <% } %>
        </div>

    </div>

</div>

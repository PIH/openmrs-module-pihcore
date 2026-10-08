<style>
    .red {
        color: red;
    }
</style>
<div class="info-section">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% fields?.each { concept, obj -> %>
            <div>
                <span class="patient-dashboard-widget-label">${ ui.message(obj.label) }:</span>
                <span class="patient-dashboard-widget-value ${obj.css ? obj.css : ''}">${ obj.obs ?: ui.message("coreapps.none") }</span>
                <% if (obj.date) { %>
                    <span style="color: gray;">(${ ui.format(obj.date) })</span>
                <% } %>
            </div>
        <% } %>
    </div>
</div>

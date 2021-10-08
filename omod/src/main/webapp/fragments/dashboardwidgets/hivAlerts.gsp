
<div id="hiv-alerts" class="info-section">
    <div class="info-header">
        <i class="fas fa-fw fa-exclamation-circle"></i>
        <h3>${ ui.message("pihcore.hivAlerts.alerts") }</h3>
    </div>
    <div class="info-body">
        <% if (lateReturnVisitDateForDispensing) { %>
            <div>
                <span  <% if (lateReturnVisitDateForDispensingHighlight) { %>class="alert-warning"<% } %>>
                    ${ ui.message("pihcore.hivAlerts.lateForMedPickup") }: ${ ui.format(lateReturnVisitDateForDispensing) }
                </span>
            </div>
        <% } %>
        <% if (lateReturnVisitDateForAppointment) { %>
            <div>
                ${ ui.message("pihcore.hivAlerts.lateForAppointment") }: ${ ui.format(lateReturnVisitDateForAppointment) }
            </div>
        <% } %>
    </div>
</div>

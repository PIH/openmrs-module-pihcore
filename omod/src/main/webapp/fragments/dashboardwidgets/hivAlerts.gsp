<%

%>
<div id="hiv-alerts" class="info-section">
    <div class="info-header">
        <i class="fas fa-fw fa-exclamation-circle"></i>
        <h3>ALERTS</h3>
    </div>
    <div class="info-body">
        <% if (lateReturnVisitDateForDispensing) { %>
            <div>
                Late for med pickup: ${ ui.format(lateReturnVisitDateForDispensing) }
            </div>
        <% } %>
    </div>
</div>

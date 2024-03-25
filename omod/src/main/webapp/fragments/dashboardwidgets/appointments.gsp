<div class="info-section">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label).toUpperCase() }</h3>
        <a href="${ "/" + contextPath + "/spa/home/appointments/patient/" +   patientUuid }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <ul>
            <% appointments.each { appointment -> %>
            <li>
                <div style="display: block; overflow: hidden; padding-right: 5px; padding-bottom: 2px;">
                    <div style="float: left">
                        ${ui.formatDatetimePretty(appointment.startDateTime)}
                        <br/>
                        <strong>
                            ${ appointment.service?.name }
                        </strong>
                    </div>
                    <div  style="float: right; vertical-align: middle;">
                        ${  ui.message("pihcore.appointments.status." + appointment.status) }
                    </div>
                </div>
            </li>
            <% } %>
        </ul>
    </div>
</div>

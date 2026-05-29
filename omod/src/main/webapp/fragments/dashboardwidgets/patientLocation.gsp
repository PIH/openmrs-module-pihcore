<style>
    .boldLabel {
        font-family: 'OpenSansBold';
    }
    .edit-icon {
        cursor: pointer;
        margin-left: 5px;
        color: #007bff;
    }
    .edit-icon:hover {
        color: #0056b3;
    }
    .admission-status-editor {
        display: inline-block;
        margin-left: 10px;
    }
    .admission-status-editor select {
        padding: 4px 8px;
        margin-right: 5px;
    }
    .admission-status-save-btn {
        background-color: #6c757d;
        color: white;
        border: none;
        padding: 4px 12px;
        cursor: pointer;
        border-radius: 3px;
    }
    #admissionStatusSaveBtn.changed,
    #admissionStatusSaveBtn.changed:hover {
        background-color: #dc3545;
    }

    .admission-status-save-btn:disabled {
        background-color: #6c757d;
        cursor: not-allowed;
    }
</style>

<% if (patientStatus.length() > 0 ) { %>
<div class="info-section patient-location ${app.id}">
    <div class="info-header">
        <i class="icon-map-marker"></i>
        <h3>${ ui.message("pihcore.patientLocation.ucase") }</h3>
    </div>
    <div class="info-body">
        <div>
            <% if (inpatientLocation != null ) { %>
            <span class="patient-dashboard-widget-label boldLabel">${ ui.message("pihcore.current.location") }:</span>
            <span class="patient-dashboard-widget-value">
                <% if (inpatientLocation.id == sessionContext.sessionLocationId) { %>
                <a href="/${contextPath}/spa/home/ward">${ui.format(inpatientLocation)}</a>
                <% } else { %>
                ${ui.format(inpatientLocation)}
                <% } %>
            </span>
            <% } else { %>
            <span class="patient-dashboard-widget-label boldLabel">${ ui.message("pihcore.queue.name") }:</span>
            <span class="patient-dashboard-widget-value">${queueName}</span>
            <% } %>
        </div>
        <div>
            <span class="patient-dashboard-widget-label boldLabel">${ ui.message("pihcore.status") }:</span>
            <span class="patient-dashboard-widget-value">${patientStatus}</span>
        </div>
        <div>
            <span class="patient-dashboard-widget-label boldLabel">${ ui.message("pihcore.admission.type") }:</span>
            <span class="patient-dashboard-widget-value">
                <span id="admissionStatusDisplay">${ isBornDuringVisit ? ui.message("pihcore.inborn") : ui.message("pihcore.outborn") }</span>
                <% if (activeVisitUuid != null && visitAttributeUuid != null) { %>
                <i class="icon-pencil edit-icon" id="editAdmissionStatusIcon" onclick="showAdmissionStatusEditor()" title="${ ui.message("coreapps.edit") }"></i>
                <span id="admissionStatusEditor" class="admission-status-editor" style="display: none;">
                    <select id="admissionStatusSelect">
                        <option value="true" ${ isBornDuringVisit ? 'selected' : '' }>${ ui.message("pihcore.inborn") }</option>
                        <option value="false" ${ !isBornDuringVisit ? 'selected' : '' }>${ ui.message("pihcore.outborn") }</option>
                    </select>
                    <button id="admissionStatusSaveBtn" class="admission-status-save-btn" onclick="saveAdmissionStatus()">${ ui.message("coreapps.save") }</button>
                </span>
                <% } %>
            </span>
        </div>
    </div>
</div>

<script type="text/javascript">
    var ACTIVE_VISIT_UUID = '${ activeVisitUuid }';
    var VISIT_ATTRIBUTE_UUID = '${ visitAttributeUuid }';
    var ATTRIBUTE_TYPE_UUID = '86f716fc-5e26-4eb1-9484-46370cff28f0';
    var INBORN_LABEL = '${ ui.message("pihcore.inborn") }';
    var OUTBORN_LABEL = '${ ui.message("pihcore.outborn") }';
    var INITIAL_VALUE = '${ isBornDuringVisit }';

    function showAdmissionStatusEditor() {
        jq('#admissionStatusDisplay').hide();
        jq('#editAdmissionStatusIcon').hide();
        jq('#admissionStatusEditor').show();
    }

    function hideAdmissionStatusEditor() {
        jq('#admissionStatusDisplay').show();
        jq('#editAdmissionStatusIcon').show();
        jq('#admissionStatusEditor').hide();
        // Reset select to initial value
        jq('#admissionStatusSelect').val(INITIAL_VALUE);
        var saveButton = jq('#admissionStatusSaveBtn');
        saveButton.removeClass('changed');
        saveButton[0].style.removeProperty('background-color');
        saveButton[0].style.removeProperty('background-image');
    }

    function saveAdmissionStatus() {
        var bornDuringVisit = jq('#admissionStatusSelect').val() === 'true';
        var saveButton = jq('#admissionStatusSaveBtn');

        // Disable button during save
        saveButton.prop('disabled', true);
        saveButton.text('${ ui.message("pihcore.saving") }');

        var requestData = {
            attributeType: ATTRIBUTE_TYPE_UUID,
            value: bornDuringVisit
        };

        jq.ajax({
            url: '/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/visit/' + ACTIVE_VISIT_UUID + '/attribute/' + VISIT_ATTRIBUTE_UUID,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            dataType: 'json',
            success: function(response) {
                // Update the display text
                var displayText = bornDuringVisit ? INBORN_LABEL : OUTBORN_LABEL;
                jq('#admissionStatusDisplay').text(displayText);
                emr.successMessage('${ ui.message("pihcore.admission.type.success") }', 3000, 'top', 'right');
                // Update initial value
                INITIAL_VALUE = bornDuringVisit.toString();
                // Hide editor and show edit icon
                hideAdmissionStatusEditor();

                // Re-enable button
                saveButton.prop('disabled', false);
                saveButton.text('${ ui.message("coreapps.save") }');
            },
            error: function(xhr, status, error) {
                console.error('Error updating admission status:', error);
                emr.errorMessage('${ ui.message("pihcore.admission.type.fail") }', 3000, 'top', 'right');

                // Re-enable button
                saveButton.prop('disabled', false);
                saveButton.text('${ ui.message("coreapps.save") }');
            }
        });
    }

    // Handle select change event
    jq(document).on('change', '#admissionStatusSelect', function() {
        var saveButton = jq('#admissionStatusSaveBtn');
        var btnEl = saveButton[0];
        if (jq(this).val() !== INITIAL_VALUE) {
            saveButton.prop('disabled', false);
            saveButton.addClass('changed');
            btnEl.style.setProperty('background-color', '#dc3545', 'important');
            btnEl.style.setProperty('background-image', 'none', 'important');
        } else {
            saveButton.removeClass('changed');
            btnEl.style.removeProperty('background-color');
            btnEl.style.removeProperty('background-image');
        }
    });
    // Close editor when clicking outside
    jq(document).on('click', function(event) {
        var editor = jq('#admissionStatusEditor');
        var icon = jq('#editAdmissionStatusIcon');

        if (editor.is(':visible')) {
            var isClickInside = editor.is(event.target) || editor.has(event.target).length > 0 ||
                icon.is(event.target) || icon.has(event.target).length > 0;

            if (!isClickInside) {
                hideAdmissionStatusEditor();
            }
        }
    });
</script>
<% } %>
<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("mirebalais.checkin.title") }", link: "${ ui.pageLink("coreapps", "findpatient/findPatient?app=" + appName) }" },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("coreapps", "patientdashboard/patientDashboard", [patientId: patient.id])}'},
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(function() {

        jq('#actions #cancel').click(function() {
            emr.navigateTo({
                provider: "coreapps",
                page: "findpatient/findPatient",
                query: {
                   app: "${ appName }"
                }
            });
        });

        jq('#actions #close-visit-and-continue').click(function() {
            emr.navigateTo({
                provider: "pihcore",
                page: "checkin/checkin",
                query: {
                    patientId: "${ patient.id }",
                    closeVisit: true
                }
            });
        });

        jq('#actions #continue').click(function() {
            var id = jq(this).attr('id');
            emr.navigateTo({
                provider: "registrationapp",
                page: "registrationSummary",
                query: {
                    patientId: "${ patient.id }",
                    appId: "registrationapp.registerPatient",
                    breadcrumbOverrideProvider: "coreapps",
                    breadcrumbOverridePage: "findpatient%2FfindPatient",
                    breadcrumbOverrideApp: "mirebalais.liveCheckin",
                    breadcrumbOverrideLabel: "mirebalais.app.patientRegistration.checkin.label"
                }
            });
        });
        jq('#actions button').first().focus();
    });
</script>

<%= ui.includeFragment("emr", "widget/note", [
        noteType: "warning",
        message: ui.message("mirebalais.checkin.newVisit"),
        additionalContent: """
                <div id="actions">
                    <button id="close-visit-and-continue" class="confirm medium right">
                        <i class="icon-arrow-right"></i>
                        ${ ui.message("pihcore.checkIn.closeVisitAndContinue") }
                    </button>

                    <button id="continue" class="confirm medium right">
                        <i class="icon-arrow-right"></i>
                        ${ ui.message("pihcore.checkIn.continue") }
                    </button>

                    <button id="cancel" class="cancel medium">
                        <i class="icon-arrow-left"></i>
                        ${ ui.message("pihcore.checkIn.back") }
                    </button>
                </div>
            """
]) %>

<% if (existingEncounters.size() > 0) { %>

<div id="existing-encounters">
    <h3>${ ui.message("mirebalais.checkin.checkinThisVisit") }</h3>
    <table>
        <thead>
        <tr>
            <th>${ ui.message("mirebalais.outpatientVitals.when") }</th>
            <th>${ ui.message("mirebalais.outpatientVitals.where") }</th>
            <th>${ ui.message("mirebalais.outpatientVitals.enteredBy") }</th>
        </tr>
        </thead>
        <tbody>
        <% if (existingEncounters.size() == 0) { %>
        <tr>
            <td colspan="3">${ ui.message("coreapps.none") }</td>
        </tr>
        <% } %>
        <% existingEncounters.each { enc ->
            def minutesAgo = (long) ((System.currentTimeMillis() - enc.encounterDatetime.time) / 1000 / 60)
        %>
        <tr>
            <td>${ ui.message("mirebalais.outpatientVitals.minutesAgo", minutesAgo) }</td>
            <td>${ ui.format(enc.location) }</td>
            <td>${ ui.format(enc.creator) }</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<% } %>
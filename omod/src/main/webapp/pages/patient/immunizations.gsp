<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.immunizations") }"}
    ];

    jq(document).ready(function() {
        jq("#return-button").click(function (event) {
            document.location.href = '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}';
        });
    });
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<h3>${ ui.message("pihcore.immunization.history") }</h3>

<div class="container">

    <table>
        <thead>
            <th>${ui.message("pihcore.immunizations")}</th>
            <% immunizationSequenceNumbers.keySet().each{ sequenceHeader -> %>
                <th>${ui.message(sequenceHeader)}</th>
            <% } %>
        </thead>
        <tbody>
            <% immunizationConcepts.each{ immunizationConcept -> %>
                <tr>
                    <td>${ui.message("pihcore.concept.name." + immunizationConcept.uuid)}</td>
                    <% immunizationSequenceNumbers.values().each{ sequenceNumber ->
                        def immunizationKey = immunizationConcept.uuid + "|" + sequenceNumber
                        def immunization = immunizations.remove(immunizationKey)
                    %>
                        <td>
                            <% if (immunization != null) { %>
                                ${ui.formatDatePretty(immunization.dateObs?.valueDatetime)}
                            <% } %>
                        </td>
                    <% } %>
                </tr>
            <% } %>

        </tbody>

    </table>

    <br/>

    <div>
        <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
    </div>

</div>



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

<style>
    .vaccination-table th, td {
        font-size: 0.9em;
        text-align: center;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<h3>${ ui.message("pihcore.immunization.history") }</h3>

<div class="container">

    <table class="vaccination-table">
        <thead>
            <th>${ui.message("pihcore.immunizations")}</th>
            <% immunizationSequenceNumbers.values().each{ sequenceHeader -> %>
                <th>${ui.message(sequenceHeader)}</th>
            <% } %>
        </thead>
        <tbody>
            <% immunizationConcepts.keySet().each{ immunizationConcept ->
                def supportedDoses = Arrays.asList(immunizationConcepts.get(immunizationConcept)) %>
                <tr>
                    <td style="font-weight: bold;">${ui.message("pihcore.concept.name." + immunizationConcept.uuid)}</td>
                    <% immunizationSequenceNumbers.keySet().each{ sequenceNumber ->
                        def immunizationKey = immunizationConcept.uuid + "|" + sequenceNumber
                        def immunization = immunizations.remove(immunizationKey)
                        def supported = sequenceNumber != null && supportedDoses.contains(sequenceNumber)
                    %>
                        <td ${supported ? "" : "style='background-color: #888;'"}>
                            <% if (immunization != null) { %>
                                ${ui.formatDatePretty(immunization.dateObs?.valueDatetime)}
                            <% } %>
                        </td>
                    <% } %>
                </tr>
            <% } %>

        </tbody>

    </table>

    <!-- This only exists as a safety net in case data exists that does not fit in the above table -->
    <% if (!immunizations.isEmpty()) { %>

        <h4>${ui.message("pihcore.other")}</h4>

        <table>
            <thead>
                <tr>
                    <th>${ui.message("pihcore.immunization")}</th>
                    <th>${ui.message("pihcore.vaccination.sequence.dose")}</th>
                    <th>${ui.message("pihcore.vaccination.whenWasItGiven")}</th>
                </tr>
            </thead>
            <tbody>
                <% immunizations.values().each{ immunization -> %>
                    <tr>
                        <td>${ui.message("pihcore.concept.name." + immunization.immunizationObs.valueCoded.uuid)}</td>
                        <td>
                            <%
                                def sequenceNum = immunization.sequenceNumberObs?.valueNumeric
                                if (sequenceNum) {
                                    sequenceNum = sequenceNum.intValue();
                                }
                                if (immunizationSequenceNumbers.containsKey(sequenceNum)) {
                                    sequenceNum = ui.message(immunizationSequenceNumbers.get(sequenceNum))
                                }
                            %>
                            ${sequenceNum}
                        </td>
                        <td>${ui.formatDatePretty(immunization.dateObs?.valueDatetime)}</td>
                    </tr>
                <% } %>
            </tbody>
        </table>

    <% } %>

    <br/>

    <div>
        <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
    </div>

</div>



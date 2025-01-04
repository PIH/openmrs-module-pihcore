<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.vaccinations") }"}
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

<h3>${ ui.message("pihcore.vaccination.history") }</h3>

<div class="container">

    <table class="vaccination-table">
        <thead>
            <th>${ui.message("pihcore.vaccinations")}</th>
            <% vaccinationSequenceNumbers.values().each{ sequenceHeader -> %>
                <th>${ui.message(sequenceHeader)}</th>
            <% } %>
        </thead>
        <tbody>
            <% vaccinationConcepts.keySet().each{ vaccinationConcept ->
                def supportedDoses = Arrays.asList(vaccinationConcepts.get(vaccinationConcept)) %>
                <tr>
                    <td style="font-weight: bold;">${ui.message("pihcore.concept.name." + vaccinationConcept.uuid)}</td>
                    <% vaccinationSequenceNumbers.keySet().each{ sequenceNumber ->
                        def vaccinationKey = vaccinationConcept.uuid + "|" + sequenceNumber
                        def vaccination = vaccinations.remove(vaccinationKey)
                        def supported = sequenceNumber != null && supportedDoses.contains(sequenceNumber)
                    %>
                        <td ${supported ? "" : "style='background-color: #888;'"}>
                            <% if (vaccination != null) { %>
                                ${ui.formatDatePretty(vaccination.dateObs?.valueDatetime)}
                            <% } %>
                        </td>
                    <% } %>
                </tr>
            <% } %>

        </tbody>

    </table>

    <!-- This only exists as a safety net in case data exists that does not fit in the above table -->
    <% if (!vaccinations.isEmpty()) { %>

        <h4>${ui.message("pihcore.other")}</h4>

        <table>
            <thead>
                <tr>
                    <th>${ui.message("pihcore.vaccination")}</th>
                    <th>${ui.message("pihcore.vaccination.sequence.dose")}</th>
                    <th>${ui.message("pihcore.vaccination.whenWasItGiven")}</th>
                </tr>
            </thead>
            <tbody>
                <% vaccinations.values().each{ vaccination -> %>
                    <tr>
                        <td>${ui.message("pihcore.concept.name." + vaccination.vaccinationObs.valueCoded.uuid)}</td>
                        <td>
                            <%
                                def sequenceNum = vaccination.sequenceNumberObs?.valueNumeric
                                if (sequenceNum) {
                                    sequenceNum = sequenceNum.intValue();
                                }
                                if (vaccinationSequenceNumbers.containsKey(sequenceNum)) {
                                    sequenceNum = ui.message(vaccinationSequenceNumbers.get(sequenceNum))
                                }
                            %>
                            ${sequenceNum}
                        </td>
                        <td>${ui.formatDatePretty(vaccination.dateObs?.valueDatetime)}</td>
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



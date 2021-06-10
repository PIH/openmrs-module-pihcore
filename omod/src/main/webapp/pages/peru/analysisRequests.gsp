<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<style>
    form > section > *:not(script)  {
        float: left;
    }
    form>section > button{
        margin-top:24px;
    }
    table{
        margin-top:16px;
    }
    
</style>
<!-- NOTE:  if you are using the OpenMRS SDK and "watching" this module, you should be able to "hot" reload this page and it should automatically pick up changes you've made -->

<!-- feel free to convert this and the other text to Spanish (though ideally all would use message codes, see "ui.message" below) -->
<h3>Ordenes de Laboratorio</h3>

<form method="get">
    <section>
        ${ui.includeFragment("uicommons", "field/datetimepicker", [
                        label        : "Desde",
                        formFieldName: "startDate",
                        left         : true,
                        defaultDate  : null,
                        useTime      : false,
                        showEstimated: false,
                        initialValue : new Date(),
                        id           : 'start-date'
                ])}
        ${ui.includeFragment("uicommons", "field/datetimepicker", [
                        label        : "Hasta",
                        formFieldName: "endDate",
                        left         : true,
                        defaultDate  : null,
                        useTime      : false,
                        showEstimated: false,
                        initialValue : new Date(),
                        id           : 'end-date'
                ])}
        <button>Buscar</button>
    </section>
</form>

<!-- now we can work with the "encounters" list we added to the model in the controller -->
<% if (encounters.isEmpty()) { %>
    <!-- "ui" is special object wired into the page view that provides various methods for formatting/displaying data -->
    <!-- "message" loads and displays the appropriate message code based on locale -->
    <!-- the underlying Java class backing "ui" shows the available methods: https://github.com/openmrs/openmrs-module-uiframework/blob/master/api/src/main/java/org/openmrs/ui/framework/UiUtils.java -->
    <span>${ ui.message("coreapps.none") }</span>
<% } else { %>
    <table>
        <tr>
            <th>
                Fecha
            </th>
            <th>
                Paciente
            </th>
            <th>
                Numero de ordenes
            </th>
            <th>
                Nombre de muestra
            </th>
            <th>
                Local
            </th>
            <th>
                Registrado por
            </th>
            <th>
                Acción
            </th>
        </tr>
        <!-- iterate over the encounters and create a row for each -->
        <%  encounters.each { encounter -> %>
            <tr>
                <td>
                    <!-- "format" is a general method for formatting most types of OpenMRS objects for display) -->
                   ${ ui.format(encounter.encounterDatetime) }
                </td>
                <td>
                    ${ ui.format(encounter.patient) }
                </td>
                <td>
                   ${ encounter.orders ? encounter.orders.size() : "" }
                </td>
                <td>
                    ${ ui.format(encounter.encounterType)}
                </td>
                <td>
                    ${ ui.format(encounter.location)}
                </td>
                <td>
                    ${ ui.format(encounter.creator)}
                </td>
                <td style="white-space: nowrap;">
                        <a href="${ ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi",
                                    [
                                        "patientId": encounter.patient.uuid,
                                        "encounterId": encounter.uuid,
                                        "returnUrl": ui.escapeJs(ui.pageLink("pihcore", "/openmrs/pihcore/visit/visit.page", [ "patient": encounter.patient.uuid] )),
                                        "definitionUiResource": "file:configuration/pih/htmlforms/sampleCollection.xml"
                                    ]
                        )}">
                            Ingresar Orden
                        </a>
                </td>
            </tr>
        <% } %>
    </table>
<% } %>

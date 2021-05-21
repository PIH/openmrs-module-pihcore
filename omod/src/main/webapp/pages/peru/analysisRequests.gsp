<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<style>
    form > section > *  {
        display: initial;
        min-width: unset;
    }
</style>
<!-- NOTE:  if you are using the OpenMRS SDK and "watching" this module, you should be able to "hot" reload this page and it should automatically pick up changes you've made -->

<!-- feel free to convert this and the other text to Spanish (though ideally all would use message codes, see "ui.message" below) -->
<h3>Ordenes de Laboratorio</h3>

<form method="get">
    <section>
        <label>Desde:</label>
        <input type="date" name="startDate" />
        <label>&nbsp;Hasta:</label>
        <input type="date" name="endDate" />
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
                <td>
                    <a href="http://localhost:8080/openmrs/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId=${encounter.patient.uuid}&encounterId=${encounter.uuid}&definitionUiResource=file:configuration/pih/htmlforms/sampleCollection.xml">Ingresar orden</a>
                </td>
            </tr>
        <% } %>
    </table>
<% } %>

<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<!-- NOTE:  if you are using the OpenMRS SDK and "watching" this module, you should be able to "hot" reload this page and it should automatically pick up changes you've made -->

<!-- feel free to convert this and the other text to Spanish (though ideally all would use message codes, see "ui.message" below) -->
<h3>Test Orders</h3>

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
                Date
            </th>
            <th>
                Patient
            </th>
            <th>
                Number of Orders
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
            </tr>
        <% } %>
    </table>
<% } %>

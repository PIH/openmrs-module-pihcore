<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")

%>
<style>
    .date-column {
        width: 125px;
    }
    .name-link {
        cursor:pointer;
        color:blue;
        text-decoration:underline;
    }
    .boundary {
        margin-top: 10px;
        margin-bottom: 10px;
    }

    .row {
        display: flex;
    }
    /* Create two columns that sits next to each other */
    .left-column {
        flex: 40%;
        float: left;
        padding: 10px;
    }
    .right-column {
        flex: 60%;
        float: right;
        padding: 10px;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("registration.patient.children.label") }" , link: '${ui.pageLink("pihcore", "children/children", ["patientId": patient.id])}'}
    ];

    jq(document).ready(function() {
        jq("#return-button").click(function(event) {
            document.location.href = '${ ui.escapeJs(returnUrl) }';
        });
    });

    const contextPath = window.location.href.split('/')[3];
    const apiBaseUrl =  "/" + contextPath + "/ws/rest/v1";

    let deleteChildDialog = null;
    function navigateBackToChildren() {
        emr.navigateTo({
            provider: "pihcore",
            page: "children/children",
            query: {
                patientId: '${ patient.patientId }',
                rerturnUrl: '${ ui.escapeJs(returnUrl) }'
            }
        });
    }
    function initDeleteChildDialog(relationshipUuid) {
        deleteChildDialog = emr.setupConfirmationDialog({
            selector: '#delete-child-dialog',
            actions: {
                confirm: function() {
                    jq.ajax({
                        type:"DELETE",
                        url: apiBaseUrl + "/relationship/" + relationshipUuid })
                        .fail(function(data) {
                            emr.errorMessage("Failed to delete relationship: " + data.responseText);
                        })
                        .success(function(data) {
                            emr.successMessage("Relationship has been deleted");

                    }).always(function(){
                        deleteChildDialog.close();
                        jq("#childName").text("");
                        setTimeout(navigateBackToChildren, 1000);  // set a delay so that the toast message has time to display before the redirect
                    });
                },
                cancel: function() {
                    deleteChildDialog.close();
                    jq("#childName").text("");
                }
            }
        });

        deleteChildDialog.show();
    }
    function deleteChildRelationship(relationshipUuid, childName) {
        jq("#childName").text(childName);
        initDeleteChildDialog(relationshipUuid);
        deleteChildDialog.show();
    }

    function searchChildren() {
        emr.navigateTo({
            provider: "pihcore",
            page: "children/findChildren",
            query: {
                patientId: '${ patient.patientId }',
                rerturnUrl: '${ ui.escapeJs(returnUrl) }'
            }
        });
    }
</script>

<div id="delete-child-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="fas fa-fw fa-child"></i>
        <h3>${ ui.message("pihcore.children.delete") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("pihcore.children.delete.confirmTitle") }:</p>
        <ul>
            <li class="info">
                <span>${ ui.message("pihcore.child") }</span>
                <h5 id="childName"></h5>
            </li>
            <li class="info">
                <span>${ ui.message("pihcore.mother") }</span>
                <h5>${ ui.format(patient) }</h5>
            </li>
        </ul>

        <button class="confirm right">${ ui.message("uicommons.yes") }</button>
        <button class="cancel">${ ui.message("uicommons.no") }</button>
    </div>
</div>

<div clas="row-lb">
    <div class="left-column">
        <h3>${ ui.message("registration.patient.children.label") }</h3>
    </div>
    <div class="right-column">
        <input id="searchChildren" type="button" value="${ ui.message("pihcore.children.searchAndRegister") }" onclick="searchChildren();" />
    </div>
</div>

<table id="children-list-table">
    <thead>
    <tr>
        <th>${ ui.message("pihcore.children.name") }</th>
        <th>${ ui.message("pihcore.birthdate") }</th>
        <th>${ ui.message("pihcore.age") }</th>
        <th>${ ui.message("pihcore.gender") }</th>
        <th>${ ui.message("mirebalais.deathCertificate.date_of_death") }</th>
        <th>${ ui.message("pihcore.children.removeRelationship") }</th>
    </tr>
    </thead>
    <tbody>
        <% if (children.size() == 0) {  %>
            <tr>
                <td colspan="6">${ ui.message("emr.none") }</td>
            </tr>
        <% } %>
    </tbody>
    <% children.keySet().each { relationship ->
        def child = children.get(relationship);
    %>
            <tr>
                <td class="name-link"><a href="${ ui.urlBind("/" + contextPath + dashboardUrl, [ patientId: child.personId ]) }">${ child.givenName }, ${ child.familyName }</a></td>
                <td class="date-column">${ ui.format(child.birthdate) }</td>
                <td>${child.age}</td>
                <td>${child.gender}</td>
                <td class="date-column">${ ui.format(child.deathDate) }</td>
                <td onclick="javascript:deleteChildRelationship('${ relationship }', '${ child.givenName }' + ', ' + '${ child.familyName }')"><i class="icon-remove delete-action"></i></td>
            </tr>
    <% } %>
</table>
<div class="boundary"></div>
<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>

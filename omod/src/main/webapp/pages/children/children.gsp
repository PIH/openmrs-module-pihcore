<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")

    import groovy.json.JsonSlurper
    import groovy.json.JsonOutput

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

<div clas="row">
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

<div style="margin-top: 30px; margin-bottom: 20px;">
    <h3>${ ui.message("registration.patient.unregistered.babies") }</h3>
    <span>${ ui.message("registration.patient.unregistered.babies.msg") }</span>
</div>
<table id="unregistered-babies-table">
    <thead>
        <tr>
            <th>${ ui.message("pihcore.delivery.form") }</th>
            <th>${ ui.message("pihcore.encounterList.enteredDatetime") }</th>
            <th>${ ui.message("pihcore.provider") }</th>
            <th>${ ui.message("pihcore.birthdate") }</th>
            <th>${ ui.message("pihcore.gender") }</th>
            <th>${ ui.message("registration.patient.register.baby") }</th>
            <th>${ ui.message("registration.patient.register.link") }</th>
            <th>${ ui.message("registration.patient.remove") }</th>
        </tr>
    </thead>
    <tbody>
        <% if (unregisteredBabies.size() == 0) { %>
        <tr>
            <td colspan="8">${ ui.message("emr.none") }</td>
        </tr>
        <% } %>
        <% unregisteredBabies.each { e ->
            def jsonSlurper = new JsonSlurper()
            def regValues = jsonSlurper.parseText(initialRegistrationValues);
            regValues.put("demographics.demographics-gender.gender", e.gender)
            def birthDate = new Date(Date.parse(e.birthDatetime))
            regValues.put("demographics.demographics-birthdate.birthdateDay", birthDate[Calendar.DAY_OF_MONTH])
            regValues.put("demographics.demographics-birthdate.birthdateMonth", birthDate[Calendar.MONTH] + 1)
            regValues.put("demographics.demographics-birthdate.birthdateYear", birthDate[Calendar.YEAR])
            def updatedValues = JsonOutput.toJson(regValues)
            def childrenPageReturnUrl = "pihcore/children/children.page?patientId=" + e.patientUuid + "&registerBabyObs=" + e.registerBabyObs

            def pageLink = ui.pageLink("htmlformentryui", "htmlform/editHtmlFormWithStandardUi", [
                        "patientId": e.patientUuid,
                        "encounterId": e.encounterUuid,
                        "returnProvider": "pihcore",
                        "returnPage": "children/children"])
        %>
        <tr id="encounter-${ e.encounterId }" class="encounter-row${pageLink ? ' pointer' :''}" data-href="${pageLink}">
            <td>
                ${ ui.message("pih.task.summaryLandD") }
            </td>
            <td class="date-column">
                ${ ui.format(e.encounterDatetime) }
            </td>
            <td>
                ${ ui.format(e.provider) }
            </td>
            <td class="date-column">
                ${ ui.format(e.birthDatetime) }
            </td>
            <td>
                ${ui.format(e.gender)  }
            </td>
            <td>
                <a href="${ ui.pageLink("registrationapp", "registerPatient", [ appId: 'registrationapp.registerPatient', breadcrumbOverride: '', mother: e.patientUuid, initialValues: updatedValues, goToSectionId: "idcardLabel", returnUrl: childrenPageReturnUrl]) }">
                    <button id="register-child-button">${ ui.message("Register") }</button>
                </a>
            </td>
            <td>
                <input type="button" value="Link">
            </td>
            <td >
                <input type="button" value="X">
            </td>
        </tr>
    <% } %>
    </tbody>
</table>
<div class="boundary"></div>
<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>


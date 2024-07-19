<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")

    import groovy.json.JsonSlurper
    import groovy.json.JsonOutput

    def patientDashboardLink = ui.pageLink("coreapps", "clinicianfacing/patient")
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

    .matchingPatientContainer .container {
        overflow: hidden;
    }

    .matchingPatientContainer .container div {
        margin: 5px 10px;
    }

    .matchingPatientContainer .container .name {
        font-size: 25px;
        display: inline-block;
    }

    .matchingPatientContainer .container .info .address{
        font-size: 15px;
        display: inline-block;
    }

    .matchingPatientContainer .container .identifiers {
        font-size: 15px;
        display:inline-block;
        min-width: 600px;
    }

    .matchingPatientContainer .container .identifiers .idName {
        font-size: 15px;
        font-weight: bold;
    }

    .matchingPatientContainer .container .identifiers .idValue {
        font-size: 15px;
        margin: 0 20px 0 0;
    }

    #matchingPatientsResults {
        padding: 0px;
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

    const isBabyRegisteredConceptUuid = "23eeeec5-7f82-4bea-8bdf-f959900882e7";
    const yesConceptUuid = "3cd6f600-26fe-102b-80cb-0017a47871b2";
    const doNotRegisterConceptUuid = "3cd7b72a-26fe-102b-80cb-0017a47871b2";
    const contextPath = window.location.href.split('/')[3];
    const apiBaseUrl =  "/" + contextPath + "/ws/rest/v1";

    let patientDashboardLink = '${patientDashboardLink}';

    let deleteChildDialog = null;
    let linkChildDialog = null;
    let removeDeliveryEntryDialog = null;

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

    function initRemoveDeliveryEntryDialog(registerBabyObs) {
        removeDeliveryEntryDialog = emr.setupConfirmationDialog({
            selector: '#remove-delivery-entry-dialog',
            actions: {
                confirm: function() {
                    let updatedObs = {
                        concept : isBabyRegisteredConceptUuid,
                        value   : doNotRegisterConceptUuid,
                        comment : "do not register delivery"
                    };
                    let dataJson = JSON.stringify(updatedObs);
                    jq.ajax({
                        type: "POST",
                        url: apiBaseUrl + "/obs/" + registerBabyObs,
                        dataType: "json",
                        contentType: "application/json",
                        data: dataJson
                    })
                        .fail(function (data) {
                            emr.errorMessage("Failed to update Labor and Delivery Register baby in EMR obs: " + data.responseText);
                        })
                        .success(function (data) {
                            emr.successMessage("Labor encounter delivery was marked as not to register");
                        }).always(function () {
                        setTimeout(navigateBackToChildren, 1000);  // set a delay so that the toast message has time to display before the redirect
                        removeDeliveryEntryDialog.close();
                    });
                },
                cancel: function() {
                    removeDeliveryEntryDialog.close();
                }
            }
        });

        removeDeliveryEntryDialog.show();
    }

    function doNotRegisterChild(registerBabyObs) {
        if (registerBabyObs) {
            initRemoveDeliveryEntryDialog(registerBabyObs);
            removeDeliveryEntryDialog.show();
        }
    }

    function linkBabyToDeliveryForm(registerBabyObs, babyUuid) {
        if (registerBabyObs && babyUuid) {
            let updatedObs = {
                concept : isBabyRegisteredConceptUuid,
                value   : yesConceptUuid,
                comment : babyUuid
            };
            let dataJson = JSON.stringify(updatedObs);
            jq.ajax({
                type: "POST",
                url: apiBaseUrl + "/obs/" + registerBabyObs,
                dataType: "json",
                contentType: "application/json",
                data: dataJson
            })
                .fail(function (data) {
                    emr.errorMessage("Failed to update Labor and Delivery Register baby in EMR obs: " + data.responseText);
                })
                .success(function (data) {
                    emr.successMessage("Labor encounter has been linked to registered baby");
                }).always(function () {
                    linkChildDialog.close();
                    setTimeout(navigateBackToChildren, 1000);  // set a delay so that the toast message has time to display before the redirect
            });
        }
    }

    function showSimilarPatients(data, registerBabyObs) {
        if (data.length == 0 ) {
            jq("#similarPatientsSlideView").hide();
            jq("#noMatchingResults").show();
            return;
        } else {
            jq("#noMatchingResults").hide();
            jq("#similarPatientsSlideView").show();
        }

        jq('#similarPatientsCount').text(data.length);
        var similarPatientsSelect = jq('#similarPatientsSelect');
        similarPatientsSelect.empty();
        for (index in data) {
            let item = data[index];
            var container = jq('#matchedPatientTemplates .container');
            var cloned = container.clone();

            cloned.find('.name').append(item.givenName + ' ' + item.familyName);

            let gender = item.gender;

            var attributes = "";
            if (item.attributeMap) {
                _.each(item.attributeMap, function(value, key) {
                    if (value) {
                        attributes = attributes + ", " + value;
                    }
                });
            }
            cloned.find('.info').append(gender + ', ' + item.birthdate);
            cloned.find('.address').append(item.personAddress + ', ' + attributes);

            if (item.identifiers) {
                var identifiers = cloned.find('.identifiers');
                item.identifiers.forEach(function (entry) {
                    var clonedIdName = identifiers.find('.idNameTemplate').clone();
                    clonedIdName.text(entry.name + ': ');
                    clonedIdName.removeClass("idNameTemplate");
                    identifiers.append(clonedIdName);

                    var clonedIdValue = identifiers.find(".idValueTemplate").clone();
                    clonedIdValue.text(entry.value);
                    clonedIdValue.removeClass("idValueTemplate");
                    identifiers.append(clonedIdValue);
                });
            }

            let button = jq('#matchedPatientTemplates .local_button').clone();
            var link = patientDashboardLink;
            link += (link.indexOf('?') == -1 ? '?' : '&') + 'patientId=' + item.uuid;
            button.attr("onclick", "linkBabyToDeliveryForm('" + registerBabyObs + "', '" + item.uuid + "')");

            cloned.append(button);

            jq('#similarPatientsSelect').append(cloned);
        }
    }

    function initLinkChildDialog() {
        linkChildDialog = emr.setupConfirmationDialog({
            selector: '#link-child-dialog',
            actions: {
                confirm: function() {
                    console.log("Search for registered children");
                },
                cancel: function() {
                    linkChildDialog.close();
                }
            }
        });
        linkChildDialog.show();
    }

    function searchRegisteredChild(gender, birthdateDay, birthdateMonth, birthdateYear, familyName, registerBabyObs) {
        let formData = "familyName=" + familyName + "&givenName=Baby&middleName=&preferred=true&unknown=false&"
            + "gender=" + gender
            + "&birthdateDay=" + birthdateDay
            + "&birthdateMonth=" + birthdateMonth
            + "&birthdateYear=" + birthdateYear
            + "&birthdate=" + birthdateYear + "-" + birthdateMonth + "-" + birthdateDay;

        let searchUrl = '/' + OPENMRS_CONTEXT_PATH + '/registrationapp/matchingPatients/getSimilarPatients.action?appId=registrationapp.registerPatient';
        jq.post(searchUrl, formData, function(data) {
            showSimilarPatients(data, registerBabyObs);
            initLinkChildDialog();
            linkChildDialog.show();
        }, "json");
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

<div id="remove-delivery-entry-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="fas fa-fw fa-child"></i>
        <h3>${ ui.message("pihcore.delivery.register") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("pihcore.donotregister.delivery") }?</p>

        <button class="confirm right">${ ui.message("uicommons.yes") }</button>
        <button class="cancel">${ ui.message("uicommons.no") }</button>
    </div>
</div>

<div id="matchedPatientTemplates" style="display:none;">
    <div class="container"
         style="border-color: #00463f; border-bottom: solid; border-width:2px; margin-bottom: 5px;">
        <div class="name"></div>
        <div class="info"></div>
        <div class="address"></div>
        <div class="identifiers">
            <span class="idName idNameTemplate"></span><span class="idValue idValueTemplate"></span>
        </div>
    </div>
    <button class="local_button" style="float:right; margin:10px; padding: 2px 8px" onclick="location.href=''">
        ${ui.message("pihcore.children.select.patient")}
    </button>
</div>

<div id="link-child-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="fas fa-fw fa-child"></i>
        <h3>${ ui.message("pihcore.children.potential.match") }</h3>
    </div>
    <div id="matchingPatientsResults" class="dialog-content">
        <div id="noMatchingResults" style="display: none;margin-bottom: 30px;">
            <span>${ ui.message("emr.none") }</span>
        </div>
        <div id="similarPatientsSlideView" style="display: none; height: 360px; overflow-x: hidden; overflow-y: scroll; margin-bottom: 30px; text-align: justify;">
            <ul id="similarPatientsSelect" class="matchingPatientContainer select" style="width: auto;"></ul>
        </div>

        <button class="confirm right">${ ui.message("pihcore.children.continue.search") }</button>
        <button class="cancel">${ ui.message("general.cancel") }</button>
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
        <th>${ ui.message("pihcore.children.removeChild") }</th>
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
            def birthdateDay = birthDate[Calendar.DAY_OF_MONTH]
            def birthdateMonth = birthDate[Calendar.MONTH] + 1
            def birthdateYear = birthDate[Calendar.YEAR]
            regValues.put("demographics.demographics-birthdate.birthdateDay", birthdateDay)
            regValues.put("demographics.demographics-birthdate.birthdateMonth", birthdateMonth)
            regValues.put("demographics.demographics-birthdate.birthdateYear", birthdateYear)
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
                <button id="link-child-button" onclick="javascript:searchRegisteredChild('${ e.gender }', '${ birthdateDay }' , '${ birthdateMonth }', '${ birthdateYear }', '${ patient.familyName }', '${ e.registerBabyObs }')">${ ui.message("coreapps.findPatient.search") }</button>
            </td>
            <td >
                <input type="button" onclick="javascript:doNotRegisterChild('${ e.registerBabyObs }')"value="X">
            </td>
        </tr>
    <% } %>
    </tbody>
</table>
<div class="boundary"></div>
<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>


<%
    ui.decorateWith("appui", "standardEmrPage")

    def appId = 'registrationapp.registerPatient'
    def baseUrl = ui.pageLink("registrationapp", "findPatient", [appId: appId])
    def afterSelectedUrl = '/pihcore/children/children.page?patientId={{patientId}}'
%>

<script type="text/javascript">
    let breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("registration.patient.children.label") }" , link: '${ui.pageLink("pihcore", "children/children", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.findChild") }" , link: '${ui.pageLink("pihcore", "children/findChildren", ["patientId": patient.id])}'}
    ];

    jq(document).ready(function() {
        jq("#return-button").click(function(event) {
            window.history.back();
        });

        jq('#patient-search').focus();
    });

    const contextPath = window.location.href.split('/')[3];
    const apiBaseUrl =  "/" + contextPath + "/ws/rest/v1";
    let addChildDialog = null;

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

    function initAddChildDialog(childUuid) {
        let newRelationship = {
            relationshipType: '${ motherToChildRelationshipType.uuid }',
            personA: '${ patient.uuid }',
            personB: childUuid
        };
        let dataJson = JSON.stringify(newRelationship);
        addChildDialog = emr.setupConfirmationDialog({
            selector: '#add-child-dialog',
            actions: {
                confirm: function() {
                    jq.ajax({
                        url: apiBaseUrl + "/relationship",
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        data: dataJson,
                        success: function( data ) {
                            emr.successMessage('${ ui.message("pihcore.relationship.created") }');
                            addChildDialog.close();
                            jq("#childName").text("");
                            setTimeout(navigateBackToChildren, 1000);
                        }
                    }).fail(function (data) {
                        emr.errorMessage('${ ui.message("pihcore.relationship.failToCreate") }' + ": " + data.responseText);
                        addChildDialog.close();
                        jq("#childName").text("");
                    });
                },
                cancel: function() {
                    addChildDialog.close();
                    jq("#childName").text("");
                }
            }
        });

    }
    function addChild(childUuid, childName) {
        jq("#childName").text(childName);
        initAddChildDialog(childUuid);
        addChildDialog.show();
    }
    let selectPatientHandler = {
        handle: function (row, widgetData) {
            addChild(row.uuid, row.person.personName.display + ' (' + row.person.gender + ', ' + row.person.age + ')');
        }
    }
</script>

<div id="add-child-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="fas fa-fw fa-child"></i>
        <h3>${ ui.message("pihcore.children.add") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("pihcore.children.add.confirmTitle") }:</p>
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
<div class="mx-2">
    <div class="row mt-2">
        <div class="col-8 col-md-4 order-md-last mb-4">
            <a href="${ ui.pageLink("registrationapp", "registerPatient", [ appId: appId, breadcrumbOverride: '', initialValues: initialRegistrationValues]) }">
                <button id="register-patient-button">${ ui.message("registrationapp.new.registration") }</button>
            </a>
        </div>
        <div class="col-12"><h3>${ ui.message("coreapps.searchPatientHeading") }</h3></div>
        <div class="col-12 col-md-8">
            ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
                    [ afterSelectedUrl: afterSelectedUrl,
                      rowSelectionHandler: "selectPatientHandler",
                      initialSearchFromParameter: "search",
                      showLastViewedPatients: 'false' ])}
        </div>
    </div>

</div>

<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("pihcore.encounterList.return") }"/>
</div>

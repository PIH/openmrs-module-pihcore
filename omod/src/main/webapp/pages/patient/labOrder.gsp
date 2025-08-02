<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "labOrder.css")
%>
<script type="text/javascript">

    const selectedTests = [];
    const testNames = new Map();
    const testsByPanel = new Map();
    <% labSet.setMembers.each { category -> %>
        <% category.setMembers.each { orderable -> %>
            testNames.set('${ orderable.uuid }', '${ pihui.getBestShortName(category) }');
            <% if (orderable.isSet()) { %>
                testsByPanel.set('${ orderable.uuid }', new Set());
                <% orderable.setMembers.each { test -> %>
                    testsByPanel.get('${ orderable.uuid }').add('${ test.uuid }');
                <% } %>
            <% } %>
        <% } %>
    <% } %>

    function changeCategory(categoryUuid) {
        jQuery(".category-link").removeClass("active-category");
        jQuery("#category-link-" + categoryUuid).addClass("active-category");
        jQuery(".lab-selection-form").hide();
        jQuery("#lab-selection-form-" + categoryUuid).show();
    }

    function togglePanel(panelUuid) {
        const existingIndex = selectedTests.indexOf(panelUuid);
        const addingPanel = (existingIndex < 0);
        if (addingPanel) {
            selectedTests.push(panelUuid);
            jq("#panel-button-" + panelUuid).addClass("active").blur();
        }
        else {
            selectedTests.splice(existingIndex, 1);
            jq("#panel-button-" + panelUuid).removeClass("active").blur();
        }
        testsByPanel.get(panelUuid).forEach((testUuid) => {
            if (addingPanel) {
                addTest(testUuid);
            }
            else {
                removeTest(testUuid);
            }
        })
    }

    function toggleTest(testUuid) {
        const existingIndex = selectedTests.indexOf(testUuid);
        if (existingIndex < 0) {
            addTest(testUuid);
        }
        else {
            removeTest(testUuid);
        }
    }

    function removeTest(testUuid) {
        let addedFromPanel = false;
        selectedTests.forEach((selectedTest) => {
            testsByPanel.get(selectedTest)?.forEach((testInPanel) => {
                if (testInPanel === testUuid) {
                    addedFromPanel = true;
                }
            })
        });
        if (!addedFromPanel) {
            selectedTests.splice(selectedTests.indexOf(testUuid), 1);
            jq("#test-button-" + testUuid).removeClass("active");
        }
        jq("#test-button-" + testUuid).blur();
    }

    function addTest(testUuid) {
        if (selectedTests.indexOf(testUuid) < 0) {
            selectedTests.push(testUuid);
            jq("#test-button-" + testUuid).addClass("active");
        }
        jq("#test-button-" + testUuid).blur()
    }

    jQuery(document).ready(function () {
        jQuery(".lab-selection-form").hide();
        <% if (labSet && !labSet.setMembers.isEmpty()) { %>
            changeCategory('${labSet.setMembers.get(0).uuid}');
        <% } %>
    })
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.format(patient.patient)) }" , link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'},
        { label: "${ ui.message("pihcore.labOrder") }" , link: '${ui.pageLink("pihcore", "patient/labOrder", ["patientId": patient.id])}'}
    ];
</script>

<style>
    legend {
        text-align: left; padding: 0 10px; font-weight: 300; font-size: 16px; text-transform: uppercase; width: unset;
    }
</style>

<div class="row">
    <div class="col-12 col-md-8">
        <div class="lab-order-entry">
            <h3>${ ui.message("pih.app.labs.ordering") }</h3>

            <% if (!labSet) { %>

                Lab Ordering is not configured properly, see your system administrator

            <% } else { %>

                <div class="row">
                    <div class="col-12 col-sm-4 col-md-5 lab-category">
                        <ul>
                            <% labSet.setMembers.each { category -> %>
                                <li>
                                    <a id="category-link-${category.uuid}" class="category-link" href="#" onclick="changeCategory('${category.uuid}')">
                                        ${ pihui.getBestShortName(category) }
                                    </a>
                                </li>
                            <% } %>
                        </ul>
                    </div>
                    <div class="col-12 col-sm-8 col-md-7">
                        <% labSet.setMembers.each { category -> %>
                            <div class="lab-selection-form" id="lab-selection-form-${category.uuid}">
                                <fieldset class="fieldset">
                                    <legend>
                                        ${ui.message("pihcore.labOrder.panels")}
                                    </legend>
                                    <div class="panel-box">
                                        <% category.setMembers.each { orderable -> %>
                                            <% if (orderable.isSet()) { %>
                                                <button id="panel-button-${orderable.uuid}" class="lab-tests-btn" type="button" onclick="togglePanel('${orderable.uuid}')">
                                                    ${ pihui.getBestShortName(orderable) }
                                                </button>
                                            <% } %>
                                        <% } %>
                                    </div>
                                </fieldset>
                                <fieldset class="fieldset">
                                    <legend>${ui.message("pihcore.labOrder.tests")}</legend>
                                    <div class="panel-box">
                                        <% category.setMembers.each { orderable -> %>
                                            <% if (!orderable.isSet()) { %>
                                                <button id="test-button-${orderable.uuid}" class="lab-tests-btn" type="button" onclick="toggleTest('${orderable.uuid}')">
                                                    ${ pihui.getBestShortName(orderable) }
                                                </button>
                                            <% } %>
                                        <% } %>
                                    </div>
                                </fieldset>
                            </div>
                        <% } %>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
    <div class="col-12 col-sm-6 col-md-4 draft-wrapper">
        <h5 class="h5-draft-header">
            ${ui.message('pihcore.unsavedDraftOrders')}
        </h5>
        <div class="table-container">
            <ul class="draft-list-container">

            </ul>
        </div>
    </div>
</div>
<div style="margin-top: 20px;">
    <button class="cancel" id="cancel-button">
        ${ui.message("pihcore.encounterList.return")}
    </button>
</div>
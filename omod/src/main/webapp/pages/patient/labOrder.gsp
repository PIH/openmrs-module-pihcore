<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "orderEntry.css")
    ui.includeCss("pihcore", "labOrder.css")
    ui.includeJavascript("uicommons", "moment.min.js")
%>
<script type="text/javascript">

    // Configuration
    const testNames = new Map();
    const reasonsByTest = new Map();
    const testsByPanel = new Map();
    const returnUrl = '${returnUrl ?: ui.pageLink("pihcore", "patient/labOrders", [patient: patient.id])}';

    // Order Data
    const orderedTests = [];
    const urgencies = new Map();
    const reasons = new Map();
    const orderedTestsFromPanel = [];
    const patient = '${patient.patient.uuid}';
    const orderer = '${sessionContext.currentProvider.uuid}';
    const encounterLocation = '${sessionContext.sessionLocation.uuid}'

    <% labSet.setMembers.each { category -> %>
        <% category.setMembers.each { orderable -> %>
            testNames.set('${ orderable.uuid }', '${ ui.encodeJavaScript(pihui.getBestShortName(orderable)) }');
            <% if (orderable.isSet()) { %>
                testsByPanel.set('${ orderable.uuid }', new Set());
                <% orderable.setMembers.each { test -> %>
                    testsByPanel.get('${ orderable.uuid }').add('${ test.uuid }');
                <% } %>
            <% } %>
            <% if (orderReasonsMap.get(orderable.uuid)) { %>
                reasonsByTest.set('${ orderable.uuid }', []);
                <% orderReasonsMap.get(orderable.uuid).each{ reasonConcept -> %>
                    reasonsByTest.get('${ orderable.uuid }').push({uuid: '${reasonConcept.uuid}', display: '${ ui.encodeJavaScript(pihui.getBestShortName(reasonConcept)) }'});
                <% } %>
            <% } %>
        <% } %>
    <% } %>

    function changeCategory(categoryUuid) {
        jq(".category-link").removeClass("active-category");
        jq("#category-link-" + categoryUuid).addClass("active-category");
        jq(".lab-selection-form").hide();
        jq("#lab-selection-form-" + categoryUuid).show();
    }

    function addOrderedTest(orderedUuid) {
        orderedTests.push(orderedUuid);
        const testName = testNames.get(orderedUuid);

        const draftItem = jq("#draft-order-template").clone();
        jq(draftItem)
            .attr("id", "draft-order-" + orderedUuid)
            .find(".draft-name").html(testName);
        jq("#draft-list-container").append(draftItem);
        jq(draftItem).find(".draft-discard-btn").click(function () {
            toggleTest(orderedUuid);
        });
        jq(draftItem).find(".draft-urgency-toggle-btn").click(function () {
            toggleUrgency(orderedUuid);
        });
        jq(draftItem).show();

        const orderedReasons = reasonsByTest.get(orderedUuid);
        if (orderedReasons && orderedReasons.length > 0) {
            const draftReasonItem = jq("#order-reason-template").clone();
            jq(draftReasonItem).attr("id", "order-reason-" + orderedUuid);
            const reasonSelector = jq(draftReasonItem).find(".order-reason-selector");
            orderedReasons.forEach(reason => {
                const selectItem = jq("<option>").val(reason.uuid).html(reason.display);
                jq(reasonSelector).append(selectItem);
            });
            jq(reasonSelector).change(function () {
                reasons.set(orderedUuid, jq(reasonSelector).val());
            });
            jq(reasonSelector).change();
            jq("#draft-list-container").append(draftReasonItem);
            jq(draftReasonItem).show();
        }
    }

    function removeOrderedTest(orderedUuid) {
        orderedTests.splice(orderedTests.indexOf(orderedUuid), 1);
        jq("#draft-order-" + orderedUuid).remove();
        jq("#order-reason-" + orderedUuid).remove();
    }

    function toggleUrgency(orderedUuid) {
        const current = urgencies.get(orderedUuid);
        const newUrgency = (current === 'STAT' ? 'ROUTINE' : 'STAT');
        urgencies.set(orderedUuid, newUrgency);
        const urgencyIcon = jq("#draft-order-" + orderedUuid).find(".draft-urgency-icon");
        jq(urgencyIcon).removeClass('i-gray').removeClass('i-red').addClass(newUrgency === 'STAT' ? 'i-red' : 'i-gray');
    }

    function toggleTest(testUuid) {
        const testButton = jq("#test-button-" + testUuid);
        const existingIndex = orderedTests.indexOf(testUuid);

        // Handle panel
        if (testsByPanel.has(testUuid)) {
            const addingPanel = (existingIndex < 0);
            if (addingPanel) {
                addOrderedTest(testUuid);
                jq("#panel-button-" + testUuid).addClass("active").blur();
            } else {
                removeOrderedTest(testUuid);
                jq("#panel-button-" + testUuid).removeClass("active").blur();
            }
            testsByPanel.get(testUuid).forEach((testInPanelUuid) => {
                if (addingPanel) {
                    if (orderedTests.indexOf(testInPanelUuid) === -1) {
                        orderedTestsFromPanel.push(testInPanelUuid);
                        jq("#test-button-" + testInPanelUuid).addClass("active").blur();
                    }
                }
                else {
                    if (orderedTestsFromPanel.indexOf(testInPanelUuid) > 0) {
                        orderedTestsFromPanel.splice(orderedTestsFromPanel.indexOf(testInPanelUuid), 1);
                        jq("#test-button-" + testInPanelUuid).removeClass("active").blur();
                    }
                }
            });
            updateDraftList();
        }
        // Handle individual test
        else {
            if (orderedTestsFromPanel.indexOf(testUuid) < 0) {
                if (existingIndex < 0) {
                    addOrderedTest(testUuid);
                    testButton.addClass("active");
                } else {
                    removeOrderedTest(testUuid);
                    testButton.removeClass("active");
                }
                updateDraftList();
            }
            testButton.blur();
        }
    }

    function updateDraftList() {
        jq("#num-draft-orders").html(orderedTests.length);
        const discardAllButton = jq("#draft-discard-all");
        const saveButton = jq("#draft-save-button");
        const orderDateSection = jq("#order-date-section");
        if (orderedTests.length > 1) {
            discardAllButton.val('${ui.encodeJavaScript(ui.message("pihcore.discardAll"))}');
        }
        else {
            discardAllButton.val('${ui.encodeJavaScript(ui.message("pihcore.discard"))}');
            if (orderedTests.length === 0) {
                discardAllButton.attr("disabled", "disabled");
                saveButton.attr("disabled", "disabled");
                orderDateSection.hide();
                jq("#order-date-picker-display").val("");
                jq("#order-date-picker-field").val("");
                jq("#order-date-default").show();
                jq("#order-date-custom").hide();
            }
            else {
                discardAllButton.removeAttr("disabled");
                saveButton.removeAttr("disabled");
                orderDateSection.show();
            }
        }
    }

    jq(document).ready(function () {

        jq(".lab-selection-form").hide();
        <% if (labSet && !labSet.setMembers.isEmpty()) { %>
            changeCategory('${labSet.setMembers.get(0).uuid}');
        <% } %>

        jq("#cancel-button").click(function () {
            document.location.href = returnUrl;
        })

        jq("#draft-discard-all").click(function () {
            const testsToRemove = [... orderedTests];
            testsToRemove.forEach(test => {
                toggleTest(test);
            })
            urgencies.clear();
            reasons.clear();
            updateDraftList();
        });

        jq("#order-date-toggle").click(function () {
            jq("#order-date-default").hide();
            jq("#order-date-custom").show();
        })

        jq("#draft-save-button").click(function () {
            jq.get(openmrsContextPath + "/ws/rest/v1/pihcore/labOrderConfig", function(labOrderConfig) {
                const orders = [];
                const orderDate = jq("#order-date-picker-field").val() || new Date();
                orderedTests.forEach(orderable => {
                    orders.push({
                        type: 'testorder',
                        patient: patient,
                        orderer: orderer,
                        concept: orderable,
                        urgency: urgencies.get(orderable) || 'ROUTINE',
                        orderReason: reasons.get(orderable) ?? null,
                        careSetting: labOrderConfig.careSetting.INPATIENT,
                        dateActivated: orderDate,
                        autoExpireDate: moment(orderDate).add(labOrderConfig.autoExpireDays || 30, 'days').format('YYYY-MM-DDTHH:mm:ss.SSS'),
                    });
                })
                const encounterPayload = {
                    patient: patient,
                    encounterType: labOrderConfig.encounterType,
                    encounterDatetime: orderDate,
                    location: encounterLocation,
                    orders,
                    encounterProviders: [ { encounterRole: labOrderConfig.encounterRole, provider: orderer } ],
                };

                jq.ajax({
                    url: openmrsContextPath + '/ws/rest/v1/encounter',
                    type: 'POST',
                    contentType: 'application/json; charset=utf-8',
                    data: JSON.stringify(encounterPayload),
                    dataType: 'json', // Expect JSON response
                    success: function(response) {
                        emr.successMessage('${ui.encodeJavaScript(ui.message("pihcore.orderSuccessMessage"))}');
                        document.location.href = returnUrl;
                    },
                    error: function(xhr, status, error) {
                        const message = xhr.responseJSON?.error?.message ?? error ?? xhr.responseText;
                        emr.errorMessage('${ui.encodeJavaScript(ui.message("pihcore.orderErrorMessage"))}: ' + message);
                    }
                });
            });
        });
    })
</script>

${ui.includeFragment("coreapps", "patientHeader", [patient: patient.patient])}

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {
            label: "${ ui.escapeJs(ui.format(patient.patient)) }",
            link: '${ui.pageLink("pihcore", "router/programDashboard", ["patientId": patient.id])}'
        },
        {
            label: "${ ui.encodeJavaScript(ui.message("pihcore.labOrders")) }",
            link: '${ui.pageLink("pihcore", "patient/labOrders", ["patient": patient.id])}'
        },
        {
            label: "${ ui.encodeJavaScript(ui.message("pihcore.addLabOrders")) }",
            link: '${ui.pageLink("pihcore", "patient/labOrder", ["patient": patient.id])}'
        }
    ];
</script>

<style>
    legend {
        text-align: left;
        padding: 0 10px;
        font-weight: 300;
        font-size: 16px;
        text-transform: uppercase;
        width: unset;
    }

    li.small-font {
        font-size: 0.8rem;
    }

    .tooltip {
        position: relative;
        opacity: unset;
        text-align: center;
    }
    .tooltip .tooltip-text {
        visibility: hidden;
        width: 300px;
        background-color: white;
        border: 1px solid grey;
        border-radius: 6px;
        text-align: left;
        position: absolute;
        bottom: 130%;
        left: 30%;
        z-index: 1;
        margin-left: -60px;
        opacity: 0;
        transition: opacity 0.3s;
    }
    .tooltip .tooltip-text p {
        padding: 5px 0 0 8px;
        font-size: 13px;
        font-weight: bold;
    }
    .tooltip .tooltip-text div {
        display: flex;
        flex-flow: row wrap;
        padding: 0px 10px 5px 10px;
    }
    .tooltip .tooltip-text div span {
        margin: 0 5px 7px 0;
        text-align: left;
        width: 31.5%;
    }
    .tooltip .tooltip-text::after {
        content: "";
        position: absolute;
        top: 100%;
        left: 10%;
        margin-left: -5px;
        border-width: 10px;
        border-style: solid;
        border-color: #555 transparent transparent transparent;
    }
    .tooltip:hover .tooltip-text {
        visibility: visible;
        opacity: 1;
    }

    #order-date-section {
        font-size: 80%;
        display: none;
    }
    #order-date-label {
        font-weight: bold;
    }
    #order-date-default {
        padding-left: 10px;
    }
    #order-date-toggle {
        padding-left: 20px;
    }
    #order-date-custom {
        padding-left: 20px;
        display: none;
    }
</style>

<div class="row">
    <div class="col-12 col-md-8">
        <div class="lab-order-entry">
            <h3>${ ui.message("pihcore.addLabOrders") }</h3>

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
                                                <button id="panel-button-${orderable.uuid}" class="lab-tests-btn tooltip" type="button" onclick="toggleTest('${orderable.uuid}')">
                                                    ${ pihui.getBestShortName(orderable) }
                                                    <span class="tooltip-text">
                                                        <p>${ui.message("pihcore.testsIncludedInThisPanel")}:</p>
                                                        <div>
                                                            <% orderable.setMembers.each { setMember -> %>
                                                                <span>${pihui.getBestShortName(setMember)}</span>
                                                            <% } %>
                                                        </div>
                                                    </span>
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
            (<span id="num-draft-orders">0</span>)
        </h5>
        <div class="table-container">
            <ul id="draft-list-container" class="draft-list-container"></ul>
        </div>
        <ul style="display: none;">
            <li class="draft-list small-font" id="draft-order-template" style="display: none;">
                <span class="order-status">NEW</span>
                <span class="draft-name"></span>
                <div class="action-btn-wrapper">
                    <a class="draft-urgency-toggle-btn action-btn" href="#">
                        <i class="draft-urgency-icon i-gray scale" title="${ui.message('pihcore.urgency')}">&#x25B2;</i>
                    </a>
                    <a class="action-btn right" href="#">
                        <i class="draft-discard-icon draft-discard-btn icon-remove scale" title="${ui.message('pihcore.discard')}"></i>
                    </a>
                </div>
            </li>
            <li id="order-reason-template" style="display: none;">
                <span>${ui.message("pihcore.orderReason")}</span>:
                <select class="order-reason-selector" name="orderReason"></select>
            </li>
        </ul>
        <div id="order-date-section">
            <span id="order-date-label">${ui.message("pihcore.orderDate")}:</span>
            <span id="order-date-default">
                ${ ui.format(new Date()) }
                <a href="#" id="order-date-toggle">${ui.message("pihcore.change")}</a>
            </span>
            <span id="order-date-custom">
                ${ui.includeFragment("uicommons", "field/datetimepicker", [
                        id: "order-date-picker",
                        label: "",
                        formFieldName: "order_date",
                        useTime: false,
                        left: true,
                        size: 20
                ])}
            </span>
        </div>
        <br/>
        <input type="button" id="draft-discard-all" value="${ui.message('pihcore.discard')}" disabled="disabled" class="cancel modified-btn"/>
        <input type="submit" id="draft-save-button" value="${ui.message('mirebalais.save')}" disabled="disabled" class="right confirm modified-btn"/>
        <br />
        <br />
    </div>
</div>
<div style="margin-top: 20px;">
    <button class="cancel" id="cancel-button">
        ${ui.message("pihcore.encounterList.return")}
    </button>
</div>
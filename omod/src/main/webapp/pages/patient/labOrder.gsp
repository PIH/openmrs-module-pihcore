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
    const careSetting = '${careSetting.uuid}';
    const encounterType = '${encounterType.uuid}';
    const encounterRole = '${encounterRole.uuid}';
    const autoExpireDays = ${autoExpireDays};
    const returnUrl = '${returnUrl ?: ui.pageLink("pihcore", "patient/labOrders", [patient: patient.id])}';

    // Order Data
    const orderedTests = [];
    const urgencies = new Map();
    const reasons = new Map();
    const orderedTestsFromPanel = [];
    const orderDate = new Date();
    const patient = '${patient.patient.uuid}';
    const orderer = '${sessionContext.currentProvider.uuid}';
    const encounterLocation = '${sessionContext.sessionLocation.uuid}'

    <% labSet.setMembers.each { category -> %>
        <% category.setMembers.each { orderable -> %>
            testNames.set('${ orderable.uuid }', '${ pihui.getBestShortName(orderable) }');
            <% if (orderable.isSet()) { %>
                testsByPanel.set('${ orderable.uuid }', new Set());
                <% orderable.setMembers.each { test -> %>
                    testsByPanel.get('${ orderable.uuid }').add('${ test.uuid }');
                <% } %>
            <% } %>
            <% if (orderReasonsMap.get(orderable.uuid)) { %>
                reasonsByTest.set('${ orderable.uuid }', []);
                <% orderReasonsMap.get(orderable.uuid).each{ reasonConcept -> %>
                    reasonsByTest.get('${ orderable.uuid }').push({uuid: '${reasonConcept.uuid}', display: '${ pihui.getBestShortName(reasonConcept) }'});
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

    function addOrderedTest(orderedUuid) {
        orderedTests.push(orderedUuid);
        const testName = testNames.get(orderedUuid);

        const draftItem = jQuery("#draft-order-template").clone();
        jQuery(draftItem)
            .attr("id", "draft-order-" + orderedUuid)
            .find(".draft-name").html(testName);
        jQuery("#draft-list-container").append(draftItem);
        jQuery(draftItem).find(".draft-discard-btn").click(function () {
            toggleTest(orderedUuid);
        });
        jQuery(draftItem).find(".draft-urgency-toggle-btn").click(function () {
            toggleUrgency(orderedUuid);
        });
        jQuery(draftItem).show();

        const orderedReasons = reasonsByTest.get(orderedUuid);
        if (orderedReasons && orderedReasons.length > 0) {
            const draftReasonItem = jQuery("#order-reason-template").clone();
            jQuery(draftReasonItem).attr("id", "order-reason-" + orderedUuid);
            const reasonSelector = jQuery(draftReasonItem).find(".order-reason-selector");
            orderedReasons.forEach(reason => {
                const selectItem = jQuery("<option>").val(reason.uuid).html(reason.display);
                jQuery(reasonSelector).append(selectItem);
            });
            jQuery(reasonSelector).change(function () {
                reasons.set(orderedUuid, jQuery(reasonSelector).val());
            });
            jQuery(reasonSelector).change();
            jQuery("#draft-list-container").append(draftReasonItem);
            jQuery(draftReasonItem).show();
        }
    }

    function removeOrderedTest(orderedUuid) {
        orderedTests.splice(orderedTests.indexOf(orderedUuid), 1);
        jQuery("#draft-order-" + orderedUuid).remove();
        jQuery("#order-reason-" + orderedUuid).remove();
    }

    function toggleUrgency(orderedUuid) {
        const current = urgencies.get(orderedUuid);
        const newUrgency = (current === 'STAT' ? 'ROUTINE' : 'STAT');
        urgencies.set(orderedUuid, newUrgency);
        const urgencyIcon = jQuery("#draft-order-" + orderedUuid).find(".draft-urgency-icon");
        jQuery(urgencyIcon).removeClass('i-gray').removeClass('i-red').addClass(newUrgency === 'STAT' ? 'i-red' : 'i-gray');
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
        jQuery("#num-draft-orders").html(orderedTests.length);
        const discardAllButton = jQuery("#draft-discard-all");
        const saveButton = jQuery("#draft-save-button");
        if (orderedTests.length > 1) {
            discardAllButton.val('${ui.message("pihcore.discardAll")}');
        }
        else {
            discardAllButton.val('${ui.message("pihcore.discard")}');
            if (orderedTests.length === 0) {
                discardAllButton.attr("disabled", "disabled");
                saveButton.attr("disabled", "disabled");
            }
            else {
                discardAllButton.removeAttr("disabled");
                saveButton.removeAttr("disabled");
            }
        }
    }

    jQuery(document).ready(function () {

        jQuery(".lab-selection-form").hide();
        <% if (labSet && !labSet.setMembers.isEmpty()) { %>
            changeCategory('${labSet.setMembers.get(0).uuid}');
        <% } %>

        jQuery("#cancel-button").click(function () {
            document.location.href = returnUrl;
        })

        jQuery("#draft-discard-all").click(function () {
            const testsToRemove = [... orderedTests];
            testsToRemove.forEach(test => {
                toggleTest(test);
            })
            urgencies.clear();
            reasons.clear();
            updateDraftList();
        });

        jQuery("#draft-save-button").click(function () {
            const orders = [];
            orderedTests.forEach(orderable => {
                orders.push({
                    type: 'testorder',
                    patient: patient,
                    orderer: orderer,
                    concept: orderable,
                    urgency: urgencies.get(orderable) || 'ROUTINE',
                    orderReason: reasons.get(orderable) ?? null,
                    careSetting: careSetting,
                    dateActivated: orderDate,
                    autoExpireDate: moment(orderDate).add(autoExpireDays, 'days').format('YYYY-MM-DDTHH:mm:ss.SSS'),
                });
            })
            const encounterPayload = {
                patient: patient,
                encounterType: encounterType,
                encounterDatetime: orderDate,
                location: encounterLocation,
                orders,
                encounterProviders: [ { encounterRole: encounterRole, provider: orderer } ],
            };

            jq.ajax({
                url: openmrsContextPath + '/ws/rest/v1/encounter',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(encounterPayload),
                dataType: 'json', // Expect JSON response
                success: function(response) {
                    emr.successMessage('${ui.message("pihcore.orderSuccessMessage")}');
                    document.location.href = returnUrl;
                },
                error: function(xhr, status, error) {
                    const message = xhr.responseJSON?.error?.message ?? error ?? xhr.responseText;
                    emr.errorMessage('${ui.message("pihcore.orderErrorMessage")}: ' + message);
                }
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
            label: "${ ui.message("pihcore.labOrders") }",
            link: '${ui.pageLink("pihcore", "patient/labOrders", ["patient": patient.id])}'
        },
        {
            label: "${ ui.message("pih.app.labs.ordering") }",
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
                                                <button id="panel-button-${orderable.uuid}" class="lab-tests-btn" type="button" onclick="toggleTest('${orderable.uuid}')">
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
        <br />
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
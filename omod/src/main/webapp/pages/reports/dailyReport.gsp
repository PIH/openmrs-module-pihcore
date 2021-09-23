<%
    def angularLocale = context.locale.toString().toLowerCase();

    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "i18n/angular-locale_" + angularLocale + ".js")
    ui.includeJavascript("pihcore", "reports/dailyReport.js")
    ui.includeJavascript("pihcore", "reports/ui-bootstrap-tpls-0.6.0.min.js")
    ui.includeCss("pihcore", "dailyReport.css")

    def CHECKINS_REPORT_DEFINITION_UUID = "f170699a-50af-11e3-ba00-27a0ac7f78d9"
    def hideCounts = ((hideCounts != null) && (reportDefinition.uuid == CHECKINS_REPORT_DEFINITION_UUID)) ? hideCounts : false
%>

<%= ui.includeFragment("appui", "messages", [ codes: [
        reportDefinition.name,
        reportDefinition.description,
        "mirebalaisreports.dailyRegistrations.overall",
        "mirebalaisreports.dailyClinicalEncounters.clinicalCheckIns",
        "mirebalaisreports.dailyClinicalEncounters.consultWithoutVitals",
        "mirebalaisreports.dailyCheckInEncounters.overall",
        "mirebalaisreports.dailyCheckInEncounters.dataQuality.multipleCheckins",
        "mirebalaisreports.dailyCheckInEncounters.CLINICAL_new",
        "mirebalaisreports.dailyCheckInEncounters.CLINICAL_return",
        context.locationService.allLocations.collect { "ui.i18n.Location.name." + it.uuid },
        context.encounterService.allEncounterTypes.collect { "ui.i18n.EncounterType.name." + it.uuid }
    ].flatten()
]) %>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("reportingui.reportsapp.home.title")) }", link: emr.pageLink("reportingui", "reportsapp/home") },
        { label: "${ ui.message(ui.format(reportDefinition)) }", link: "${ ui.escapeJs(ui.thisUrl()) }" }
    ];

    window.reportDefinition = {
        uuid: '${ reportDefinition.uuid}'
    };
</script>


<div ng-app="dailyReport" ng-controller="DailyReportController">

    <div id="date-header">
        <h1 id="current-date">
            ${ ui.message(reportDefinition.name) }
        </h1>
        <div class="angular-datepicker">
            <div class="form-horizontal">
                <input type="text" class="datepicker-input" datepicker-popup="dd-MMMM-yyyy" ng-model="day" is-open="isDatePickerOpen" max="maxDay" date-disabled="disabled(date, mode)" ng-required="true" show-weeks="false" />
                <button class="btn" ng-click="openDatePicker()"><i class="icon-calendar"></i></button>
            </div>
        </div>
        <p>
            ${ ui.message(reportDefinition.description) }
        </p>
    </div>

    <div ng-show="isLoading()">
        <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
    </div>

    <div ng-show="hasResults()">
        <div ng-repeat="dataset in currentData().dataSets">
            <!-- do not include byLocation table for Thomonde -->
            <div ng-include=" 'include/' + dataset.definition.name + '.page' " class="dataset" ng-show="!((dataset.definition.name == 'byLocation') && '${hideCounts}' == 'true')"></div>
        </div>
    </div>

    <div id="view-cohort" ng-show="viewingCohort && '${hideCounts}' !== 'true'">

        <img ng-show="viewingCohort.loading" src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>

        <div ng-show="viewingCohort.members">
            <p>
                {{ viewingCohort.column.label | translate }} -
                <span ng-show="viewingCohort.row['rowLabel']">
                    {{ viewingCohort.row['rowLabel'] | translate }} -
                </span>
                {{ viewingCohort.day | date }}
            </p>

            <table class="patient-list-table">
                <thead>
                    <th></th>
                    <th>${ ui.message("ui.i18n.PatientIdentifierType.name.a541af1e-105c-40bf-b345-ba1fd6a59b85") }</th>
                    <th>${ ui.message("ui.i18n.PatientIdentifierType.name.e66645eb-03a8-4991-b4ce-e87318e37566") }</th>
                </thead>
                <tbody ng-show="viewingCohort.members">
                    <tr ng-repeat="member in viewingCohort.members">
                        <td>
                            <% if (sessionContext.currentUser.hasPrivilege(privilegePatientDashboard)) { %>
                                <!-- only add link to patient dashboard if user has appropriate privilege -->
                                <a target="_blank" href="${ "/" + contextPath + dashboardUrlWithoutQueryParams }?patientId={{ member.patientId }}">
                            <% } %>

                                {{ member.familyName }}, {{ member.givenName }}

                            <% if (sessionContext.currentUser.hasPrivilege(privilegePatientDashboard)) { %>
                            <!-- only add link to patient dashboard if user has appropriate privilege -->
                                </a>
                            <% } %>
                        </td>
                        <td>
                            {{ member.zlEmrId }}
                        </td>
                        <td>
                            {{ member.dossierNumber }}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

</div>
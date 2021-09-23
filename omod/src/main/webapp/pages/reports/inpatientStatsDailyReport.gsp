<%
    def angularLocale = context.locale.toString().toLowerCase();

    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "i18n/angular-locale_" + angularLocale + ".js")
    ui.includeJavascript("pihcore", "reports/inpatientStatsDailyReport.js")
    ui.includeJavascript("pihcore", "reports/ui-bootstrap-tpls-0.6.0.min.js")
    ui.includeCss("pihcore", "inpatientStatsDailyReport.css")
%>

${ ui.includeFragment("appui", "messages", [ codes: [
        "ui.i18n.Location.name.272bd989-a8ee-4a16-b5aa-55bad4e84f5c",
        "ui.i18n.Location.name.dcfefcb7-163b-47e5-84ae-f715cf3e0e92",
        "ui.i18n.Location.name.e5db0599-89e8-44fa-bfa2-07e47d63546f",
        "ui.i18n.Location.name.2c93919d-7fc6-406d-a057-c0b640104790",
        "ui.i18n.Location.name.62a9500e-a1a5-4235-844f-3a8cc0765d53",
        "ui.i18n.Location.name.c9ab4c5c-0a8a-4375-b986-f23c163b2f69",
        "ui.i18n.Location.name.950852f3-8a96-4d82-a5f8-a68a92043164",
        "ui.i18n.Location.name.7d6cc39d-a600-496f-a320-fd4985f07f0b",
        "ui.i18n.Location.name.29437276-aeae-4ea8-8219-720886cdc87f",
        "mirebalaisreports.inpatientStatsDailyReport.censusAtStart",
        "mirebalaisreports.inpatientStatsDailyReport.admissions",
        "mirebalaisreports.inpatientStatsDailyReport.transfersIn",
        "mirebalaisreports.inpatientStatsDailyReport.transfersOut",
        "mirebalaisreports.inpatientStatsDailyReport.discharged",
        "mirebalaisreports.inpatientStatsDailyReport.deathsWithin48",
        "mirebalaisreports.inpatientStatsDailyReport.deathsAfter48",
        "mirebalaisreports.inpatientStatsDailyReport.transfersOutOfHUM",
        "mirebalaisreports.inpatientStatsDailyReport.leftWithoutCompletingTx",
        "mirebalaisreports.inpatientStatsDailyReport.leftWithoutSeeingClinician",
        "mirebalaisreports.inpatientStatsDailyReport.censusAtEnd",
        "mirebalaisreports.inpatientStatsDailyReport.edcheckin",
        "mirebalaisreports.inpatientStatsDailyReport.orvolume",
        "mirebalaisreports.inpatientStatsDailyReport.availableBeds",
        "mirebalaisreports.inpatientStatsDailyReport.percentageOfOccupancy",
        "mirebalaisreports.inpatientStatsDailyReport.possiblereadmission"
    ]
])}

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("reportingui.reportsapp.home.title")) }", link: emr.pageLink("reportingui", "reportsapp/home") },
        { label: "${ ui.message("mirebalaisreports.inpatientStatsDailyReport.name") }", link: "${ ui.pageLink("pihcore", "reports/inpatientStatsDailyReport") }" }
    ];
</script>

<div ng-app="inpatientStatsDailyReport" ng-controller="InpatientStatsDailyReportController" ng-init="evaluate()">

    <div id="date-header">
        <h1 id="current-date">
            ${ ui.message("mirebalaisreports.inpatientStatsDailyReport.name") }
        </h1>
        <div class="angular-datepicker">
            <div class="form-horizontal">
                <input type="text" class="datepicker-input" datepicker-popup="dd-MMMM-yyyy" ng-model="day" is-open="isDatePickerOpen" max="maxDay" date-disabled="disabled(date, mode)" ng-required="true" show-weeks="false" />
                <button class="btn" ng-click="openDatePicker()"><i class="icon-calendar"></i></button>
            </div>
        </div>
    <div ng-hide="isLoading(day) || hasResults(day)">
        <button ng-click="evaluate()">
            ${ ui.message("mirebalaisreports.evaluate") }
        </button>
    </div>

    <div ng-show="isLoading(day)">
        <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
    </div>

    <div ng-show="hasResults(day)">
        <table class="inpatients-table">
            <thead>
                <tr>
                    <th></th>
                    <th ng-repeat="location in locations">{{ location | translate:"ui.i18n.Location.name." }}</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="locationIndicator in locationIndicators" ng-class="locationIndicator.class">
                    <th>{{ locationIndicator.name | translate:"mirebalaisreports.inpatientStatsDailyReport." }}</th>
                    <td ng-repeat="location in locations">
                        <a ng-click="viewCohort(day, locationIndicator, location)" ng-show="locationIndicator.evaluator(location, day, locationIndicator) > 0 && locationIndicator.link">
                            {{ locationIndicator.evaluator(location, day, locationIndicator) }}
                        </a>
                        <span ng-show="locationIndicator.evaluator(location, day, locationIndicator) <= 0 || !locationIndicator.link">
                            {{ locationIndicator.evaluator(location, day, locationIndicator) }}
                        </span>
                    </td>
                </tr>
            </tbody>
        </table>

        <table class="inpatients-table">
            <tbody>
            <tr ng-repeat="indicator in indicators">
                <th>{{ indicator.name | translate:"mirebalaisreports.inpatientStatsDailyReport." }}</th>
                <td>
                    <a ng-click="viewCohort(day, indicator)" ng-show="dataFor(day).cohorts[indicator.name].size > 0">
                        {{ dataFor(day).cohorts[indicator.name].size }}
                    </a>   
                    <span ng-click="viewCohort(day, indicator)" ng-show="dataFor(day).cohorts[indicator.name].size <= 0">
                        {{ dataFor(day).cohorts[indicator.name].size }}
                    </span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div id="view-cohort" ng-show="viewingCohort">

        <img ng-show="viewingCohort.loading" src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>

        <div ng-show="viewingCohort.members">
            <h3>
                <span ng-show="viewingCohort.location">{{ viewingCohort.location | translate:"ui.i18n.Location.name." }}</span>
                {{ viewingCohort.indicator.name | translate:"mirebalaisreports.inpatientStatsDailyReport." }}
            </h3>

            <table class="inpatients-list-table">
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

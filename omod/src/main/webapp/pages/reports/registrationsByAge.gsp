<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "i18n/angular-locale_" + context.locale.toString().toLowerCase() + ".js")
    ui.includeJavascript("pihcore", "reports/registrationsByAge.js")
    ui.includeJavascript("pihcore", "reports/ui-bootstrap-tpls-0.6.0.min.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("reportingui.reportsapp.home.title")) }", link: emr.pageLink("reportingui", "reportsapp/home") },
        { label: "${ ui.message("mirebalaisreports.registrationoverview.title") }", link: "${ ui.thisUrl() }" }
    ];
</script>

<style>
    #results-section {
        padding-top:30px;
    }
    #loading-section {
        width:100%;
        padding-top:50px;
        text-align: center;
    }
    #detail-table {
        width:100%;
        padding-top:50px;
    }
</style>

<h1>
    ${ ui.message("mirebalaisreports.registrationoverview.title") }
</h1>

<div id="registrations-by-age-app" ng-controller="RegistrationsByAgeController" ng-init="runReport()">

    <div id="date-header">
        <div class="angular-datepicker">
            <div class="form-horizontal">
                <%= ui.message"mirebalaisreports.fromDate" %>
                <input id="start-date-input" type="text" class="datepicker-input" ng-click="openStartDate()" datepicker-popup="dd-MMMM-yyyy" ng-model="startDate" is-open="startDateOpened" max="maxDay" date-disabled="disabled(date, mode)" ng-required="false" show-weeks="false" />
                <button class="btn" ng-click="openStartDate()"><i class="icon-calendar"></i></button>
            </div>
        </div>
        <div class="angular-datepicker">
            <div class="form-horizontal">
                <%= ui.message"mirebalaisreports.toDate" %>
                <input id="end-date-input" type="text" class="datepicker-input" ng-click="openEndDate()" datepicker-popup="dd-MMMM-yyyy" ng-model="endDate" is-open="endDateOpened" max="maxDay" date-disabled="disabled(date, mode)" ng-required="true" show-weeks="false" />
                <button class="btn" ng-click="openEndDate()"><i class="icon-calendar"></i></button>
            </div>
        </div>
        <div class="angular-datepicker">
            <div class="form-horizontal">
                <input type="button" ng-click="runReport()" value="<%= ui.message"mirebalaisreports.general.runReport" %>"/>
            </div>
        </div>
    </div>

    <div id="results-section">

        <div id="loading-section" ng-show="isLoading()">
            <img src="${ ui.resourceLink("uicommons", "images/spinner.gif") }"/>
        </div>

        <div id="indicator-table" ng-show="data">
            <table width="100%" border="1" cellspacing="0" cellpadding="2">
                <thead>
                    <tr>
                        <th></th>
                        <th style="white-space: nowrap;" ng-repeat="ageCategory in data.ageCategories">
                            {{ ageCategory }}
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td style="width:25%"><%= ui.message("mirebalaisreports.registrationsForAge") %></td>
                        <td ng-repeat="ageCategory in data.ageCategories" ng-click="selectCategory(ageCategory)">
                            <a>
                                {{ numRows(ageCategory) }}
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div id="detail-table" ng-show="selectedCategory">

            <h4><%= ui.message("mirebalaisreports.registrationsForAge") %> {{ selectedCategory }}</h4>

            <table class="patient-list-table" style="width:100%;">
                <thead>
                    <th ng-repeat="columnLabel in data.columnLabels">
                        {{ columnLabel }}
                    </th>
                </thead>
                <tbody>
                    <tr ng-repeat="row in currentRows">
                        <td ng-repeat="column in data.columns">
                            {{ row[column] }}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    </div>
</div>

<script type="text/javascript">
    angular.bootstrap('#registrations-by-age-app', [ 'registrationsByAge' ]);
</script>
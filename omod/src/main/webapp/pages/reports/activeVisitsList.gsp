<%
    //the table pagination widget needs a newer bootstap version, ui-bootstrap-tpls-2.3.1
    //  the version in openmrs is older (ui-bootstrap-tpls-0.6.0)
    ui.decorateWith("appui", "standardEmrPage", [ includeBootstrap: false ])

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.min.js")

    ui.includeJavascript("uicommons", "angular-translate.min.js")
    ui.includeJavascript("uicommons", "angular-translate-loader-url.min.js")
    ui.includeJavascript("uicommons", "services/session.js")
    ui.includeJavascript("uicommons", "services/appFrameworkService.js")
    ui.includeJavascript("uicommons", "model/user-model.js")
    ui.includeJavascript("uicommons", "model/encounter-model.js")
    ui.includeJavascript("uicommons", "model/visit-model.js")
    ui.includeJavascript("uicommons", "filters/display.js")
    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("pihcore", "services/configService.js")


    ui.includeCss("pihcore", "inpatient.css")
    ui.includeCss("pihcore", "activeVisitsList.css")
    ui.includeJavascript("pihcore", "visit/filters.js")
    ui.includeJavascript("pihcore", "reports/activeVisitsListController.js")

    // libs for the bootstrap date picker and other libs
    ui.includeJavascript("pihcore", "reports/ui-bootstrap-tpls-2.3.1.js")

    def lastLocationId = lastLocation;
    def allPatientsIds = []
    activeVisitsCohort.each {
        allPatientsIds.push(it);
    }
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.activeVisits.label")}"}
    ];

    jq(document).ready(function() {
        var locationId = '${lastLocationId}';
        if (parseInt(locationId) > 0) {
            jq("#visits-filterByLocation select").val(locationId);
        }

        jq("#visits-filterByLocation select").change(function(event){
            var selectedLocationId = this.value;
            var url = '/${ contextPath }/pihcore/reports/activeVisitsList.page?app=pih.app.activeVisits';
            if (parseInt(selectedLocationId) > 0) {
                url = url +'&location=' + selectedLocationId;
            }
            window.location= url;
        });
    });
    
</script>

<div class="inpatient-filter">
    ${ ui.includeFragment("emr", "field/location", [
            "id": "visits-filterByLocation",
            "formFieldName": "filterByLocationId",
            "label": "Filter by Last Seen",
            "withTag": "Login Location"
    ] ) }
</div>
<br/>
<br>
<br>
<div class="container" id="activeVisitsList-app" ng-controller="ActiveVisitsListController">
    <div class="row" id="active-visits-spinner" style="text-align:center; font-weight:bold; padding-top:50px;" ng-show="showResultsSpinner">
        <img src="${ui.resourceLink("uicommons", "images/spinner.gif")}">
    </div>
    <div class="top-buffer">
        <table id="activeVisitsTable" class="table display" cellspacing="0" width="100%">
            <thead>
            <tr>
                <th>${ui.message("coreapps.patient.identifier")}</th>
                <th>${ui.message("coreapps.person.name")}</th>
                <th>${ui.message("coreapps.activeVisits.checkIn")}</th>
                <th>${ui.message("coreapps.activeVisits.lastSeen")}</th>
            </tr>
            </thead>
            <tbody>
                <tr ng-repeat="visit in activeVisitsData" >
                    <td>{{ visit.identifier }}</td>
                    <td><a href="{{ getPatientUrl(visit.patientId) }}">{{ visit.familyName }}, {{ visit.givenName }}</a></td>
                    <td><span ng-if="visit.firstCheckinLocation">{{ visit.firstCheckinLocation }}<br/> @ {{ visit.checkinDateTime | date:'medium' }}</span></td>
                    <td><span ng-if="visit.lastEncounterLocation">{{ visit.lastEncounterLocation }}<br/> @ {{ visit.lastEncounterDateTime | date:'medium' }}</span></td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="row">
        <div class="col-sm-6">  <br/>
            <label for="paging">${ui.message("labtrackingapp.listpage.showing")} {{filter.paging.currentEntryStart}} ${ui.message("labtrackingapp.listpage.to")} {{filter.paging.currentEntryEnd}} ${ui.message("labtrackingapp.listpage.of")} {{filter.paging.totalItems}} ${ui.message("labtrackingapp.listpage.entries")}</label>
        </div>
        <div class="col-sm-6 text-right">
            <ul id="paging" uib-pagination total-items="filter.paging.totalItems" ng-model="filter.paging.currentPage" max-size="filter.paging.maxSize" ng-change="pageChanged()"
                class="pagination-sm" boundary-link-numbers="true"></ul>
        </div>
    </div>
</div>


<script type="text/javascript">
    angular.module('activeVisitsListApp')
            .value("allPatientsIds", '${ allPatientsIds.join(",") }')
            .value('patientPageUrl', '${ dashboardUrl }')
            .value('locale', '${ locale }');
    angular.bootstrap("#activeVisitsList-app", [ "activeVisitsListApp" ]);
    jq(function () {
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>






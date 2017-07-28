<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("mirebalais", "inpatient.css")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ng-grid-2.0.7.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
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

    ui.includeCss("uicommons", "angular-ui/ng-grid.min.css")

    ui.includeCss("pihcore", "activeVisitsList.css")
    ui.includeJavascript("pihcore", "visit/filters.js")
    ui.includeJavascript("pihcore", "reports/activeVisitsListController.js")

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
            jq("#activeVisitsGrid").hide();
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
    <div class="gridStyle" id="active-visits-spinner" style="text-align:center; font-weight:bold; padding-top:50px;" ng-show="showResultsSpinner">
        <img src="${ui.resourceLink("uicommons", "images/spinner.gif")}">
    </div>

    <table class="gridStyle" ng-grid="activeVisitsGrid" id="activeVisitsGrid" ng-hide="showResultsSpinner"></table>

    <div>
        {{ pagingInformation }}
    </div>

</div>


<script type="text/javascript">
    angular.module('activeVisitsListApp')
            .value("allPatientsIds", '${ allPatientsIds.join(",") }')
            .value('patientPageUrl', '${ patientPageUrl }')
            .value('locale', '${ locale }');
    angular.bootstrap("#activeVisitsList-app", [ "activeVisitsListApp" ]);
    jq(function () {
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>






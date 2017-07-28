angular.module('activeVisitsListApp', ['uicommons.filters', 'ngDialog', "ui.bootstrap", 'ngGrid',
    'configService', 'pascalprecht.translate'])

    .config(function ($translateProvider) {
        $translateProvider
            .useUrlLoader('/' +  OPENMRS_CONTEXT_PATH + '/module/uicommons/messages/messages.json',  {
                queryParameter : 'localeKey'
            })
            .fallbackLanguage('en')
            .useSanitizeValueStrategy('escape');
    })
    .service('ActiveVisitsListService', ['$q', '$http',
        function($q, $http) {
        }])
    .controller('ActiveVisitsListController', ['$q', '$http', '$scope', 'ActiveVisitsListService', 'ngDialog', 'allPatientsIds', 'dashboardUrl', '$filter', '$translate', 'locale',
        function($q, $http, $scope, ExportPatientService, ngDialog, allPatientsIds, dashboardUrl, $filter, $translate, locale) {

            $scope.allPatients = allPatientsIds.split(",");
            $scope.dashboardUrl = "/" + OPENMRS_CONTEXT_PATH + dashboardUrl;
            $scope.activeVisitsData = [];
            $scope.pagingInformation = '';
            $scope.showResultsSpinner = true;
            $scope.totalActiveVisitsItems = $scope.allPatients.length;

            $translate.use(locale);

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
            };

            $scope.pagingOptions = {
                pageSize: 15,
                currentPage: 1
            };

            $scope.getPatientUrl = function(patientId) {
                var patientUrl = $scope.dashboardUrl;
                return patientUrl.replace("{{patientId}}", patientId);
            };

            $scope.getFormatedDate = function(date) {
                var formatedDate = new Date($filter('serverDate')(date));
                return formatedDate;
            };

            $scope.getIdsForPage = function(pageSize, page){
                if (pageSize > 0 && page > 0) {
                    var pos = (page -1) * pageSize;
                    if (pos < ($scope.allPatients.length -1) ) {
                        return $scope.allPatients.slice( pos, pos + pageSize);
                    }
                }
                return null;
            };

            $scope.updatePagingInformation = function() {
                var pg = $scope.pagingOptions.currentPage;
                var sz = $scope.pagingOptions.pageSize;
                var tot = $scope.totalActiveVisitsItems;
                var lastOnPage = pg * sz;
                var firstOnPage = lastOnPage - sz + 1;
                if (lastOnPage > tot) {
                    lastOnPage = tot;
                }
                $scope.pagingInformation = firstOnPage + " to " + lastOnPage + " of " + tot;
            };

            $scope.setPagingData = function(data, totalItems, page, pageSize){
                $scope.showResultsSpinner = false;
                $scope.activeVisitsData = data;
                $scope.totalActiveVisitsItems = totalItems;
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
                $scope.updatePagingInformation();
            };

            $scope.getPagedDataAsync = function (pageSize, page, searchText) {
                $scope.showResultsSpinner = true;
                var ids = $scope.getIdsForPage(pageSize, page);
                var getAllUrl = "/" + OPENMRS_CONTEXT_PATH + "/module/pihcore/reports/ajax/activeVisitsList.form";

                $http({
                    headers : {'Content-Type' : 'application/json; charset=UTF-8'},
                    method: 'GET',
                    url: getAllUrl,
                    params: {
                        patientIds: ids
                    }
                }).success(function (fullData) {
                    $scope.setPagingData(fullData, $scope.totalActiveVisitsItems, page, pageSize);
                });
            };

            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);


            $scope.$watch('pagingOptions', function (newVal, oldVal) {
                if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
                    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
                }
            }, true);
            
            $scope.$watch('filterOptions', function (newVal, oldVal) {
                if (newVal !== oldVal) {
                    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
                }
            }, true);
            
            
            $scope.activeVisitsGrid = {
                data: 'activeVisitsData',
                enablePaging: true,
                showFooter: true,
                enableRowSelection: false,
                totalServerItems: 'totalActiveVisitsItems',
                pagingOptions: $scope.pagingOptions,
                filterOptions: $scope.filterOptions,
                columnDefs: [
                    {
                        field: 'zlEmrId',
                        width: '20%',
                        cellTemplate: "<div>{{ row.getProperty(\'zlEmrId\') }}</div>",
                        headerCellTemplate: "<div>{{ \'coreapps.patient.identifier\' | translate }}</div>"
                    },
                    {
                        field: 'familyName',
                        cellTemplate:  '<div> <a href="{{ getPatientUrl(row.getProperty(\'patientId\')) }}">{{ row.getProperty(\'familyName\') }}, {{ row.getProperty(\'givenName\') }} </a><div>',
                        headerCellTemplate: "<div>{{ \'coreapps.person.name\' | translate }}</div>"
                    },
                    {
                        field: 'checkinDateTime',
                        cellTemplate:  '<div> {{ row.getProperty(\'firstCheckinLocation\') }} @ {{ row.getProperty(\'checkinDateTime\') | serverDate:DatetimeFormats.date }} <div>',
                        headerCellTemplate: "<div>{{ \'coreapps.activeVisits.checkIn\' | translate }}</div>"
                    },
                    {
                        field: 'lastEncounterLocation',
                        headerCellTemplate: "<div>{{ \'coreapps.activeVisits.lastSeen\' | translate }}</div>"
                    }
                ]

            };

        }]);
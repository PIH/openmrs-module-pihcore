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
    .controller('ActiveVisitsListController', ['$q', '$http', '$scope', 'ActiveVisitsListService', 'ngDialog', 'allPatientsIds', 'patientPageUrl', '$filter', '$translate', 'locale','$timeout',
        function($q, $http, $scope, ExportPatientService, ngDialog, allPatientsIds, patientPageUrl, $filter, $translate, locale, $timeout) {

            $scope.allPatients = allPatientsIds.split(",");
            $scope.patientPageUrl = "/" + OPENMRS_CONTEXT_PATH + patientPageUrl;
            $scope.activeVisitsData = [];
            $scope.pagingInformation = '';
            $scope.showResultsSpinner = true;
            $scope.totalActiveVisitsItems = $scope.allPatients.length;

            $scope.timeoutPromise;
            $scope.delayInMs = 1000;

            $translate.use(locale);

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
            };

            $scope.pagingOptions = {
                pageSize: '15',
                pageSizes: [15, 30, 100],
                currentPage: 1
            };

            $scope.getPatientUrl = function(patientId, visitUuid) {
                var patientUrl = $scope.patientPageUrl;
                return patientUrl.replace("{{patientId}}", patientId);
            };

            $scope.getFormatedDate = function(date) {
                var formatedDate = new Date($filter('serverDate')(date));
                return formatedDate;
            };

            $scope.getIdsForPage = function(pageSize, page){
                if (pageSize > 0 && page > 0) {
                    var p = parseInt(page);
                    var ps = parseInt(pageSize);
                    var pos = (p -1) * ps;
                    if (pos < ($scope.allPatients.length -1) ) {
                        return $scope.allPatients.slice( pos, pos + ps);
                    }
                }
                return $scope.allPatients;
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
                window.clearTimeout($scope.timeoutPromise);  //does nothing, if timeout already done
                if (newVal !== oldVal && (newVal.currentPage > 0)) {
                    $scope.timeoutPromise = window.setTimeout( function() {
                            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
                        }
                        , $scope.delayInMs);
                }
            }, true);
            

            $scope.changedGridOptions = function() {
                console.log("grid options changed");
            };

            $scope.activeVisitsGrid = {
                data: 'activeVisitsData',
                enablePaging: true,
                showFooter: true,
                enableRowSelection: false,
                totalServerItems: 'totalActiveVisitsItems',
                pagingOptions: $scope.pagingOptions,
                filterOptions: $scope.filterOptions,
                headerRowHeight: 60,
                columnDefs: [
                    {
                        field: 'zlEmrId',
                        width: '20%',
                        cellTemplate: "<div>{{ row.getProperty(\'zlEmrId\') }}</div>",
                        headerCellTemplate: "<div>{{ \'coreapps.patient.identifier\' | translate }}</div>"
                    },
                    {
                        field: 'familyName',
                        cellTemplate:  '<div> <a href="{{ getPatientUrl(row.getProperty(\'patientId\'), row.getProperty(\'visitUuid\')) }}">{{ row.getProperty(\'familyName\') }}, {{ row.getProperty(\'givenName\') }} </a><div>',
                        headerCellTemplate: "<div>{{ \'coreapps.person.name\' | translate }}</div>"
                    },
                    {
                        field: 'checkinDateTime',
                        cellTemplate:  '<div ng-if="row.getProperty(\'checkinDateTime\')"><small> {{ row.getProperty(\'firstCheckinLocation\') }} @ {{ row.getProperty(\'checkinDateTime\') | serverDate:DatetimeFormats.date }} </small><div>',
                        headerCellTemplate: "<div>{{ \'coreapps.activeVisits.checkIn\' | translate }}</div>"
                    },
                    {
                        field: 'lastEncounterLocation',
                        cellTemplate:  '<div ng-if="row.getProperty(\'lastEncounterLocation\')"> {{ row.getProperty(\'lastEncounterType\')  | translate }} <br/> <small>{{ row.getProperty(\'lastEncounterLocation\') }} @ {{ row.getProperty(\'lastEncounterDateTime\') | serverDate:DatetimeFormats.date }} </small><div>',
                        headerCellTemplate: "<div>{{ \'coreapps.activeVisits.lastSeen\' | translate }}</div>"
                    }
                ]

            };

        }]);
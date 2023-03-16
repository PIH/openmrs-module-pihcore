angular.module('activeVisitsListApp', ['uicommons.filters', 'ngDialog', "ui.bootstrap",
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
            $scope.showResultsSpinner = true;
            if ( $scope.allPatients.length === 1 && ($scope.allPatients[0].length < 1) ) {
                $scope.totalActiveVisitsItems = 0;
            } else {
                $scope.totalActiveVisitsItems = $scope.allPatients.length;
            }

            $scope.filter = {
              paging: {
                totalItems: 0,
                currentPage: 1,
                maxSize: 10,
                currentEntryStart: 0,
                currentEntryEnd: 0
              }
            };

            $scope.timeoutPromise;
            $scope.delayInMs = 1000;

            $translate.use(locale);

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
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
                    if (pos <= ($scope.allPatients.length -1) ) {
                        return $scope.allPatients.slice( pos, pos + ps);
                    }
                }
                return $scope.allPatients;
            };


            $scope.setPagingData = function(data, pageNo, pageSize){
                $scope.showResultsSpinner = false;
                $scope.activeVisitsData = $filter('orderBy')(data, '-lastEncounterDateTime');
                let totalItems =  $scope.totalActiveVisitsItems;
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
                if (totalItems == 0) {
                    $scope.filter.paging.totalItems = 0;
                    $scope.filter.paging.currentPage = 0;
                    $scope.filter.paging.currentEntryStart = 0;
                    $scope.filter.paging.currentEntryEnd = 0;
                  }
                  else {
                    var sz = $scope.filter.paging.maxSize;
                    $scope.filter.paging.totalItems = totalItems;
                    $scope.filter.paging.currentPage = pageNo;
                    $scope.filter.paging.currentEntryStart = pageNo * sz - sz + 1;
                    $scope.filter.paging.currentEntryEnd = totalItems < pageNo * sz ? totalItems : pageNo * sz;

                  }
            };

            $scope.pageChanged = function () {
                return $scope.getPagedDataAsync($scope.filter.paging.maxSize, $scope.filter.paging.currentPage);
              };

            $scope.getPagedDataAsync = function (pageSize, page, searchText) {
                $scope.showResultsSpinner = true;
                let pageNumber = $scope.filter.paging.currentPage;
                if (pageNumber == 0 ) {
                    pageNumber = 1
                }

                var ids = $scope.getIdsForPage(pageSize, pageNumber);
                var getAllUrl = "/" + OPENMRS_CONTEXT_PATH + "/module/pihcore/reports/ajax/activeVisitsList.form";

                $http({
                    headers : {'Content-Type' : 'application/json; charset=UTF-8'},
                    method: 'GET',
                    url: getAllUrl,
                    params: {
                        patientIds: ids
                    }
                }).success(function (fullData) {
                    $scope.setPagingData(fullData, pageNumber, $scope.filter.paging.maxSize);
                });
            };

            $scope.getPagedDataAsync($scope.filter.paging.maxSize, $scope.filter.paging.currentPage);


        }]);

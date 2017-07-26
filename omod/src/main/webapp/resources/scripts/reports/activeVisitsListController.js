angular.module('activeVisitsListApp', ['ngDialog', "ui.bootstrap", 'ngGrid'])
    .service('ActiveVisitsListService', ['$q', '$http',
        function($q, $http) {
        }])
    .controller('ActiveVisitsListController', ['$q', '$http', '$scope', 'ActiveVisitsListService', 'ngDialog', 'allPatientsIds',
        function($q, $http, $scope, ExportPatientService, ngDialog, allPatientsIds) {

            $scope.allPatients = allPatientsIds.split(",");
            $scope.activeVisitsData = [];
            $scope.pagingInformation = '';
            $scope.showResultsSpinner = true;
            $scope.totalActiveVisitsItems = $scope.allPatients.length;

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
            };

            $scope.pagingOptions = {
                pageSize: 15,
                currentPage: 1
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
                        field: 'patientId',
                        displayName: 'Patient ID'
                    },
                    {
                        field: 'familyName',
                        displayName: 'Name'
                    },
                    {
                        field: 'checkinDateTime',
                        displayName: 'Check-In'
                    },
                    {
                        field: 'lastEncounterLocation',
                        displayName: 'Last Seen'
                    }
                ]

            };

        }]);
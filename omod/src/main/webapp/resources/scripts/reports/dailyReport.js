var app = angular.module('dailyReport', ['ui.bootstrap']).config(function($templateRequestProvider){
    $templateRequestProvider.httpOptions({
        headers: { Accept: 'text/html' }
    });
}).

    filter('translate', function() {
        return function(input, prefix) {
            if (input && input.uuid) {
                input = input.uuid;
            }
            var code = prefix ? prefix + input : input;
            return emr.message(code, input);
        }
    }).

    controller('DailyReportController', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

        function dataFor(date) {
            return $scope.data[date.getTime()];
        }

        $scope.data = { };

        $scope.viewingCohort = null;

        $scope.maxDay = moment().startOf('day').toDate();

        $scope.day = moment().startOf('day').toDate(); // default to today

        $scope.$watch('day', function() {
            $scope.viewingCohort = null;
        });

        $scope.currentData = function() {
            var day = $scope.day;
            if (!dataFor(day)) {
                evaluate(day);
            }
            return dataFor(day);
        }

        $scope.openDatePicker = function() {
            $timeout(function() {
                $scope.isDatePickerOpen = true;
            });
        };

        $scope.isLoading = function() {
            return dataFor($scope.day) && dataFor($scope.day).loading;
        };

        $scope.hasResults = function() {
            return dataFor($scope.day) && dataFor($scope.day).dataSets;
        };

        $scope.hasResultsWithMembers = function() {
            var data = dataFor($scope.day);
            if (!data || data.loading) {
                return false;
            }
            for (var i = 0; i < data.dataSets.length; ++i) {
                var ds = data.dataSets[i];
                for (var j = 0; j < ds.rows.length; ++j) {
                    var row = ds.rows[j];
                    for (var key in row) {
                        if (row[key].size) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        $scope.hasAnyNonEmptyCohort = function(rowOrDataset) {
            if (rowOrDataset.rows) { // it's a dataset
                for (var i = 0; i < rowOrDataset.rows.length; ++i) {
                    if ($scope.hasAnyNonEmptyCohort(rowOrDataset.rows[i])) {
                        return true;
                    }
                }
                return false;
            } else { // it's a row
                for (key in rowOrDataset) {
                    if (rowOrDataset[key].size) {
                        return true;
                    }
                }
                return false;
            }
        }

        function evaluate(day) {
            var forDay = new Date(day.getTime());
            if (dataFor(forDay)) {
                return;
            }

            $scope.data[forDay.getTime()] = { loading: true };

            $http.get('/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/reportingrest/reportdata/' + window.reportDefinition.uuid + '?day=' + moment(forDay).format('YYYY-MM-DD')).
                success(function(data, status, headers, config) {
                    $scope.data[forDay.getTime()] = data;
                });
        }

        $scope.viewCohort = function(day, row, column) {
            day = new Date(day.getTime());

            var ptIds = row[column.name].memberIds;
            if (ptIds == null || ptIds.length == 0) {
                $scope.viewingCohort = null;
                return;
            }

            $scope.viewingCohort = { loading: true };

            $http.get(emr.fragmentActionLink('pihcore', 'reports/cohort', 'getCohort',
                    {
                        memberIds: ptIds
                    })).
                success(function(data, status, headers, config) {
                    $scope.viewingCohort = {
                        row: row,
                        column: column,
                        day: day,
                        members: data.members
                    };
                });
        }

        evaluate($scope.day);
    }]);

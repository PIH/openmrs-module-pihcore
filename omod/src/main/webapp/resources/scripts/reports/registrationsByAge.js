angular.module('registrationsByAge', ['ui.bootstrap'])

    .controller('RegistrationsByAgeController', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

        $scope.startDate = moment().startOf('day').toDate().getTime();
        $scope.endDate = $scope.startDate;

        $scope.loading = false;
        $scope.data = null;
        $scope.selectedCategory = null;
        $scope.currentRows = [];

        $scope.openStartDate = function() {
            $timeout(function() {
                $scope.startDateOpened = true;
            });
        };

        $scope.openEndDate = function() {
            $timeout(function() {
                $scope.endDateOpened = true;
            });
        };

        $scope.runReport = function() {

            $scope.loading = true;
            $scope.data = null;
            $scope.selectedCategory = null;

            $http.get(emr.fragmentActionLink('pihcore', 'reports/registrationsByAgeReport', 'evaluate',
                {"startDate": moment($scope.startDate).format('YYYY-MM-DD'), "endDate": moment($scope.endDate).format('YYYY-MM-DD')}))
                .then(function(result) {
                    $scope.data = result.data;
                    $scope.loading = false;
                });
        };

        $scope.isLoading = function() {
            return $scope.loading;
        };

        $scope.hasResults = function() {
            return $scope.data;
        };

        $scope.numRows = function(category) {
            var categoryData = $scope.data.registrationsByAge[category];
            if (categoryData) {
                return categoryData.rows.length;
            }
            return 0;
        };

        $scope.selectCategory = function(category) {
            $scope.selectedCategory = category;
            var categoryData = $scope.data.registrationsByAge[category]
            if (categoryData) {
                $scope.currentRows = categoryData.rows;
            } else {
                $scope.currentRows =[]
            }
        }

    }]);

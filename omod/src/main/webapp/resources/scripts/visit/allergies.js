angular.module("allergies", [ "constants", "ngResource", "uicommons.common" ])
    .factory('Allergies', [ '$resource', function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/allergies/:uuid", {
            uuid: '@uuid'
        });
    }])

    .directive("currentAllergies", [ 'Allergies', "SessionInfo", function(Allergies, SessionInfo) {
        return {
            scope: {
                patient: "=",
                visituuid: "="
            },
            controller: ["$scope", function($scope) {
                if ($scope.patient) {
                    $scope.allergies = Allergies.get({uuid: $scope.patient.uuid});
                }

                $scope.session = SessionInfo.get();

                $scope.showAlergiesDetails = false;
                $scope.expandAllergies = function(showAlergiesDetails) {
                    $scope.showAlergiesDetails = !showAlergiesDetails;
                }
                $scope.goToPage = function(provider, page, opts) {
                    if (opts['returnUrl'] === undefined) {
                        opts['returnUrl'] = "/" + OPENMRS_CONTEXT_PATH + "/pihcore/visit/visit.page?visit=" + $scope.visituuid;
                    }
                    location.href = emr.pageLink(provider, page, opts);
                }

                $scope.$on('expand-all',function() {
                    $scope.showAlergiesDetails = true;
                });

                $scope.$on('contract-all',function() {
                    $scope.showAlergiesDetails = false
                });

                $scope.canEdit= function() {
                    var currentUser = new OpenMRS.UserModel($scope.session.user);
                    return currentUser.hasPrivilege('Task: emr.enterConsultNote');
                }


            }],
            templateUrl: "templates/allergies/allergiesList.page"
        }
    }]);
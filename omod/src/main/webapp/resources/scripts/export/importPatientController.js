angular.module('importPatientApp', ['ngDialog'])
    .service('ImportPatientService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                }
            };

            this.importPatient = function (patientJson) {
                var patient = JSON.parse(patientJson);
                return $http.post(CONSTANTS.URLS.PATIENT, patient);
            };
        }])
    .controller('ImportPatientController', ['$scope', 'ImportPatientService', 'ngDialog',
        function($scope, ImportPatientService, ngDialog) {

            $scope.importedPatients = [];
            $scope.content = null;
            $scope.errorMessage = null;
            $scope.patientJson = null;
            $scope.patientPage = "/coreapps/clinicianfacing/patient.page?patientId={{patientUuid}}";

            $scope.showContent = function(fileContent){
                $scope.errorMessage = null;
                $scope.patientJson = fileContent;
                $scope.content = $scope.patientJson;
            };

            $scope.navigateTo = function(patientId) {
                var destinationPage ="";
                destinationPage = Handlebars.compile($scope.patientPage)({
                        patientUuid: patientId
                    });
                window.location.href = "/" + OPENMRS_CONTEXT_PATH + destinationPage;
            };

            $scope.importPatient = function() {
                if ($scope.patientJson) {
                    return ImportPatientService.importPatient($scope.patientJson).then(function (result) {
                        if (result.status == 201) {
                            var patient = result.data;
                            $scope.importedPatients.push(patient);
                            console.log("Patient was successfully imported. Patient UUID=" + patient.uuid);
                        } else {
                            console.log("result.status = " + result.status);
                        }
                        $scope.content = null;
                        $scope.errorMessage = null;
                    }, function (error) {
                        $scope.errorMessage = JSON.stringify(error, undefined, 4);
                        console.log("Failed to create patient record: " + $scope.errorMessage);
                    });
                }
            }
            
        }])
    .directive('onReadFile', function ($parse) {
        return {
            restrict: 'A',
            scope: false,
            link: function(scope, element, attrs) {
                var fn = $parse(attrs.onReadFile);

                element.on('change', function(onChangeEvent) {
                    var reader = new FileReader();

                    reader.onload = function(onLoadEvent) {
                        scope.$apply(function() {
                            fn(scope, {fileContent:onLoadEvent.target.result});
                        });
                    };
                    reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
                });
            }
        }
    });
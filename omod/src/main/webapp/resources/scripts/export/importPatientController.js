angular.module('importPatientApp', ['ngDialog'])
    .service('ImportPatientService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                    VISIT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/visit",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                }
            };

            this.importPatient = function (patient) {
                if (angular.isDefined(patient)) {
                    return $http.post(CONSTANTS.URLS.PATIENT, patient)
                        .then( function(response) {
                            if (response.status == 201 ) {
                                //new patient record created
                                return response.data;
                            } else {
                                return null;
                            }
                        }, function(error) {
                            console.log(JSON.stringify(error, undefined, 4));
                        });
                }
            };

            this.importVisits = function(visits){
                var promises = [];
                angular.forEach(visits, function(visit) {
                   var promise = $http.post(CONSTANTS.URLS.VISIT, visit);
                    promises.push(promise);
                });
                return $q.all(promises);
            };
        }])
    .controller('ImportPatientController', ['$q', '$scope', 'ImportPatientService', 'ngDialog',
        function($q, $scope, ImportPatientService, ngDialog) {

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
                    var patientRecord = JSON.parse($scope.patientJson);
                    if (angular.isDefined(patientRecord) && patientRecord.patient) {
                        ImportPatientService.importPatient(patientRecord.patient)
                            .then( function(patient) {
                                $scope.importedPatients.push(patient);
                                return ImportPatientService.importVisits(patientRecord.visits);
                            }).then( function(visits) {                             
                                $scope.content = null;
                                $scope.errorMessage = null;
                            })
                    }
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
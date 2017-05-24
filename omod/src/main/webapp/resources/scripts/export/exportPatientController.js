angular.module('exportPatientApp', ['ngDialog'])
    .service('ExportPatientService', ['$q', '$http',
            function($q, $http) {
                    var CONSTANTS = {
                            URLS: {
                                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                            },
                            PATIENT_CUSTOM_REP: "v=custom:(uuid,display,identifiers,person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath,names,addresses,attributes))"
                    };

                    this.exportPatient = function (patientUuid) {
                            return $http.get(CONSTANTS.URLS.PATIENT + "/" + patientUuid + "?" + CONSTANTS.PATIENT_CUSTOM_REP);
                    };
            }])
    .controller('ExportPatientController', ['$scope', 'ExportPatientService', 'ngDialog', 'allPatients',
        function($scope, ExportPatientService, ngDialog, allPatients) {
                $scope.allPatients = allPatients;
                $scope.currentPatientJson = "test";

                function saveFile(data, filename) {
                        var blob = new Blob([data], {type: 'text/json'});
                        var e = document.createEvent('MouseEvents'),
                            a = document.createElement('a');

                        a.download = filename;
                        a.href = window.URL.createObjectURL(blob);
                        a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
                        e.initEvent('click', true, false, window,
                            0, 0, 0, 0, 0, false, false, false, false, 0, null);
                        a.dispatchEvent(e);
                }

                $scope.exportPatient = function (patientUuid) {
                        return ExportPatientService.exportPatient(patientUuid).then(function (result) {
                                if (result.status == 200 ) {
                                        $scope.currentPatientJson = JSON.stringify(result.data, undefined, 4);
                                        ngDialog.openConfirm({
                                                showClose: true,
                                                closeByEscape: true,
                                                scope: $scope,
                                                template: "confirmExportPatient.page"
                                        }).then(function() {
                                            saveFile($scope.currentPatientJson, patientUuid + "_patient.json") ;
                                        });
                                }
                        }, function (error) {
                                console.log("Failed to retrieve patient record: " + error);
                        });
                };

        }]);
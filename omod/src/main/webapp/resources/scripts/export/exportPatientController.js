angular.module('exportPatientApp', ['ngDialog'])
    .service('ExportPatientService', ['$q', '$http',
            function($q, $http) {
                    var CONSTANTS = {
                            URLS: {
                                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                                    VISIT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/visit",
                                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                            },
                            PATIENT_CUSTOM_REP: "v=custom:(uuid,display,identifiers,person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath,names,addresses,attributes))",
                            VISIT_CUSTOM_REP: "v=custom:(patient:(uuid),attributes,startDatetime,stopDatetime,indication,location:(uuid),visitType:(uuid))"
                    };

                    this.getPatient = function (patientUuid) {
                            return $http.get(CONSTANTS.URLS.PATIENT + "/" + patientUuid + "?" + CONSTANTS.PATIENT_CUSTOM_REP).then(function(resp) {
                                    if (resp.status == 200) {
                                            return resp.data;
                                    } else {
                                            return null;
                                    }
                            }, function (error) {
                                    console.log(JSON.stringify(err, undefined, 4));
                            });
                    };
                    this.getVisits = function (patientUuid) {
                            return $http.get(CONSTANTS.URLS.VISIT + "/"  + "?patient=" + patientUuid + "&" + CONSTANTS.VISIT_CUSTOM_REP).then(function (resp) {
                                    if (resp.status == 200) {
                                            return resp.data.results;
                                    } else {
                                            return null;
                                    }

                            }, function (err) {
                                    console.log(JSON.stringify(err, undefined, 4));
                            });
                    };

            }])
    .controller('ExportPatientController', ['$q', '$scope', 'ExportPatientService', 'ngDialog', 'allPatients',
        function($q, $scope, ExportPatientService, ngDialog, allPatients) {
                $scope.allPatients = allPatients;
                $scope.currentPatientJson = null;

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

                        var promises = [];
                        var patient = ExportPatientService.getPatient(patientUuid);
                        promises.push(patient);
                        var visits = ExportPatientService.getVisits(patientUuid);
                        promises.push(visits);
                        
                        return $q.all(promises).then(function(data) {
                                var patientRecord={};
                                patientRecord.patient = data[0];
                                patientRecord.visits =  data[1];

                                $scope.currentPatientJson = JSON.stringify(patientRecord, undefined, 4);
                                ngDialog.openConfirm({
                                        showClose: true,
                                        closeByEscape: true,
                                        scope: $scope,
                                        template: "confirmExportPatient.page"
                                }).then(function () {
                                        saveFile($scope.currentPatientJson, patientUuid + "_patient.json");
                                });
                        });


                };

        }]);
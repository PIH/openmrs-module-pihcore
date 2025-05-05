angular.module('exportPatientApp', ['ngDialog'])
    .service('ExportPatientService', ['$q', '$http',
            function($q, $http) {
                    var CONSTANTS = {
                            URLS: {
                                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                                    VISIT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/visit",
                                    ENCOUNTER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter",
                                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                            },
                            PATIENT_CUSTOM_REP: "v=custom:(uuid,display,identifiers:(uuid,identifier,identifierType:(uuid),preferred),person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath,names,addresses,attributes))",
                            VISIT_CUSTOM_REP: "v=custom:(patient:(uuid),attributes,startDatetime,stopDatetime,indication,location:(uuid),visitType:(uuid),encounters:(uuid,patient:(uuid),location:(uuid),encounterType:(uuid),encounterDatetime,voided,voidReason),voided)",
                            ENCOUNTER_CUSTOM_REP: "v=custom:(uuid,patient:(uuid),location:(uuid),encounterType:(uuid),encounterDatetime,voided,obs:(uuid,concept:(uuid),person:(uuid),obsDatetime,location:(uuid),encounter:(uuid),comment,valueModifier,valueCodedName:(uuid),groupMembers:(uuid,person:(uuid),concept:(uuid),obsDatetime,value,valueCodedName:(uuid),voided),voided,value:(uuid)))",
                            OBS_CUSTOM_REP: "v=custom:(uuid,concept:(uuid),person:(uuid),obsDatetime,location:(uuid),valueCoded:(uuid),valueDatetime,valueNumeric,valueText,valueComplex,encounter:(uuid),comment,valueModifier,valueCodedName:(uuid),obsGroup:(uuid),groupMembers:(uuid),voided)"
                    };

                    this.getPatient = function (patientUuid) {
                            return $http.get(CONSTANTS.URLS.PATIENT + "/" + patientUuid + "?" + CONSTANTS.PATIENT_CUSTOM_REP).then(function(resp) {
                                    if (resp.status == 200) {
                                            return resp.data;
                                    } else {
                                            return null;
                                    }
                            }, function (error) {
                                    console.log(JSON.stringify(error, undefined, 4));
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
                    this.getEncounters = function (patientUuid) {
                            return $http.get(CONSTANTS.URLS.ENCOUNTER + "/"  + "?patient=" + patientUuid + "&s=default&" + CONSTANTS.ENCOUNTER_CUSTOM_REP).then(function (resp) {
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
    .controller('ExportPatientController', ['$q', '$scope', 'ExportPatientService', 'ngDialog', 'allPatientsUuids',
        function($q, $scope, ExportPatientService, ngDialog, allPatientsUuids) {
                $scope.allPatients = allPatientsUuids.split(",");
                $scope.currentPatientJson = null;

                function saveFile(data, filename) {
                        var currentDate = new Date();
                        var month = currentDate.getMonth() + 1;
                        var dateString = currentDate.getFullYear() + "_"
                            + month + "_"
                            + currentDate.getDate() + "_"
                            + currentDate.getHours() + "_"
                            + currentDate.getMinutes() ;

                        var blob = new Blob([data], {type: 'text/json'});
                        var e = document.createEvent('MouseEvents'),
                            a = document.createElement('a');

                        a.download = dateString + "_" + filename;
                        a.href = window.URL.createObjectURL(blob);
                        a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
                        e.initEvent('click', true, false, window,
                            0, 0, 0, 0, 0, false, false, false, false, 0, null);
                        a.dispatchEvent(e);
                }

                function parseObs(inputObs) {
                        var obs = {};
                        obs.uuid = inputObs.uuid;
                        //obs REST ws does not like the concept.uuid format
                        obs.concept = inputObs.concept.uuid;
                        obs.person = inputObs.person;
                        obs.obsDatetime = inputObs.obsDatetime;
                        obs.location = inputObs.location;
                        obs.encounter = inputObs.encounter;
                        obs.comment = inputObs.comment;
                        obs.valueModifier = inputObs.valueModifier;
                        obs.valueCodedName = inputObs.valueCodedName;
                        obs.voided = inputObs.voided;
                        if (inputObs.value != null) {
                                if (inputObs.value.uuid) {
                                        var tempValue= {};
                                        tempValue.uuid = inputObs.value.uuid;
                                        obs.value = tempValue;
                                } else {
                                        obs.value = inputObs.value;
                                }
                        }
                        if (inputObs.groupMembers && inputObs.groupMembers.length > 0) {
                                var importedGroupMembers = [];
                                angular.forEach(inputObs.groupMembers, function(expGroupMember) {
                                        importedGroupMembers.push(parseObs(expGroupMember));
                                });
                                obs.groupMembers = importedGroupMembers;
                        }
                        return obs;
                }

                function parseEncounters(results) {
                        var encounters = [];
                        angular.forEach(results, function(result) {
                                var encounter = {};
                                encounter.uuid= result.uuid;
                                encounter.patient= result.patient;
                                if (result.location && result.location.uuid) {
                                        encounter.location = result.location.uuid;
                                }
                                encounter.encounterType= result.encounterType;
                                encounter.encounterDatetime= result.encounterDatetime;
                                encounter.voided= result.voided;
                                if (result.obs && result.obs.length > 0) {
                                    var obs = [];
                                    angular.forEach(result.obs, function(expObs) {
                                            obs.push(parseObs(expObs));
                                    });
                                    encounter.obs = obs;
                                }
                                encounters.push(encounter);
                        });
                        return encounters;
                }

                function exportPatientRecord(patientUuid, dialog) {
                        $scope.currentPatientJson = null;
                        var promises = [];
                        var patient = ExportPatientService.getPatient(patientUuid);
                        promises.push(patient);
                        var visits = ExportPatientService.getVisits(patientUuid);
                        promises.push(visits);
                        var encounters = ExportPatientService.getEncounters(patientUuid);
                        promises.push(encounters);

                        return $q.all(promises).then(function(data) {
                                var allPatients = [];
                                var patientRecord={};
                                patientRecord.patient = data[0];
                                patientRecord.visits =  data[1];
                                patientRecord.encounters =  parseEncounters(data[2]);
                                allPatients.push(patientRecord);

                                $scope.currentPatientJson = JSON.stringify(allPatients, undefined, 4);
                                if (dialog == true) {
                                        ngDialog.openConfirm({
                                                showClose: true,
                                                closeByEscape: true,
                                                scope: $scope,
                                                template: "confirmExportPatient.page"
                                        }).then(function () {
                                                saveFile($scope.currentPatientJson, patientUuid + "_patient.json");
                                        });
                                } else {
                                        return patientRecord;
                                }
                        });
                };

                $scope.exportPatient = function (patientUuid, dialog) {
                        exportPatientRecord(patientUuid, dialog);
                };

                $scope.exportAllPatients = function () {
                        var promises = [];
                        angular.forEach($scope.allPatients, function(patient) {
                                promises.push(exportPatientRecord(patient, false));
                        })
                        return $q.all(promises).then(function(data) {
                                var allPatients = [];
                                angular.forEach(data, function(record){
                                        allPatients.push(record);
                                });
                                var allPatientsJson = JSON.stringify(allPatients, undefined, 4);
                                saveFile(allPatientsJson, "all_patients.json");
                        });
                };

        }]);
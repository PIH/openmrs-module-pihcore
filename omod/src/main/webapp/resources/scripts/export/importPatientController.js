angular.module('importPatientApp', ['ngDialog'])
    .service('ImportPatientService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                    VISIT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/visit",
                    ENCOUNTER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                }
            };

            function getRestObject(url, uuid) {
                var status = {};
                return $http.get(url + "/" + uuid)
                    .then( function(resp) {
                        status.code = resp.status;
                        status.msg = null;
                        status.data = resp;
                        return status;
                    }, function(error){
                        if (error.status) {
                            status.code = error.status;
                        } else {
                            status.code = 500;
                        }
                        status.msg = JSON.stringify(error, undefined, 4);
                        status.data = [];
                        return status;
                    });
            };

            function deferredEncounter(encounter) {
                var deferred = $q.defer();
                var postUrl = CONSTANTS.URLS.ENCOUNTER;
                getRestObject(postUrl, encounter.uuid).then(function(status) {
                    if (status.code == 200) {
                        postUrl = postUrl + "/" + encounter.uuid;
                    } else {
                        //remove uuid property when creating new encounter b/c REST API does not allowed as of now to create new Encounter with given uuid
                        delete encounter.uuid;
                    }
                }, function(error) {
                    console.log(JSON.stringify(error, undefined, 4));
                }).finally( function(){
                    $http.post(postUrl, encounter).then(function(encounter) {
                        deferred.resolve(encounter);
                    }, function(error){
                        deferred.reject(error);
                    });
                });

                return deferred.promise;
            };

            this.importPatient = function (patient) {
                var deferred = $q.defer();
                if (angular.isDefined(patient)) {
                    var postUrl = CONSTANTS.URLS.PATIENT;
                    //check to see if patient exist
                    getRestObject(postUrl, patient.uuid).then(function(status) {
                        if (status.code == 200) {
                            postUrl = postUrl + "/" + patient.uuid;
                        }
                    }, function(error) {
                        console.log(JSON.stringify(error, undefined, 4));
                    }).finally( function(){
                        $http.post(postUrl, patient)
                            .then( function(response) {
                                if ( response.status == 200 || response.status == 201 ) {
                                    //new patient record was updated or created
                                    deferred.resolve( response.data );
                                } else {
                                    deferred.reject({ error: response.status});
                                }
                            }).catch( function(error) {
                                console.log(JSON.stringify(error, undefined, 4));
                                deferred.reject({ error: JSON.stringify(error, undefined, 4)});
                            }).finally( function() {
                                console.log("finished importing patient demographics:" + patient.uuid);
                            });
                    });
                }
                return deferred.promise;
            };

            this.importVisits = function(visits){
                var promises = [];
                angular.forEach(visits, function(visit) {
                    var promise = $http.post(CONSTANTS.URLS.VISIT, visit);
                    promises.push(promise);
                });
                return $q.all(promises);
            };

            this.importEncounters = function(encounters){
                var promises = [];
                angular.forEach(encounters, function(encounter) {
                      var promise = deferredEncounter(encounter);
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
            
            
            function importPatientRecord(patientRecord) {
                var deferred = $q.defer();
                var importedPatientRecord = {};
                if (patientRecord) {
                    if (angular.isDefined(patientRecord) && patientRecord.patient) {
                        ImportPatientService.importPatient(patientRecord.patient)
                            .then(function (patient) {
                                importedPatientRecord.patient = patient;
                                return ImportPatientService.importVisits(patientRecord.visits);
                            }, function (error) {
                                $scope.content = null;
                                $scope.errorMessage = JSON.stringify(error, undefined, 4);
                                deferred.reject(error);
                            }).then(function (visits) {
                            if (visits && visits.length > 0) {
                                var importedVisits = [];
                                angular.forEach(visits, function (visit) {
                                    importedVisits.push(visit.data);
                                });
                                importedPatientRecord.visits = importedVisits;
                            }
                            return ImportPatientService.importEncounters(patientRecord.encounters);
                        }, function (error) {
                            console.log("failed to import patient visits");
                            $scope.content = null;
                            $scope.errorMessage = JSON.stringify(error, undefined, 4);
                            deferred.reject(error);
                        }).then(function (encounters) {
                            if (encounters && encounters.length > 0) {
                                var importedEncounters = [];
                                angular.forEach(encounters, function (encounter) {
                                    importedEncounters.push(encounter.data);
                                });
                                importedPatientRecord.encounters = importedEncounters;
                                deferred.resolve(importedPatientRecord);
                            }
                        }, function (error) {
                            $scope.content = null;
                            $scope.errorMessage = JSON.stringify(error, undefined, 4);
                            deferred.reject(error);
                        }).catch(function (error) {
                            console.log("error=" + error);
                            deferred.reject(error);
                        });
                    }
                }

                return deferred.promise;
            };

            function importPatients(patientRecords){
                if (angular.isDefined(patientRecords) && patientRecords.length > 0) {
                    $scope.importedPatients = [];

                    patientRecords.reduce(function(p, val) {
                        return p.then(function() {
                            return importPatientRecord(val).then(function(patientRecord) {
                                $scope.importedPatients.push(patientRecord);
                            });
                        });
                    }, $q.when(true)).then(function(finalResult) {
                        $scope.content = null;
                    }, function(error) {
                        $scope.content = null;
                        $scope.errorMessage = JSON.stringify(error, undefined, 4);
                    });
                }
            }
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

            $scope.importPatients = function() {
                if ($scope.patientJson) {
                    var patientRecords = JSON.parse($scope.patientJson);
                    importPatients(patientRecords);
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
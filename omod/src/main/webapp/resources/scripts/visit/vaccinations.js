angular.module("vaccinations", [ "constants", "ngDialog", "obsService", "encounterTransaction", "filters", "pascalprecht.translate" ])

    .config(function ($translateProvider) {
        $translateProvider
            .useUrlLoader('/' + OPENMRS_CONTEXT_PATH + '/module/uicommons/messages/messages.json')
            .useSanitizeValueStrategy('escape');  // TODO is this the correct one to use http://angular-translate.github.io/docs/#/guide/19_security

    })

    .factory("VaccinationService", [ "Obs", "Concepts", "EncounterTypes", "EncounterTransaction", "EncounterRoles", "SessionInfo",
        function(Obs, Concepts, EncounterTypes, EncounterTransaction, EncounterRoles, SessionInfo) {
        return {
            getHistory: function(patient) {
                // raw REST query, will return { results: [...] }
                return Obs.query({ v:"default", patient: patient.uuid, concept: Concepts.vaccinationHistoryConstruct.uuid });
            },
            deleteDose: function(obsGroup) {
                return Obs.delete({uuid: obsGroup.uuid});
            },
            saveWithEncounter: function(visit, patient, vaccination, sequence, date) {
                return EncounterTransaction.save({
                    patientUuid: patient.uuid,
                    visitUuid: visit.uuid,
                    encounterTypeUuid: EncounterTypes.primaryCareVisit.uuid,  // TODO we will have to modify this if we start supporting multiple "consult" encounter types
                    locationUuid: SessionInfo.get().sessionLocation.uuid,
                    encounterDateTime: visit.stopDatetime ? (visit.startDatetime > date ? visit.startDatetime : date) : "",   // use the selected date if the not active visit, otherwise let transaction service figure it out
                    providers:[ {   "uuid": SessionInfo.get().currentProvider.uuid,
                        "encounterRoleUuid": EncounterRoles.consultingClinician.uuid } ],

                    observations: [
                        {
                            concept: Concepts.vaccinationHistoryConstruct.uuid,
                            groupMembers: [
                                {
                                    concept: Concepts.vaccinationGiven.uuid,
                                    value: vaccination.concept
                                },
                                {
                                    concept: Concepts.vaccinationSequenceNumber.uuid,
                                    value: sequence.sequenceNumber
                                },
                                {
                                    concept: Concepts.vaccinationDate.uuid,
                                    value: date
                                }
                            ]
                        }
                    ]
                });
            },
            saveWithoutEncounter: function(patient, vaccination, sequence, date) {
                // this is for the scenario when they are retrospectively recording a vaccination that was not done during
                // any visit captured in this patient record (e.g. patient brings a paper record from another clinic)
                var obsDatetime = new Date().toISOString();
                return Obs.save({
                    person: patient.uuid,
                    obsDatetime: obsDatetime,
                    concept: Concepts.vaccinationHistoryConstruct.uuid,
                    groupMembers: [
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationGiven.uuid,
                            value: vaccination.concept.uuid
                        },
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationSequenceNumber.uuid,
                            value: sequence.sequenceNumber
                        },
                        {
                            person: patient.uuid,
                            obsDatetime: obsDatetime,
                            concept: Concepts.vaccinationDate.uuid,
                            value: date
                        }
                    ]
                });
            }
        }
    }])

    .directive("vaccinationTable", [ "Concepts", "VaccinationService", "ngDialog", "groupMemberFilter", "SessionInfo", "$filter", "$timeout",
        function(Concepts, VaccinationService, ngDialog, groupMemberFilter, SessionInfo, $filter, $timeout) {
        return {
            restrict: "E",
            scope: {
                visit: "=",
                visits: "="
            },
            controller: function($scope) {
                var sequences = [
                    {
                        label: "pihcore.vaccination.sequence.doseZero",
                        sequenceNumber: 0
                    },
                    {
                        label: "pihcore.vaccination.sequence.doseOne",
                        sequenceNumber: 1
                    },
                    {
                        label: "pihcore.vaccination.sequence.doseTwo",
                        sequenceNumber: 2
                    },
                    {
                        label: "pihcore.vaccination.sequence.doseThree",
                        sequenceNumber: 3
                    },
                    {
                        label: "pihcore.vaccination.sequence.doseBoosterOne",
                        sequenceNumber: 11
                    },
                    {
                        label: "pihcore.vaccination.sequence.doseBoosterTwo",
                        sequenceNumber: 12
                    }
                ]
                var vaccinations = [
                    {
                        label: "pihcore.concept.name." + Concepts.bcgVaccination.uuid,
                        concept: Concepts.bcgVaccination,
                        doses: [ 1 ]
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.polioVaccination.uuid,
                        concept: Concepts.polioVaccination,
                        doses: [ 0, 1, 2, 3, 11, 12 ]
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.pentavalentVaccination.uuid,
                        concept: Concepts.pentavalentVaccination,
                        doses: [ 1, 2, 3 ]
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.rotavirusVaccination.uuid,
                        concept: Concepts.rotavirusVaccination,
                        doses: [ 1, 2 ]
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.measlesRubellaVaccination.uuid,
                        concept: Concepts.measlesRubellaVaccination,
                        doses: [ 1 ]
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.diptheriaTetanusVaccination.uuid,
                        concept: Concepts.diptheriaTetanusVaccination,
                        doses: [ 0, 1, 2, 3, 11, 12 ]
                    }
                ]

                $scope.expandVaccinations = function(showVaccinationTable) {
                    $scope.showVaccinationTable = !showVaccinationTable;
                }

                $scope.$on('expand-all',function() {
                    $scope.showVaccinationTable = true;
                });

                $scope.$on('contract-all',function() {
                    $scope.showVaccinationTable = false;
                });

                function hasCodedMember(group, concept, codedValue) {
                    return _.find(group.groupMembers, function(member) {
                        return member.concept.uuid == concept.uuid
                            && member.value.uuid == codedValue.uuid;
                    });
                }
                function hasNumericMember(group, concept, numericValue) {
                    return _.find(group.groupMembers, function(member) {
                        return member.concept.uuid == concept.uuid
                            && member.value == numericValue;
                    });
                }
                $scope.existingDose = function(sequence, vaccination) {
                    return _.find($scope.history, function(it) {
                        return hasNumericMember(it, Concepts.vaccinationSequenceNumber, sequence.sequenceNumber)
                            && hasCodedMember(it, Concepts.vaccinationGiven, vaccination.concept);
                    });
                }

                $scope.isDoseValidForVaccination = function(sequence, vaccination) {
                    return _.contains(vaccination.doses, sequence.sequenceNumber);
                }

                $scope.session = SessionInfo.get();
                $scope.showVaccinationTable = false;
                $scope.Concepts = Concepts;
                $scope.history = [];
                $scope.sequences = sequences;
                $scope.vaccinations = vaccinations;
                $scope.currentVaccinations = "";

                function loadHistory() {
                    VaccinationService.getHistory($scope.visit.patient).$promise.then(function(response) {
                        $scope.history = response.results;
                        $scope.currentVaccinations = getCurrentVaccinations();
                    });
                }

                function getCurrentVaccinations() {
                    var vaccineSequences = [];
                    _.each($scope.vaccinations, function(vaccination) {
                        _.each($scope.sequences, function(sequence) {
                            if ($scope.existingDose(sequence, vaccination)) {
                                vaccineSequences.push({ "vaccination" : vaccination.label, "sequence": sequence.label });
                            }
                        })
                    });
                    return vaccineSequences;
                }
                loadHistory();

                $scope.openDialog = function(sequence, vaccination) {
                    ngDialog.openConfirm({
                        template: "templates/vaccination/recordVaccination.page",
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: true,
                        controller: ["$scope", function($dialogScope) {
                            $dialogScope.now = new Date().toISOString();
                            $dialogScope.sequence = sequence;
                            $dialogScope.vaccination = vaccination;
                            $dialogScope.minDate = null;
                            $dialogScope.maxDate = new Date().toISOString();
                            $dialogScope.visits = $scope.visits;
                            $dialogScope.hasActiveVisit = _.find($scope.visits, function(it) {
                                return !it.stopDatetime;
                            });

                            // when they choose a visit, set the date and allowed date range based on it
                            $dialogScope.$watch("whenVisit", function(newVal) {
                                $dialogScope.date = newVal ? newVal.startDatetime : null;
                                $dialogScope.minDate = newVal ? newVal.startDatetime : null;
                                $dialogScope.maxDate = (newVal && newVal.stopDatetime) ? newVal.stopDatetime : $dialogScope.now;
                            });

                            $dialogScope.$watch("when", function(newVal) {
                                if (newVal == 'now') {
                                    $dialogScope.whenVisit = _.find($scope.visits, function(it) {
                                        return !it.stopDatetime;
                                    });
                                }
                                else if (newVal == 'visit') {
                                    //$dialogScope.date = null;
                                }
                                else if (newVal == 'no-visit') {
                                    $dialogScope.whenVisit = null; // also triggers setting date to null
                                }
                            });

                            // if the current visit is active, default to the "now" option, otherwise default to
                            // "visit" with the currently-selected visit chosen
                            if (!$scope.visit.stopDatetime) {
                                // active visit
                                $dialogScope.when = "now";
                            }
                            else {
                                $dialogScope.when = "visit";
                                $dialogScope.whenVisit = _.findWhere($scope.visits, {uuid: $scope.visit.uuid});
                                $dialogScope.date = $dialogScope.whenVisit.startDatetime;
                            }

                            $timeout(function() {
                                $(".dialog-content:visible button.confirm").focus();
                            }, 10)
                        }]
                    }).then(function(opts) {
                        if (opts.when == 'now') {
                            VaccinationService.saveWithEncounter($scope.visit, $scope.visit.patient, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        } else if (opts.when == 'visit') {
                            VaccinationService.saveWithEncounter(opts.whenVisit, $scope.visit.patient, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        } else if (opts.when == 'no-visit') {
                            VaccinationService.saveWithoutEncounter($scope.visit.patient, vaccination, sequence, opts.date)
                                .$promise.then(loadHistory);
                        }
                    });
                }

                $scope.confirmDelete = function(sequence, vaccination) {
                    var existingDose = $scope.existingDose(sequence, vaccination);
                    ngDialog.openConfirm({
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: true,
                        controller: ["$scope", function($scope) {
                            $scope.sequence = sequence;
                            $scope.vaccination = vaccination;
                            $scope.dateObs = groupMemberFilter(existingDose, Concepts.vaccinationDate);

                            $timeout(function() {
                                $(".dialog-content:visible button.confirm").focus();
                            }, 10)
                        }],
                        template: "templates/vaccination/confirmDeleteVaccination.page"
                    }).then(function(date) {
                        VaccinationService.deleteDose(existingDose)
                            .$promise.then(loadHistory);
                    });
                }

                $scope.canEdit= function() {
                    var currentUser = new OpenMRS.UserModel($scope.session.user);
                    return currentUser.hasPrivilege('Task: emr.enterConsultNote');
                }

                $scope.canDelete = function() {
                   return $scope.canEdit();
                }
            },
            templateUrl: "templates/vaccination/vaccinationTable.page"
        }
    }]);
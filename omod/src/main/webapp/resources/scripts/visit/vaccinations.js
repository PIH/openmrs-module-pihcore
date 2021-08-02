angular.module("vaccinations", [ "constants", "ngDialog", "obsService", "encounterTransaction", "filters", "pascalprecht.translate" ])

    .config(function ($translateProvider) {
        $translateProvider
            .useUrlLoader('/' + OPENMRS_CONTEXT_PATH + '/module/uicommons/messages/messages.json',   {
                queryParameter : 'localeKey'
            })
            .useSanitizeValueStrategy('escape');  // TODO is this the correct one to use http://angular-translate.github.io/docs/#/guide/19_security

    })

    .factory("VaccinationService", [ "Obs", "Concepts", "EncounterTransaction",
        function(Obs, Concepts, EncounterTransaction) {
        return {
            getHistory: function(patient) {
                // raw REST query, will return { results: [...] }
                return Obs.query({ v:"default", patient: patient.uuid, concept: Concepts.vaccinationHistoryConstruct.uuid });
            },
            deleteDose: function(obsGroup) {
                return Obs.delete({uuid: obsGroup.uuid});
            },
            saveWithEncounter: function(visit, patient, encounter, vaccination, sequence, manufacturer, lotNumber, date) {

                const vaccinationDate = encounter.encounterDatetime;
                if (date) {
                    // trim time and time zone component from date before submtting
                    vaccinationDate = moment(date).format('YYYY-MM-DD');
                }
                const groupMembers = [
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
                        value: vaccinationDate
                    }
                ];
                if (manufacturer) {
                    groupMembers.push({
                        concept: Concepts.vaccineManufacturer.uuid,
                        value: manufacturer
                    })
                }
                if (lotNumber) {
                    groupMembers.push({
                        concept: Concepts.vaccineLotNumber.uuid,
                        value: lotNumber
                    })
                }
                return EncounterTransaction.save({
                    visitUuid: visit.uuid,
                    patientUuid: patient.uuid,
                    encounterUuid: encounter.uuid,
                    // for now, we are not setting the location or the provider--will pick this up from the parent encounter
                    observations: [
                        {
                            concept: Concepts.vaccinationHistoryConstruct.uuid,
                            groupMembers: groupMembers
                        }
                    ]
                });
            }
        }
    }])

    .directive("vaccinationTable", [ "Concepts", "VaccinationService", "ngDialog", "groupMemberFilter", "SessionInfo", "$timeout",
        function(Concepts, VaccinationService, ngDialog, groupMemberFilter, SessionInfo, $timeout) {
        return {
            restrict: "E",
            scope: {
                patient: "=",               // added patient here as we may potentially use this in a non-visit, non-encounter context, but we currently arent ever passing patient into this directive
                encounter: "=",
                visit: "=",
                expandOnLoad: "="
            },
            controller: function($scope) {

                // should always have at least a patient or an encounter
                if (!$scope.patient) {
                    $scope.patient = $scope.encounter.patient;
                }

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
                    },
                    {
                        label: "pihcore.concept.name." + Concepts.covidVaccination.uuid,
                        concept: Concepts.covidVaccination,
                        doses: [ 1, 2, 3 ]
                    }
                ]

                $scope.expandVaccinations = function(showVaccinationTable) {
                    $scope.showVaccinationTable = !showVaccinationTable;
                }

                $scope.$on('expand-all',function() {
                    $scope.showVaccinationTable = true;
                });

                $scope.$on('expand-vaccinations',function() {
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

                $scope.existingDoseInEncounter = function(sequence, vaccination) {
                    var existingDose = $scope.existingDose(sequence, vaccination);
                    return $scope.encounter && existingDose && existingDose.encounter
                        && existingDose.encounter.uuid == $scope.encounter.uuid;
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
                // $scope.vaccineLotNumber = vaccineLotNumber;
                // $scope.vaccineManufacturer = vaccineManufacturer;

                loadHistory();

                function loadHistory() {
                    VaccinationService.getHistory($scope.patient).$promise.then(function(response) {
                        $scope.history = response.results;
                        $scope.currentVaccinations = getCurrentVaccinations();
                    });
                }

                function getCurrentVaccinations() {
                    var vaccineSequences = [];
                    _.each($scope.vaccinations, function(vaccination) {
                        var mostRecentSequence = _.find($scope.sequences.concat().reverse(), function(sequence) {
                            return $scope.existingDose(sequence, vaccination);
                        });
                        if (mostRecentSequence) {
                            vaccineSequences.push({ "vaccination" : vaccination.label, "sequence": mostRecentSequence.label });
                        }
                    });
                    return vaccineSequences;
                }

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
                            $dialogScope.manufacturer = $scope.manufacturer;
                            $dialogScope.lotNumber = $scope.lotNumber;
                            $dialogScope.minDate = null;
                            $dialogScope.maxDate = new Date().toISOString();
                            $dialogScope.encounter = $scope.encounter

                            if ($scope.encounter) {
                                $dialogScope.when = "encounter";
                            }
                            else {
                                $dialogScope.when = "no-encounter";
                            }

                            $timeout(function() {
                                $(".dialog-content:visible button.confirm").focus();
                            }, 10)
                        }]
                    }).then(function(opts) {
                        if (opts.when == 'encounter') {
                            VaccinationService.saveWithEncounter($scope.visit, $scope.patient, $scope.encounter, vaccination, sequence, manufacturer, lotNumber)
                                .$promise.then(loadHistory);
                        } else if (opts.when == 'no-encounter') {
                            VaccinationService.saveWithEncounter($scope.visit, $scope.patient, $scope.encounter, vaccination, sequence, manufacturer, lotNumber, opts.date)
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

                $scope.canEdit= function(sequence, vaccination) {
                    var encounter = new OpenMRS.EncounterModel($scope.encounter);
                    var currentUser = new OpenMRS.UserModel($scope.session.user);
                    return (encounter.canBeEditedBy(currentUser)
                        || encounter.participatedIn($scope.session.currentProvider)
                        || encounter.createdBy(currentUser));
                }

                $scope.canDelete = function(sequence, vaccination) {
                    var existingDose = $scope.existingDose(sequence, vaccination);
                    var encounter = new OpenMRS.EncounterModel($scope.encounter);
                    var currentUser = new OpenMRS.UserModel($scope.session.user);
                    return existingDose && existingDose.encounter.uuid == encounter.uuid &&  // only allow deleting a vaccination in the context of the visit where it was created
                        (encounter.canBeDeletedBy(currentUser)
                        || encounter.participatedIn($scope.session.currentProvider)
                        || encounter.createdBy(currentUser));
                }

                if ($scope.expandOnLoad) {
                    $scope.expandVaccinations($scope.showVaccinationTable);
                }
            },
            templateUrl: "templates/vaccination/vaccinationTable.page"
        }
    }]);

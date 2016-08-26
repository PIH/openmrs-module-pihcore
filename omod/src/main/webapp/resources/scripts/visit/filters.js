angular.module("filters", [ "uicommons.filters", "constants", "encounterTypeConfig" ])

    .filter("with", [function() {
        return function(list, property, value, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(list, function(candidate) {
                return candidate[property] && (candidate[property].uuid === value.uuid);
            });
        }
    }])

    .filter("byConcept", [function() {
        return function(listOfObs, concept, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(listOfObs, function(candidate) {
                if (!candidate.concept) {
                    return false; // too small a representation to determine
                }
                return candidate.concept.uuid === concept.uuid;
            });
        }
    }])

    .filter("valueGroupMember", [function() {
        return function(obs, concept) {
            if (!obs) {
                return null;
            }

            var candidate = _.find(obs.groupMembers, function(candidate) {
                return candidate.value.uuid === concept.uuid;
            });
            if ( candidate ) {
                return candidate.value;
            }
            return null;
        }
    }])

    .filter("groupMember", [function() {
        return function(obs, concept) {
            if (!obs) {
                return null;
            }
            return _.find(obs.groupMembers, function(candidate) {
                return candidate.concept.uuid === concept.uuid;
            });
        }
    }])

    .filter("withCodedMember", [function() {
        return function(listOfObs, concept, codedValue, justOne) {
            var f = justOne ? _.find : _.filter;
            return f(listOfObs, function(candidate) {
                return _.find(candidate.groupMembers, function(member) {
                    return member.concept.uuid == concept.uuid
                        && member.value.uuid == codedValue.uuid;
                });
            });
        }
    }])

    .filter("withoutCodedMember", [function() {
        return function(listOfObs, concept, codedValue) {
            return _.filter(listOfObs, function (candidate) {
                return !_.some(candidate.groupMembers, function(member) {
                    return member.concept.uuid == concept.uuid
                        && member.value.uuid == codedValue.uuid;
                });
            });
        }
    }])

    .filter("obs", [ "omrsDisplayFilter", function(displayFilter) {
        return function(obs, mode) {
            if (!obs) {
                return "";
            }
            var includeConcept = true;
            if ("value" === mode) {
                includeConcept = false;
            }
            var ret = includeConcept ?
                (displayFilter(obs.concept) + ": ") :
                "";
            if (obs.groupMembers) {
                ret += _.map(obs.groupMembers, function (member) {
                    // return displayFilter(member.valueCodedName ? member.valueCodedName : member.value);
                    return displayFilter(member.value);
                }).join(", ");
            } else {
                ret += displayFilter(obs.value);
            }
            return ret;
        }
    }])

    .filter("dispositionShort", ["omrsDisplayFilter", "Concepts", function (displayFilter, Concepts) {
        return function(group) {
            if (!group) {
                return "";
            }
            var disposition = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.disposition.uuid;
            });

            if (disposition) {
                return displayFilter(disposition.value)
            }
            return null;
        }
    }])

    .filter("combinedVisitDates", ["$rootScope", "omrsDisplayFilter", "serverDateFilter", "DatetimeFormats", function($rootScope, displayFilter, serverDateFilter, DatetimeFormats) {
        return function(visit) {
            if (!visit) {
                return "";
            }
            var visitStartDate = serverDateFilter(visit.startDatetime, DatetimeFormats.date );
            var returnStr = visitStartDate;
            if (visit.stopDatetime) {
                var visitStopDate = serverDateFilter(visit.stopDatetime, DatetimeFormats.date );
                if (visitStopDate != visitStartDate) {
                    returnStr = returnStr + " - " + serverDateFilter(visit.stopDatetime, DatetimeFormats.date);
                }
            }
            return returnStr;
        }
    }])

    .filter("dispositionLong", ["$rootScope", "omrsDisplayFilter", "serverDateFilter", "Concepts", "DatetimeFormats", function ($rootScope, displayFilter, serverDateFilter, Concepts, DatetimeFormats) {
        return function(group) {
            if (!group) {
                return "";
            }
            var disposition = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.disposition.uuid;
            });

            if (disposition) {

                var returnStr = displayFilter(disposition.value);

                var transferOutLocation = _.find(group.groupMembers, function(it) {
                    return it.concept.uuid == Concepts.transferOutLocation.uuid;
                });

                if (transferOutLocation) {
                    returnStr = returnStr + ": " + displayFilter(transferOutLocation.value);
                    return returnStr;
                }

                var dateOfDeath = _.find(group.groupMembers, function(it) {
                    return it.concept.uuid == Concepts.dateOfDeath.uuid;
                });

                if (dateOfDeath) {
                    returnStr = returnStr + ": " + serverDateFilter(dateOfDeath.value, DatetimeFormats.date );
                    return returnStr;
                }

                var dateOfDeath = _.find(group.groupMembers, function(it) {
                    return it.concept.uuid == Concepts.dateOfDeath.uuid;
                });

                var admissionLocation = _.find(group.groupMembers, function(it) {
                    return it.concept.uuid == Concepts.admissionLocation.uuid;
                });

                if (admissionLocation) {
                    returnStr = returnStr + ": " + displayFilter(admissionLocation.value);
                    return returnStr;
                }

                var transferLocation = _.find(group.groupMembers, function(it) {
                    return it.concept.uuid == Concepts.transferLocation.uuid;
                });

                if (transferLocation) {
                    returnStr = returnStr + ": " + displayFilter(transferLocation.value);
                    return returnStr;
                }

                return returnStr;
            }
            return null;
        }
    }])

    .filter("diagnosesInVisit", function($filter, Concepts) {

        return function(visit) {

            var diagnosisConstructs = [];

            _.each(visit.encounters, function(encounter){
                diagnosisConstructs = diagnosisConstructs.concat($filter('withCodedMember')($filter('byConcept')(encounter.obs, Concepts.diagnosisConstruct), Concepts.diagnosisOrder, Concepts.primaryOrder))
            });

            return diagnosisConstructs;
        }
    })

    .filter("diagnosesInVisitShort", function($filter, Concepts) {
        return function(visit) {

            var diagnoses = [];

            _.each($filter('diagnosesInVisit')(visit), function(diagnosisConstruct) {
                diagnoses.push($filter('diagnosisShort')(diagnosisConstruct))
            });

            return _.uniq(diagnoses);
        }
    })

    .filter("diagnosisShort", [ "omrsDisplayFilter", "Concepts", function(displayFilter, Concepts) {
        return function(group) {
            if (!group) {
                return "";
            }
            var coded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.codedDiagnosis.uuid;
            });
            if (coded) {
                // return displayFilter(coded.valueCodedName ? coded.valueCodedName : coded.value);
                return displayFilter(coded.value);
            }

            var nonCoded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.nonCodedDiagnosis.uuid;
            });
            return nonCoded ? nonCoded.value : null;
        }
    }])

    .filter("diagnosisLong", [ "omrsDisplayFilter", "Concepts", function(displayFilter, Concepts) {
        return function(group) {
            if (!group) {
                return "";
            }
            var coded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.codedDiagnosis.uuid;
            });

            var nonCoded = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.nonCodedDiagnosis.uuid;
            });

            var order = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.diagnosisOrder.uuid;
            })

            var certainty = _.find(group.groupMembers, function(it) {
                return it.concept.uuid == Concepts.diagnosisCertainty.uuid;
            }) ;

            var returnStr = "";

            if (coded) {
                returnStr = displayFilter(coded.value);
            }
            else if(nonCoded) {
                returnStr = nonCoded.value;
            }
            else {
                return null;
            }

            if (order) {
                returnStr = returnStr + ", " + displayFilter(order.value);
            }

            if (certainty) {
                returnStr = returnStr + ", " + displayFilter(certainty.value);
            }

            return returnStr;
        }
    }])

    // if val is not empty, we return before + val + after
    .filter("wrapWith", [ function() {
        return function(val, before, after) {
            if (val) {
                return before + val + after;
            }
            else {
                return val;
            }
        }
    }])

    .filter('getProviderNameFromDisplayString', function() {
        return function(input) {
           if (input && input.display){
               //we made the assumption the display string is like "Wideline Louis Charles: Dispenser"
               // /ws/rest/v1/visit/2a767422-98b2-445d-9294-d008e17b42c5?v=custom:)
                var name = input.display.split(": ");
                if (name != null && name[0].length < input.display.length) {
                    return name[0];
                } else {
                    //we made the assumption the display string is like "MAH6P - Wideline Louis Charles"
                    // /ws/rest/v1/encounter/uuid?v=full
                    name = input.display.split(" - ");
                    if (name != null) {
                        return name[name.length -1];
                    }
                }
            }
            return "";
        }
    })

    .filter('encounterTypesForVisitList',[ 'EncounterTypeConfig', function(EncounterTypeConfig) {
        return function(encounters) {

            var result = ""
            var encounterTypes = [];

            if (encounters) {
                _.each(encounters, function(e) {
                    if (EncounterTypeConfig[e.encounterType.uuid] && EncounterTypeConfig[e.encounterType.uuid].showOnVisitList
                        && encounterTypes.indexOf(e.encounterType.uuid) < 0) {
                            result = result + e.encounterType.display + ", ";
                            encounterTypes.push(e.encounterType.uuid);
                    }
                })
            }

            // trim off trailing ", "
            return result ? result.substring(0, result.length - 2) : "";
        }
    }])

    .filter('translateAs', [ "$filter", function($filter) {
        return function(input, type) {
            // first try to see if we have a custom translation property code
            if (input.uuid) {
                var result = $filter('translate')("ui.i18n." + type + ".name." + input.uuid);
                if (result) {
                    return result;
                }
            }
            if (input.display) {
                return input.display;
            }
            if (input.name) {
                return input.name;
            }
            return "";
        }
    }])

    .filter('encounterRole', function() {
        var displayOne = function(input, prefix) {
            if (input && input.provider ) {
                return input.provider;
            } else if (input && input.display) {
                return input;
            }
            return "";
        }

        var getPrimaryProvider = function(input, primaryEncounterRoleUuid) {
            var provider = null;
            // return the first provider that has the primaryEncounterRole
            if (input.length > 0) {
                _.each(input, function( key, value ) {
                    if (key.encounterRole && (key.encounterRole.uuid == primaryEncounterRoleUuid)) {
                        provider = key;
                        return false;
                    }
                });
            }
            return provider;
        }

        return function(input, primaryEncounterRoleUuid, prefix) {
            var provider = null;
            if (angular.isArray(input)) {
                if (primaryEncounterRoleUuid) {
                    //if a primaryEncounterRole is supplied than return the first provider with this role
                    provider = getPrimaryProvider(input, primaryEncounterRoleUuid);
                } else {
                    //otherwise return the first provider from the list
                    provider = input[0];
                }
            }
            return displayOne(provider, prefix);
        }
    })

    .filter('allowedWithContext', [ "SessionInfo", function(SessionInfo) {
        return function(extensionList, visit, context) {

            var hasMemberWithProperty = function(list, prop, val) {
                    return _.any(list, function(it) {
                        return it[prop] == val;
                    });
                }

            return _.filter(extensionList, function(it) {
                if (it.requiredPrivilege) {
                    if (!SessionInfo.hasPrivilege(it.requiredPrivilege)) {
                        return false;
                    }
                }
                if (it.require) {
                    // find a cleaner way to avoid polluting scope
                    var result = (function(expr) {
                        var sessionLocation = SessionInfo.get().sessionLocation;
                        var user = new OpenMRS.UserModel(SessionInfo.get().user);
                        return eval(expr);
                    })(it.require);
                    if (!result) {
                        return false;
                    }
                }
                return true;
            });
        }
    }]);
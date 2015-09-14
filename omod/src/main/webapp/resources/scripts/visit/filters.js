angular.module("filters", [ "uicommons.filters", "constants" ])

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

    .filter("obs", [ "omrs.displayFilter", function(displayFilter) {
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

    .filter("dispositionShort", ["omrs.displayFilter", "Concepts", function (displayFilter, Concepts) {
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

    .filter("dispositionLong", ["$rootScope", "omrs.displayFilter", "serverDateFilter", "Concepts", function ($rootScope, displayFilter, serverDateFilter, Concepts) {
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
                    returnStr = returnStr + ": " + serverDateFilter(dateOfDeath.value, $rootScope.dateFormat);
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

    .filter("diagnosisShort", [ "omrs.displayFilter", "Concepts", function(displayFilter, Concepts) {
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

    .filter("diagnosisLong", [ "omrs.displayFilter", "Concepts", function(displayFilter, Concepts) {
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

    .filter('allowedWithContext', [ "SessionInfo", function(SessionInfo) {
        return function(extensionList, context) {

            var util = {
                hasMemberWithProperty: function(list, prop, val) {
                    return _.any(list, function(it) {
                        return it[prop] == val;
                    });
                }
            };

            return _.filter(extensionList, function(it) {
                if (it.requiredPrivilege) {
                    if (!SessionInfo.hasPrivilege(it.requiredPrivilege)) {
                        return false;
                    }
                }
                if (it.require) {
                    // find a cleaner way to avoid polluting scope
                    var result = (function(expr) {
                        // also, clean up the server-side eval code so we can have normal js objects here without this hackiness
                        var sessionLocation = SessionInfo.get().sessionLocation;
                        sessionLocation.get = function(key) { return sessionLocation[key]; }

                        var user = new OpenMRS.UserModel(SessionInfo.get().user);
                        user.get = function(key) {
                            return user[key];
                        }
                        user.fn = {
                            hasPrivilege: function(priv) {
                                return user.hasPrivilege(priv);
                            }
                        };

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
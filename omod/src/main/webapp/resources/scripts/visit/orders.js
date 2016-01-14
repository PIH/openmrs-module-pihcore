angular.module("orders", [ "orderService", "encounterService", "ngResource", "orderEntry", "uicommons.filters", 'drugService', 'conceptService',
    "uicommons.widget.select-drug", "uicommons.widget.select-concept-from-list", "uicommons.widget.select-order-frequency",
    "ngDialog"])

    .filter('orderDates', ['serverDateFilter', function(serverDateFilter) {
        return function(order) {
            if (!order || typeof order != 'object') {
                return "";
            }
            if (order.action === 'DISCONTINUE' || !order.dateActivated) {
                return "";
            } else {
                var text = serverDateFilter(order.dateActivated);
                if (order.dateStopped) {
                    text += ' - ' + serverDateFilter(order.dateStopped);
                }
                else if (order.autoExpireDate) {
                    text += ' - ' + serverDateFilter(order.autoExpireDate);
                }
                return text;
            }
        }
    }])

    .filter('orderInstructions', [ "$filter", function($filter) {
        return function(order) {
            if (!order || typeof order != 'object') {
                return "";
            }
            if (order.action == 'DISCONTINUE') {
                return $filter('translate')("orderentryui.action.DISCONTINUE") + ": " + (order.drug ? order.drug : order.concept ).display;
            }
            else {
                var text = $filter('translate')("orderentryui.action." + order.action) + ": ";
                if (order.type == "drugorder") {
                    text += order.getDosingType().format(order);
                    if (order.quantity) {
                        text += ' #' + order.quantity + ' ' + order.quantityUnits.display;
                    }
                }
                else if (order.type == "testorder") {
                    text += order.concept.display;
                    text += " (Test)";
                }
                else {
                    text += "unknown order type: " + order.type;
                }
                return text;
            }
        }
    }])

    .directive("draftOrders", [ "OrderContext", "SessionInfo", "OrderEntryService", "EncounterTypes", "$state", "$timeout", "Drug", "ngDialog", "Concepts",
        function(OrderContext, SessionInfo, OrderEntryService, EncounterTypes, $state, $timeout, Drug, ngDialog, Concepts) {

            function mapDataToObs(draftData) {
                var ret = [];
                if (draftData.returnVisitDate) {
                    ret.push({
                        concept: Concepts.returnVisitDate,
                        value: draftData.returnVisitDate
                    });
                }
                if (draftData.clinicalManagementPlanComment) {
                    ret.push({
                        concept: Concepts.clinicalManagementPlanComment,
                        value: draftData.clinicalManagementPlanComment
                    });
                }
                return ret;
            }

            return {
                restrict: "E",
                scope: {
                    visit: "="
                },
                controller: function($scope) {
                    $scope.orderContext = OrderContext.get();
                    $scope.orderContext.draftData = {
                        clinicalManagementPlanComment: "",
                        returnVisitDate: null
                    };

                    $scope.tomorrow = moment().add(1, 'days').startOf('day');

                    $scope.getDraftOrdersByType = function(type) {
                        return _.where($scope.orderContext.draftOrders, {type: type});
                    }

                    $scope.$watch("newOrderForDrug", function(newVal) {
                        if (newVal) {
                            var draftOrder = OpenMRS.createEmptyDraftOrder($scope.orderContext);
                            draftOrder.drug = newVal;
                            OrderContext.addDraftOrder(draftOrder);
                            Drug.get({uuid: newVal.uuid, v:"custom:(uuid,display,dosageForm:full)"}).$promise.then(function(fullDrug) {
                                draftOrder.drug = fullDrug;
                                draftOrder.inferFields(OrderContext.get());
                            });
                            $scope.newOrderForDrug = null;
                        }
                    })

                    $scope.focusEdit = function() {
                        console.log("here...");
                        $timeout(function() {
                            var jq = $('.editing-draft-order').find("input:first")
                            jq.focus();
                        }, 10);
                    }

                    $scope.cancelDraft = function(draftOrder) {
                        OrderContext.cancelDraftOrder(draftOrder);
                    }

                    $scope.editDraftDrugOrder = function(draftOrder) {
                        draftOrder.editing = true;
                        OrderContext.addDraftOrder(draftOrder);
                    }

                    $scope.signAndSaveDraftOrders = function() {
                        ngDialog.openConfirm({
                            template: "templates/orders/confirmSignature.page"
                        }).then(function() {
                            var encounterContext = {
                                patient: $scope.orderContext.patient,
                                encounterType: EncounterTypes.consultationPlan,
                                location: SessionInfo.get().sessionLocation,
                                visit: $scope.visit
                            };
                            if ($scope.visit.startDatetime) {
                                encounterContext.encounterDatetime = $scope.visit.stopDatetime;
                            }

                            var obsToSave = mapDataToObs($scope.orderContext.draftData);

                            var saved = OrderEntryService.signAndSave($scope.orderContext, encounterContext, obsToSave);

                            $scope.loading = true;
                            saved.$promise.then(function(result) {
                                $scope.orderContext.draftOrders = [];
                                $scope.orderContext.draftData = {
                                    clinicalManagementPlanComment: "",
                                    returnVisitDate: null
                                };
                                $state.go("overview");
                            }, function(errorResponse) {
                                emr.errorMessage(errorResponse.data.error.message);
                                $scope.loading = false;
                            });
                        });
                    }

                    $scope.cancelAllDraftOrders = function() {
                        $scope.orderContext.draftOrders = [];
                        $state.go("overview");
                    }

                    $scope.canSaveDrafts = function() {
                        return OrderContext.canSaveDrafts();
                    }

                    $scope.$on('added-dc-order', function(dcOrder) {
                        $timeout(function() {
                            angular.element('#draft-orders input.dc-reason').last().focus();
                        });
                    })
                },
                templateUrl: "templates/orders/draftOrders.page"
            };
    }])

    .directive("activeOrders", [ "OrderEntryService", "OrderContext", "$q", function(OrderEntryService, OrderContext, $q) {
        return {
            restrict: "E",
            scope: {
                visit: "=",
                showActions: "@",
                state: "@" // "short" or "long"
            },
            controller: [ "$scope", "$rootScope", function($scope, $rootScope) {
                var orderContext = OrderContext.get();
                orderContext.$promise.then(function(test) {
                    $scope.activeDrugOrders = OrderEntryService.getActiveDrugOrders(orderContext.patient, orderContext.careSetting);
                    $scope.orderContext = orderContext;
                });

                $scope.showActions = $scope.showActions === "true";

                $scope.expand = function() {
                    $scope.state = "long";
                }

                $scope.getTemplate = function() {
                    return $scope.state === "long" ? "templates/orders/activeOrders.page" : "templates/orders/activeOrdersShort.page";
                }

                $scope.activeOrderConcepts = function() {
                    return _.pluck($scope.activeDrugOrders, "concept");
                }

                $scope.activeOrderables = function() {
                    return _.map($scope.activeDrugOrders, function(it) {
                        return it.drug ? it.drug : it.concept;
                    });
                }

                // if there's a draft order that will replace this active order, return it
                $scope.replacementFor = function(activeOrder) {
                    return _.findWhere(orderContext.draftOrders, { previousOrder: activeOrder });
                }

                $scope.reviseOrder = function(activeOrder) {
                    var revisionOrder = activeOrder.createRevisionOrder(orderContext);
                    OrderContext.addDraftOrder(revisionOrder);
                }

                $scope.discontinueOrder = function(activeOrder) {
                    var dcOrder = activeOrder.createDiscontinueOrder(orderContext);
                    dcOrder.editing = false;
                    OrderContext.addDraftOrder(dcOrder)
                    $rootScope.$broadcast('added-dc-order', dcOrder);
                }
            }],
            template: "<div ng-include=\"getTemplate()\"></div>"
        }
    }])

    .directive("orderSheet", [ "Order", "Encounter", "DatetimeFormats", function(Order, Encounter, DatetimeFormats) {
        return {
            restrict: "E",
            scope: {
                visit: "="
            },
            controller: function($scope) {
                $scope.DatetimeFormats = DatetimeFormats;
                $scope.ordersByEncounters = {};
                angular.forEach($scope.visit.encounters, function(encounter) {
                    Encounter.get({ uuid: encounter.uuid, v: "custom:(uuid,orders:full)" })
                        .$promise.then(function(encounter) {
                            $scope.ordersByEncounters[encounter.uuid] = _.map(encounter.orders, function(it) {
                                return new OpenMRS.DrugOrderModel(it);
                            });
                        });
                });

                $scope.orderList = function() {
                    var orders = _.flatten(_.values($scope.ordersByEncounters));
                    return _.sortBy(orders, "dateActivated");
                }

                $scope.anyOrders = function() {
                    return _.some(_.flatten(_.values($scope.ordersByEncounters), "orders"));
                }
            },
            templateUrl: "templates/orders/orderSheet.page"
        }
    }])

    .directive("addLabOrders", [ "$state", "$filter", "OrderContext", "Concept", "Concepts", function($state, $filter, OrderContext, Concept, Concepts) {
        return {
            restrict: "E",
            scope: {},
            controller: function($scope) {
                $scope.labs = [];
                $scope.orderLabs = {};

                Concept.get({uuid: Concepts.zlLabOrders.uuid}).$promise.then(function(results) {
                    if (results && results.setMembers && results.setMembers.length > 0) {
                        var labOrders = [];
                        _.each(results.setMembers, function(member) {
                            var concept = {uuid: member.uuid};
                            var labOrder = { label: member.display, concept: concept};
                            labOrders.push(labOrder);
                            $scope.orderLabs[member.uuid] = { concept: concept, label: member.display, selected: false };
                        })
                        $scope.labs.push({label: "", labs: labOrders});
                    }
                });

                $scope.anySelected = function() {
                    return _.findWhere($scope.orderLabs, {selected:true});
                }

                $scope.apply = function() {
                    _.each($scope.orderLabs, function(it) {
                        if (it.selected) {
                            var ord = OpenMRS.newTestOrder(OrderContext.get());
                            ord.concept = angular.copy(it.concept);
                            ord.concept.display = it.label;
                            OrderContext.addDraftOrder(ord);
                        }
                    });
                    $state.go("editPlan");
                }
            },
            templateUrl: "templates/orders/addLabOrders.page"
        }
    }])

    .directive("editDraftOrder", [ "$state", "OrderContext", function($state, OrderContext) {
        return {
            restrict: "E",
            scope: {
                draftOrder: "="
            },
            controller: function($scope) {
                $scope.dosingTypes = OpenMRS.dosingTypes;

                $scope.orderContext = OrderContext.get();
                $scope.orderContext.$promise.then(function(orderContext) {
                    $scope.cancelDraft = function(draftOrder) {
                        OrderContext.cancelDraftOrder(draftOrder);
                    }

                    // marks a draft as ready (i.e. removes the editing flag)
                    $scope.readyDraft = function(draftOrder) {
                        if (draftOrder.getDosingType().validate(draftOrder)) {
                            draftOrder.editing = false;
                            $scope.newOrderForm.$setPristine();
                            $scope.newOrderForm.$setUntouched();
                        }
                        else {
                            emr.errorMessage("Invalid");
                        }
                    }
                });
            },
            templateUrl: "../../orderentryui/templates/editDraftDrugOrder.page"
        };
    }]);
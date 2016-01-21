angular.module("visit", [ "filters", "constants", "visit-templates", "visitService", "encounterService", "obsService",
    "allergies", "orders", "vaccinations", "ui.bootstrap", "ui.router", "session", "orderEntry", "ngDialog", "appFramework",
    "configService", 'pascalprecht.translate'])

    .config(function ($stateProvider, $urlRouterProvider, $translateProvider) {

        $urlRouterProvider.otherwise("overview");

        $stateProvider
            .state("overview", {
                url: "/overview",
                templateUrl: "templates/overview.page",
                onEnter: function() {
                    breadcrumbs = breadcrumbsOverview;
                    emr.updateBreadcrumbs();
                }
            })
            .state("editPlan", {
                url: "/editPlan",
                templateUrl: "templates/orders/editPlan.page",
                onEnter: function() {
                    breadcrumbs = breadcrumbsPlan;
                    emr.updateBreadcrumbs();
                }
            })
            .state("addLabOrders", {
                url: "/addLabOrders",
                templateUrl: "templates/orders/addLabOrdersState.page"
            });

        $translateProvider
            .useUrlLoader('/' + OPENMRS_CONTEXT_PATH + '/module/uicommons/messages/messages.json')
            .useSanitizeValueStrategy('escape');  // TODO is this the correct one to use http://angular-translate.github.io/docs/#/guide/19_security

    })

    .directive("dateWithPopup", [ function() {
        return {
            restrict: 'E',
            scope: {
                ngModel: '=',
                minDate: '=',
                maxDate: '='
            },
            controller: function($scope) {
                $scope.now = new Date();
                $scope.opened = false;
                $scope.open = function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    $scope.opened = true;
                }
                $scope.options = { // for some reason setting this via attribute doesn't work
                    showWeeks: false
                }
            },
            template: '<span class="angular-datepicker">' +
                        '<input type="text" is-open="opened" ng-model="ngModel" datepicker-popup="dd-MMM-yyyy" readonly ' +
                        'datepicker-options="options" min-date="minDate" max-date="maxDate" ng-click="open($event)"/>' +
                        '<i class="icon-calendar small add-on" ng-click="open($event)" ></i>' +
                        '</span>'
        }
    }])

    // This is not a reusable directive. It does not have an isolate scope, but rather inherits scope from VisitController
    .directive("displayElement", [ "$rootScope", "Concepts", "EncounterTypes", "EncounterRoles", "VisitDisplayModel", "VisitTemplateService", function($rootScope, Concepts, EncounterTypes, EncounterRoles, VisitDisplayModel, VisitTemplateService) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.Concepts = Concepts;
                $scope.EncounterTypes = EncounterTypes;
                $scope.EncounterRoles = EncounterRoles;

                var element = $scope.element;

                $scope.eval = function(template) {
                    if (!template) {
                        return null;
                    }
                    var compiled = Handlebars.compile(template);
                    return compiled({
                        contextPath: OPENMRS_CONTEXT_PATH,
                        returnUrl: "/" + OPENMRS_CONTEXT_PATH + "/pihcore/visit/visit.page?visit=" + $scope.visit.uuid,
                        breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                        visit: $scope.visit
                    });
                }

                if (element.type === 'encounter') {
                    $scope.action = element.action;
                    $scope.encounterStubs = element.encounterStubs;
                    $scope.canAdd = element.addInline && (element.encounterStubs.length == 0 || element.allowMultiple);
                    $scope.canAdd = element.addInline && (element.encounterStubs.length == 0 || element.allowMultiple);

                    $scope.encounterTemplate = function() {
                        if ($scope.encounterStub) {
                            var state = VisitDisplayModel.encounterState($scope.encounterStub);
                            var content = element.encounter[state + "Template"];
                            if (!content) {
                                content = element.encounter["defaultTemplate"] + "Template";
                            }
                            return content;
                        }
                        else {
                            return "templates/action.page";
                        }
                    }

                    $scope.template = "templates/visitElementEncounter.page";

                }
                else if (element.type === 'consult-section') {
                    $scope.section = element;
                    $scope.template = "templates/visitElementSection.page";
                }
                else if (element.type === 'include') {
                    if (element.includeAsVisitElement) {
                        $scope.include = element.includeAsVisitElement;
                        $scope.template = "templates/visitElementInclude.page";
                    } else {
                        $scope.template = element.include;
                    }
                }
                else {
                    $scope.type = element.type;
                    $scope.template = "templates/visitElementNotYetImplemented.page";
                }

                $scope.goToPage = function(provider, page, opts) {
                    if (opts['returnUrl'] === undefined) {
                        opts['returnUrl'] = "/" + OPENMRS_CONTEXT_PATH + "/pihcore/visit/visit.page?visit=" + $scope.visit.uuid;
                    }
                    location.href = emr.pageLink(provider, page, opts);
                }
            },
            template: '<div ng-include="template"></div>'
        }
    }])

    .directive("encounter", [ "Encounter", "VisitDisplayModel", "VisitTemplateService", "OrderEntryService", "Concepts", "DatetimeFormats", "SessionInfo", "$http", "$sce",
        function(Encounter, VisitDisplayModel, VisitTemplateService, OrderEntryService, Concepts, DatetimeFormats, SessionInfo, $http, $sce) {
            return {
                restrict: "E",
                scope: {
                    encounterStub: "=encounter",
                    encounterDateFormat: "="
                },
                controller: ["$scope", function($scope) {
                    function loadFullEncounter() {

                        // load standard OpenMRS REST representation of an object
                        Encounter.get({ uuid: $scope.encounterStub.uuid, v: "full" }).
                            $promise.then(function(encounter) {
                                $scope.encounter = encounter;
                            });

                        // if the display templates for this encounter-type require a special model, fetch it (only use case now is the "encounter-in-hfe-schema" model provided by HFE)
                        if ($scope.templateModelUrl()) {
                            var url = Handlebars.compile($scope.templateModelUrl())({
                                encounter: $scope.encounter
                            });
                            $http.get("/" + OPENMRS_CONTEXT_PATH + url)
                                .then(function (response) {
                                    $scope.templateModel = response.data;
                                    if ($scope.templateModel.html) {
                                        // this enabled the "viewEncounerWithHtmlFormLong" view to display raw html returned by the htmlformentryui module
                                        $scope.html = $sce.trustAsHtml($scope.templateModel.html);
                                    }
                                });
                            // TODO error handling
                        }

                        $scope.orders = OrderEntryService.getOrdersForEncounter($scope.encounterStub);
                    }

                    $scope.encounter = $scope.encounterStub;
                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.templateModel = {};

                    var config = VisitTemplateService.getConfigFor($scope.encounterStub);
                    var currentUser = new OpenMRS.UserModel(SessionInfo.get().user);

                    $scope.session = SessionInfo.get();
                    $scope.Concepts = Concepts;
                    $scope.icon = config ? config.icon : null;
                    $scope.primaryEncounterRoleUuid = config ? config.primaryEncounterRoleUuid : null;

                    $scope.currentTemplate = function() {
                        return VisitDisplayModel.displayTemplateFor($scope.encounterStub);
                    }
                    $scope.templateModelUrl= function () {
                        return VisitDisplayModel.templateModelUrlFor($scope.encounterStub);
                    }
                    $scope.canExpand = function() {
                        return VisitDisplayModel.canExpand($scope.encounterStub);
                    }
                    $scope.canContract = function() {
                        return VisitDisplayModel.canContract($scope.encounterStub);
                    }
                    $scope.canEdit = function() {
                        return config.editUrl &&
                            new OpenMRS.EncounterModel($scope.encounter).canBeEditedBy(currentUser);
                    }
                    $scope.canDelete = function() {
                        // also allow deleting if the current user participated in the encounter as a provider
                        return new OpenMRS.EncounterModel($scope.encounter).canBeDeletedBy(currentUser);
                    }
                    $scope.expand = function() {
                        // Get the latest representation when we expand, in case things have been edited
                        loadFullEncounter();
                        VisitDisplayModel.expand($scope.encounterStub);
                    }
                    $scope.contract = function() {
                        VisitDisplayModel.contract($scope.encounterStub);
                    }
                    $scope.edit = function() {
                        $scope.$emit("request-edit-encounter", $scope.encounter);
                    }
                    $scope.delete = function() {
                        $scope.$emit("request-delete-encounter", $scope.encounter);
                    }

                    if (config.defaultState == "long") {
                        loadFullEncounter();
                    }
                }],
                template: '<div class="visit-element"><div ng-include="currentTemplate()"></div></div>'
            }
    }])

    // TODO remove any includes that are no longer being used!!
    .directive("section", [ "Encounter", "VisitDisplayModel", "VisitTemplateService", "OrderEntryService", "Concepts", "DatetimeFormats", "SessionInfo", "$http", "$sce",
        function(Encounter, VisitDisplayModel, VisitTemplateService, OrderEntryService, Concepts, DatetimeFormats, SessionInfo, $http, $sce) {
            return {
                restrict: "E",
                scope: {
                    section: "=",
                    encounter: "=",
                    visit: "="
                },
                controller: ["$scope", function($scope) {

                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.Concepts = Concepts;
                    $scope.state = 'short';
                    $scope.sectionLoaded = false;

                    function loadSection() {

                        if ($scope.encounter) {
                            var url = Handlebars.compile($scope.section.templateModelUrl)({
                                consultEncounter: $scope.encounter
                            });
                            $http.get("/" + OPENMRS_CONTEXT_PATH + url)
                                .then(function (response) {
                                    $scope.sectionLoaded = true;
                                    $scope.templateModel = response.data;
                                    if ($scope.templateModel.html) {
                                        // this enabled the "viewEncounerWithHtmlFormLong" view to display raw html returned by the htmlformentryui module
                                        $scope.html = $sce.trustAsHtml($scope.templateModel.html);
                                    }
                                });
                            // TODO error handling
                        }

                    }

                    function openSectionForEdit() {
                        var url = Handlebars.compile($scope.section.editUrl)({
                            visit: $scope.visit,
                            consultEncounter: $scope.encounter,
                            patient: $scope.visit.patient,
                            returnUrl: window.encodeURIComponent(window.location.pathname + "?visit=" + $scope.visit.uuid)
                        });

                        emr.navigateTo({ applicationUrl: (!url.startsWith("/") ? '/' : '') + url });
                    }

                    $scope.canExpand = function() {
                        return $scope.state === 'short' && $scope.section.longTemplate;
                    }
                    $scope.canContract = function() {
                        return $scope.state === 'long';
                    }
                    $scope.canEdit = function() {
                        return true;
                        // TODO should just be able to pass in the consult encounter here?
                        //return new OpenMRS.EncounterModel($scope.encounter).canBeEditedBy(currentUser);
                    }

                    $scope.expand = function() {
                        if ($scope.canExpand()) {
                            if (!$scope.sectionLoaded) {
                                loadSection();
                            }
                            $scope.template = $scope.section.longTemplate;
                            $scope.state = 'long';
                        }
                    }
                    $scope.contract = function() {
                        if ($scope.canContract()) {
                            $scope.template = $scope.section.shortTemplate;
                            $scope.state = 'short';
                        }
                    }

                    $scope.edit = function() {
                        if ($scope.encounter) {
                            openSectionForEdit();
                        }
                        else {
                            $scope.$emit('start-consult');
                            $scope.$on('consult-started', function(event, encounterUuid) {
                                $scope.encounter = {
                                    uuid: encounterUuid
                                }
                                openSectionForEdit();
                            })
                        }
                    }

                    $scope.$on('expand-all',function() {
                        if ($scope.canExpand) {
                            $scope.expand();
                        }
                    });

                    $scope.$on('contract-all',function() {
                        if ($scope.canExpand) {
                            $scope.contract();
                        }
                    });

                    $scope.template = $scope.section.shortTemplate;

                }],
                template: '<div class="visit-element"><div ng-include="template"></div></div>'
            }
        }])


    // this is not a reusable directive, and it does not have an isolate scope
    .directive("visitDetails", [ "Visit", "ngDialog", function(Visit, ngDialog) {
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.edit = function() {
                    ngDialog.openConfirm({
                        showClose: true,
                        closeByEscape: true,
                        closeByDocument: false, // in case they accidentally click the background to close a datepicker
                        controller: [ "$scope", function($dialogScope) {
                            $dialogScope.now = new Date();
                            $dialogScope.visit = $scope.visit;
                            $dialogScope.newStartDatetime = $scope.visit.startDatetime;
                            $dialogScope.startDateLowerLimit = $scope.getVisitStartDateLowerLimit($scope.visit);
                            $dialogScope.startDateUpperLimit = $scope.getVisitStartDateUpperLimit($scope.visit);
                            $dialogScope.endDateLowerLimit = $scope.getVisitEndDateLowerLimit($scope.visit);
                            $dialogScope.endDateUpperLimit = $scope.getVisitEndDateUpperLimit($scope.visit);
                            $dialogScope.newStopDatetime = $scope.visit.stopDatetime;
                            $dialogScope.newLocation = $scope.visit.location;
                        }],
                        template: "templates/visitDetailsEdit.page"
                    }).then(function(opts) {
                        // TODO this logic doesn't do the right thing if the client is in a different time zone than the server
                        var start = emr.formatDatetimeForREST(moment(opts.start).startOf('day'));
                        var stop = opts.stop ? emr.formatDatetimeForREST(moment(opts.stop).endOf('day')) : null;
                        new Visit({
                            uuid: $scope.visit.uuid,
                            startDatetime: start,
                            stopDatetime: stop
                        }).$save(function(v) {
                                $scope.reloadVisits();
                                $scope.reloadVisit();
                        });
                    });
                }
            },
            templateUrl: 'templates/visitDetails.page'
        }
    }])

    // inherits scope from visit overview controller
    .directive("chooseVisitTemplate", [ "VisitTemplateService", "VisitAttributeTypes", "VisitService", "ngDialog", function(VisitTemplateService, VisitAttributeTypes, VisitService, ngDialog) {
        return {
            restrict: 'E',
            controller: function($scope) {

                $scope.availableTemplates = VisitTemplateService.getAllowedVisitTemplates($scope.visit);
                $scope.activeTemplate = VisitTemplateService.getCurrent();
                $scope.multipleTemplates = $scope.availableTemplates && $scope.availableTemplates.length > 1;

                $scope.$watch("visit", function() {
                    if ($scope.visit) {
                        $scope.selectedTemplate = $scope.visit.getAttributeValue(VisitAttributeTypes.visitTemplate);
                        $scope.newVisitTemplate = _.findWhere($scope.availableTemplates, {name: $scope.selectedTemplate});
                        $scope.activeTemplate = VisitTemplateService.getCurrent();
                    }
                });

                $scope.choosingTemplate = false;

                function confirmChangingTemplate(VisitAttribute, existing) {
                    ngDialog.openConfirm({
                        showClose: false,
                        closeByEscape: true,
                        closeByDocument: true,
                        template: "templates/confirmVisitTemplateChange.page"
                    }).then(function() {
                        // render the visit using the new selected template
                        if ($scope.newVisitTemplate ) {
                            new VisitAttribute({
                                attributeType: VisitAttributeTypes.visitTemplate.uuid,
                                value: $scope.newVisitTemplate.name
                            }).$save().then(function () {
                                    $scope.activeTemplate = $scope.newVisitTemplate;
                                    $scope.choosingTemplate = false;
                                    $scope.reloadVisit();
                                });
                        } else {
                            new VisitAttribute({uuid: existing.uuid}).$delete().then(function() {
                                $scope.reloadVisit();
                            });
                        }
                    }, function() {
                        $scope.choosingTemplate = false;
                        $scope.reloadVisit();
                    });
                }

                $scope.save = function() {
                    var existing = $scope.visit.getAttribute(VisitAttributeTypes.visitTemplate);
                    var VisitAttribute = VisitService.visitAttributeResourceFor($scope.visit);

                    if (existing) {
                        // if we change from an existing template, prompt the user to confirm the change
                        confirmChangingTemplate(VisitAttribute, existing);
                    } else {
                        // if there is no template set then just assign the new template
                        new VisitAttribute({
                            attributeType: VisitAttributeTypes.visitTemplate.uuid,
                            value: $scope.newVisitTemplate.name
                        }).$save().then(function() {
                                $scope.choosingTemplate = false;
                                $scope.reloadVisit();
                            });
                    }
                };
            },
            templateUrl: 'templates/chooseVisitTemplate.page'
        }
    }])

    .directive("chooseDisposition", [ function() {
        return {

        }
    }])

    .directive("visitListDropdown", [ function() {
        return {
            templateUrl: 'templates/visitListDropdown.page'
        }
    }])

    .directive("visitActionsDropdown", [ function() {
        return {
            templateUrl: 'templates/visitActionsDropdown.page'
        }
    }])

    .service("VisitTemplateService", [ "VisitTemplates", "VisitAttributeTypes", "Encounter","ConfigService",
        function(VisitTemplates, VisitAttributeTypes, Encounter, ConfigService) {

            var currentTemplate = null;

            // TODO what if this is not populated in time?
            var visitTemplates;
            ConfigService.getVisitTemplates().then(function (templates) {
                visitTemplates = templates;
            })

            return {
                getAllowedVisitTemplates: function(visit) {
                    return _.filter(
                        _.map(visitTemplates, function(visitTemplate) {
                            return VisitTemplates[visitTemplate];
                        }), function(it) {
                            return it.allowedFor(visit);
                        });
                },

                setCurrent: function(visitTemplate) {
                    currentTemplate = visitTemplate;
                },

                getCurrent: function() {
                    return currentTemplate;
                },

                getConfigFor: function(encounter) {
                    if (currentTemplate && currentTemplate.encounterTypeConfig) {
                        var config = currentTemplate.encounterTypeConfig[encounter.encounterType.uuid];
                        return config ? config : currentTemplate.encounterTypeConfig.DEFAULT;
                    }
                    return null;
                },

                determineFor: function(visit) {
                    var specified = new OpenMRS.VisitModel(visit).getAttributeValue(VisitAttributeTypes.visitTemplate);
                    if (specified && VisitTemplates[specified]) {
                        return angular.copy(VisitTemplates[specified]);
                    }
                    else {
                        //var template = visit.patient.person.age < 15 ? "pedsInitialOutpatient" : "adultInitialOutpatient";
                        var template = "timeline";
                        return angular.copy(VisitTemplates[template]);
                    }
                },

                applyVisit: function(visitTemplate, visit, VisitDisplayModel) {
                    this.setCurrent(visitTemplate);
                    var encounters = _.reject(visit.encounters, function(it) { return it.voided; });
                    _.each(visitTemplate.elements, function(it) {
                        if (it.type == 'encounter') {
                            it.encounterStubs = _.filter(encounters, function(candidate) {
                                // TODO support specifying by form also
                                return candidate.encounterType.uuid === it.encounter.encounterType.uuid;
                            });
                        }
                    });
                    _.each(encounters, function(it) {
                        var config = visitTemplate.encounterTypeConfig[it.encounterType.uuid];
                        if (!config) {
                            config = visitTemplate.encounterTypeConfig.DEFAULT;
                        }
                        VisitDisplayModel.encounterStates[it.uuid] = config.defaultState;
                    });
                },

              /*  getVisitActions: function() {

                    AppFrameworkService.getUserExtensionsFor("patientDashboard.visitActions").then(function(ext) {
                        $scope.visitActions = ext;
                    })

                    if (!currentTemplate) {
                        return []
                    };

                    var elements = _.filter(currentTemplate.elements, function (element) {

                        return !element.addInline
                                && ( (element.type == 'encounter' && (element.encounterStubs.length == 0 || element.allowMultiple))
                                || element.type == 'consult-section')
                    });

                    return _.compact(_.pluck(elements, "action"));
                },*/

                getConsultEncounterType: function() {
                    if (currentTemplate && currentTemplate.consultEncounterType) {
                        return currentTemplate.consultEncounterType;
                    }
                    else {
                        return null;
                    }
                }

            }
        }])

    .factory("VisitDisplayModel", [ "VisitTemplateService", function(VisitTemplateService) {
        var model = {};
        model.reset = function() {
            model.encounterStates = {}; // maps from encounter.uuid => "short" or "long"
        };
        model.encounterState = function(encounter) {
            return model.encounterStates ? model.encounterStates[encounter.uuid] : null;
        }
        model.canExpand = function(encounter) {
            var current = model.encounterState(encounter);
            var config = VisitTemplateService.getConfigFor(encounter);
            return current === 'short' && config && config.longTemplate;
        };
        model.canContract = function(encounter) {
            var current = model.encounterState(encounter);
            var config = VisitTemplateService.getConfigFor(encounter);
            return current === 'long' && config && config.shortTemplate;
        };
        model.expand = function(encounter) {
            model.encounterStates[encounter.uuid] = 'long';
        };
        model.contract = function(encounter) {
            model.encounterStates[encounter.uuid] = 'short';
        };
        model.displayTemplateFor = function(encounter) {
            var config = VisitTemplateService.getConfigFor(encounter);
            if (config) {
                var state = model.encounterStates[encounter.uuid];
                if (state) {
                    return config[state + "Template"];
                }
            }
            return "templates/encounters/defaultEncounterShort.page"

        }
        model.templateModelUrlFor = function(encounter) {
            var config = VisitTemplateService.getConfigFor(encounter);
            if (config) {
                return config["templateModelUrl"];
            }
            return "";  // no custom template model by default
        }
        model.reset();
        return model;
    }])

    .controller("VisitController", [ "$scope", "$rootScope", "$translate", "Visit", "VisitTemplateService", "Allergies", "CareSetting", "$q", "$state",
        "$timeout", "OrderContext", "VisitDisplayModel", "ngDialog", "Encounter","OrderEntryService", "AppFrameworkService",
        'visitUuid', 'patientUuid', 'locale', "DatetimeFormats", "EncounterTransaction", "SessionInfo",
        function($scope, $rootScope, $translate, Visit, VisitTemplateService, Allergies, CareSetting, $q, $state, $timeout, OrderContext,
                 VisitDisplayModel, ngDialog, Encounter, OrderEntryService, AppFrameworkService, visitUuid, patientUuid, locale, DatetimeFormats, EncounterTransaction, SessionInfo) {

            $rootScope.DatetimeFormats = DatetimeFormats;

            $scope.VisitDisplayModel = VisitDisplayModel;

            $scope.visitUuid = visitUuid;
            $scope.patientUuid = patientUuid;
            $scope.consultEncounter = null;

            $scope.today = new Date();

            $scope.allExpanded = false;

            loadVisits(patientUuid);
            loadVisit(visitUuid);

            $translate.use(locale);

            function sameDate(d1, d2) {
                return d1 && d2 && d1.substring(0, 10) == d2.substring(0, 10);
            }

            function loadVisits(patientUuid) {
                Visit.get({patient: $scope.patientUuid, v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:default)"}).$promise.then(function(response) {
                    // TODO fetch more pages?
                    $scope.visits = response.results;
                    // TODO what does this do?
                    //$scope.isLatestVisit = !$scope.visit.stopDatetime || _.max($scope.visits, function(it) { return new Date(it.startDatetime) }).startDatetime === $scope.visit.startDatetime;
                });
            }

            function loadVisit(visitUuid) {
                Visit.get({ uuid: visitUuid, v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:(uuid,display,encounterDatetime,patient:default,location:ref,form:ref,encounterType:ref,obs:ref,orders:ref,voided,visit:ref,encounterProviders),patient:default,visitType:ref,attributes:default)" })
                    .$promise.then(function(visit) {
                        $scope.visit = new OpenMRS.VisitModel(visit);
                        $scope.visitIdx = $scope.getVisitIdx(visit);
                        $scope.encounterDateFormat = sameDate($scope.visit.startDatetime, $scope.visit.stopDatetime) ? "hh:mm a" : "hh:mm a (d-MMM)";
                        $scope.consultEncounter = VisitTemplateService.getConsultEncounterType() ? $scope.visit.getEncounterByType(VisitTemplateService.getConsultEncounterType().uuid) : null;

                        if ($scope.consultEncounter) {
                            loadConsultEncounter();
                        }

                        $scope.visitTemplate = VisitTemplateService.determineFor($scope.visit);
                        VisitTemplateService.applyVisit($scope.visitTemplate, $scope.visit, $scope.VisitDisplayModel);

                        AppFrameworkService.getUserExtensionsFor("patientDashboard.visitActions").then(function(ext) {
                            $scope.visitActions = ext;
                        })

                        // TODO refactor so that OrderContext has better logic for knowing when it is configured/ready, so that we don't have to nest this
                        $scope.careSettings.$promise.then(function() {
                            OrderContext.setCareSetting(_.findWhere($scope.careSettings.results, { careSettingType: "OUTPATIENT" }));
                            OrderContext.setPatient($scope.visit.patient);
                        })
                    });
                VisitDisplayModel.reset();
            }

            function loadConsultEncounter() {
                // load standard OpenMRS REST representation of an object
                Encounter.get({ uuid: $scope.consultEncounter.uuid, v: "full" }).
                    $promise.then(function(encounter) {
                        $scope.consultEncounter = encounter;
                    });
            }


          /*  // TODO is this still needed/used?
            function getVisitParameter() {
                var index = location.href.indexOf("?");
                var temp = location.href.substring(index + 1);
                index = temp.indexOf("visit=");
                temp = temp.substring(index + 6);
                index = temp.indexOf("&");
                if (index > 0) {
                    temp = temp.substring(0, index);
                }
                index = temp.indexOf("#");
                if (index > 0) {
                    temp = temp.substring(0, index);
                }
                return temp;
            }*/

            //$rootScope.$on('$stateChangeStart', function(event, toState) {
            //    if (toState.name === "newPrescription") {
            //        $scope.newDraftDrugOrder = OpenMRS.createEmptyDraftOrder(OrderContext.get().careSetting);
            //    }
            //});

            $rootScope.$on("request-edit-encounter", function(event, encounter) {
                var config = VisitTemplateService.getConfigFor(encounter);
                if (config.editUrl) {
                    var url = Handlebars.compile(config.editUrl)({
                        patient: encounter.patient,
                        visit: $scope.visit,
                        encounter: encounter,
                        breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                        returnUrl: "/" + OPENMRS_CONTEXT_PATH + "/pihcore/visit/visit.page?visit=" + $scope.visit.uuid
                    });
                    emr.navigateTo({applicationUrl: url});
                }
            });

            $rootScope.$on("request-delete-encounter", function(event, encounter) {
                ngDialog.openConfirm({
                    showClose: true,
                    closeByEscape: true,
                    closeByDocument: true,
                    controller: function($scope) {
                        OrderEntryService.getOrdersForEncounter(encounter).$promise.then(function(orders) {
                            $scope.activeOrders = _.filter(orders, function(it) {
                                return it.isActive();
                            });
                        });
                        $timeout(function() {
                            $(".dialog-content:visible button.confirm").focus();
                        }, 10)
                    },
                    template: "templates/confirmDeleteEncounter.page"
                }).then(function() {
                    Encounter.delete({uuid: encounter.uuid})
                        .$promise.then(function() {
                            $scope.reloadVisit();
                        });
                });
            });

            $scope.$on('visit-changed', function(event, visit) {
                if ($scope.visitUuid == visit.uuid) {
                    $scope.reloadVisit();
                }
            });

            $scope.$on('start-consult', function() {
                EncounterTransaction.save({
                    patientUuid: $scope.patientUuid,
                    visitUuid: $scope.visitUuid,
                    locationUuid: SessionInfo.get().sessionLocation.uuid,
                    encounterTypeUuid: VisitTemplateService.getConsultEncounterType().uuid,
                    providers:[ SessionInfo.get().currentProvider ],
                    encounterDateTime: $scope.visit.stopDatetime ? $scope.visit.startDatetime : new Date()
                }, function(result) {
                    $scope.consultEncounterUuid = result.encounterUuid;
                    $scope.$broadcast("consult-started", result.encounterUuid);
                })
            })


            $scope.careSettings = CareSetting.query({v:"default"});

            $scope.reloadVisit = function() {
                loadVisit($scope.visitUuid);
            }

            $scope.reloadVisits = function() {
                loadVisits($scope.patientUuid);
            }

            $scope.getVisitIdx = function(visit) {
                var idx = -1;
                if (visit != null && typeof $scope.visits !== "undefined" && $scope.visits != null && $scope.visits.length > 0 ) {
                    for (var i=0; i < $scope.visits.length ; i++) {
                        if (visit.uuid == $scope.visits[i].uuid) {
                            idx = i;
                            break;
                        }
                    }
                }
                return idx;
            }

            $scope.getVisitStartDateLowerLimit = function(visit) {
                var lowerLimitDate = new Date(visit.startDatetime);
                if ( $scope.visitIdx != -1 ) {
                  if (($scope.visitIdx + 1) == $scope.visits.length) {
                      //this is the oldest visit in the list and the there is no lower date limit
                      return null;
                  } else {
                      var previousVisitEndDate = new Date($scope.visits[$scope.visitIdx + 1].stopDatetime);
                      // return the day after the end date of the previous visit in the list
                      previousVisitEndDate.setDate(previousVisitEndDate.getDate() + 1);
                      return previousVisitEndDate;
                  }
                }
                return new Date(lowerLimitDate);
            }

            $scope.getVisitStartDateUpperLimit = function(visit) {
                var upperLimitDate = new Date(visit.startDatetime);
                if (visit.encounters != null && visit.encounters.length > 0) {
                    // the visit cannot start after the date of the oldest encounter that is part of this visit
                    return visit.encounters[visit.encounters.length -1].encounterDatetime;
                }

                return new Date(upperLimitDate);
            }

            $scope.getVisitEndDateLowerLimit = function(visit) {
                var lowerLimitDate = new Date(visit.stopDatetime);
                if (visit.encounters != null && visit.encounters.length > 0) {
                    // the visit cannot end before the date of the newest encounter that is part of this visit
                    return visit.encounters[0].encounterDatetime;
                }
                return new Date(lowerLimitDate);
            }

            $scope.getVisitEndDateUpperLimit = function(visit) {
                var upperLimitDate = new Date(visit.stopDatetime);
                if ( $scope.visitIdx != -1 ) {
                    if ( $scope.visitIdx  == 0) {
                        //this is the newest visit in the list and the upper date limit is today
                        return new Date();
                    } else {
                        var nextVisitStartDate = new Date($scope.visits[$scope.visitIdx -1].startDatetime);
                        // return the day before the start date of the next visit in the list
                        nextVisitStartDate.setDate(nextVisitStartDate.getDate() -1);
                        return nextVisitStartDate;
                    }
                }
                return new Date(upperLimitDate);
            }

            $scope.goToVisit = function(visit) {
                $scope.visitUuid = visit.uuid;
                $state.go("overview");
            }

           // TODO figure out if we can get rid of this function
            $scope.$watch('visitUuid', function(newVal, oldVal) {
                loadVisit(newVal);
            })



            $scope.hasDraftOrders = function() {
                return OrderContext.get().draftOrders.length > 0;
            }

            $scope.visitAction = function(visitAction) {

                if (visitAction.type == 'script') {
                    // TODO
                } else
                {
                    var visitModel = angular.extend({}, $scope.visit);
                    visitModel.id = $scope.visit.uuid; // HACK! TODO: change our extensions to refer to visit.uuid
                    visitModel.active = !$scope.visit.stopDatetime;

                    var returnUrl = window.encodeURIComponent(window.location.pathname + "?visit=" + $scope.visit.uuid);

                    var url = Handlebars.compile(visitAction.url)({
                        visit: visitModel,
                        consultEncounter: $scope.consultEncounter,
                        patient: $scope.visit.patient,
                        returnUrl: returnUrl
                    });

                    // if return hasn't been specified as a template in visitAction.url, make sure we append it
                    if (url.indexOf('returnUrl') == -1) {
                        url = url + "&returnUrl=" + returnUrl;
                    }

                    emr.navigateTo({ applicationUrl: (!url.startsWith("/") ? '/' : '') + url });
                }
            }


            $scope.expandAll = function() {
                $scope.$broadcast("expand-all");
                $scope.allExpanded = true;
            }

            $scope.contractAll = function() {
                $scope.$broadcast("contract-all");
                $scope.allExpanded = false;
            }

            window.onbeforeunload = function() {
                if (OrderContext.hasUnsavedData()) {
                    // TODO: localize
                    return "You have unsaved changes, are you sure you want to discard them?";
                }
            }

            //$scope.configFor = function(encounter) {
            //    if ($scope.visitTemplate && $scope.visitTemplate.encounterTypeConfig) {
            //        var templates = $scope.visitTemplate.encounterTypeConfig[encounter.encounterType.uuid];
            //        return templates ? templates : $scope.visitTemplate.encounterTypeConfig.DEFAULT;
            //    }
            //    return null;
            //}

            //$scope.displayTemplateFor = function(encounter) {
            //    var config = $scope.configFor(encounter);
            //    if (config) {
            //        var state = VisitDisplayModel.encounterStates[encounter.uuid];
            //        if (state) {
            //            return config[state + "Template"];
            //        }
            //    }
            //    return "templates/defaultEncounterShort.page"
            //}


        }]);
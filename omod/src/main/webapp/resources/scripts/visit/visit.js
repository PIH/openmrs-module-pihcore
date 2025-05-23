angular.module("visit", [ "filters", "constants", "encounterTypeConfig", "visitService", "encounterService",  "locationService", "obsService",
    "allergies", "vaccinations", "ui.bootstrap", "ui.router", "session", "ngDialog", "appFramework",
    "configService", "queueEntryService", 'pascalprecht.translate'])

    .config(function ($stateProvider, $urlRouterProvider, $translateProvider, $templateRequestProvider) {

        $urlRouterProvider.otherwise("overview");

        $stateProvider
            .state("overview", {
                url: "/overview",
                templateUrl: "templates/overview.page",
                onEnter: function () {
                    // each time we enter the "overview" state we "reset" the breadcrumbs to their original value (using .slice() to copy by value)
                    breadcrumbs = breadcrumbsOverview;
                    emr.updateBreadcrumbs();
                }
            })
            .state("vaccinations", {
                url: "/vaccinations",
                templateUrl: "templates/vaccination/vaccinationsPage.page"
            })
            .state("visitList", {
                url: "/visitList",
                templateUrl: "templates/visitList.page"
            })
            .state("encounterList", {
              url: "/encounterList",
              templateUrl: "templates/encounters/encounterList.page"
            })
            .state("encounterOverview", {
              url: "/encounterOverview",
              templateUrl: "templates/encounters/encounterOverview.page"
            })
            .state("print", {
                url: "/print",
                templateUrl: "templates/print.page"
            })

        $translateProvider
            .useUrlLoader('/' +  OPENMRS_CONTEXT_PATH + '/module/uicommons/messages/messages.json',  {
                queryParameter : 'localeKey'
            })
            .fallbackLanguage('en')
            .useSanitizeValueStrategy('escape');  // TODO is this the correct one to use http://angular-translate.github.io/docs/#/guide/19_security

        $templateRequestProvider.httpOptions({
            headers: { Accept: 'text/html' }
        });

    })

    .directive("dateWithPopup", [ function() {
        return {
            restrict: 'E',
            scope: {
                ngModel: '=',
                minDate: '=',
                maxDate: '=',
                clearButton: '='
            },
            controller: function($scope) {
                $scope.now = new Date();
                $scope.opened = false;
                $scope.open = function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    $scope.opened = true;
                }
                $scope.clear = function() {
                  $scope.ngModel = null
                }
                $scope.options = { // for some reason setting this via attribute doesn't work
                    showWeeks: false
                }
            },
            template: '<span class="angular-datepicker">' +
                        '<input type="text" is-open="opened" ng-model="ngModel" datepicker-popup="dd-MMM-yyyy" readonly ' +
                        'datepicker-options="options" min-date="minDate" max-date="maxDate" ng-click="open($event)"/>' +
                        '<i class="icon-calendar small add-on" ng-click="open($event)" ></i>' +
                        '<i class="icon-remove small add-on" ng-click="clear()" ng-show="clearButton" ></i>'  +
                        '</span>'
        }
    }])

    .directive("encounter", [ "Encounter", "Concepts", "OrderTypes", "EncounterRoles", "DatetimeFormats", "SessionInfo", "EncounterTypeConfig", "$http", "$sce", "$filter",
        function(Encounter, Concepts, OrderTypes, EncounterRoles, DatetimeFormats, SessionInfo, EncounterTypeConfig, $http, $sce, $filter) {
            return {
                restrict: "E",
                scope: {
                    encounter: "=",
                    visit: "=",
                    selected: '=',
                    encounterDateFormat: "=",
                    expandOnLoad: "=",
                    country: "=",
                    site: "="
                },
                controller: ["$scope", function($scope) {

                    var config = EncounterTypeConfig.get($scope.encounter, $scope.country, $scope.site);

                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.Concepts = Concepts;
                    $scope.OrderTypes = OrderTypes;
                    $scope.EncounterRoles = EncounterRoles;
                    $scope.session = SessionInfo.get();
                    $scope.encounterState = $scope.selected ? "long" : (config ? config.defaultState : "short");
                    $scope.template = config ? config[$scope.encounterState + "Template"] :
                        ($scope.encounterState == 'long' ? "templates/encounters/defaultEncounterLong.page" : "templates/encounters/defaultEncounterShort.page");

                    $scope.icon = config ? config.icon : null;
                    $scope.primaryEncounterRoleUuid = config ? config.primaryEncounterRoleUuid : null;
                    $scope.sections = config && config.sections ? $filter('allowedWithContext')(config.sections, $scope.visit) : [];
                    $scope.showSections = $scope.selected;

                    function loadFullEncounter() {
                        // if the display templates for this encounter-type require a special model, fetch it (only use case now is the "encounter-in-hfe-schema" model provided by HFE)
                        if ($scope.templateModelUrl()) {
                            var url = Handlebars.compile($scope.templateModelUrl())({
                                encounter: $scope.encounter
                            });
                            $http.get("/" + OPENMRS_CONTEXT_PATH + url)
                                .then(function (response) {
                                    $scope.templateModel = response.data;
                                    getFieldsWithValue($scope.templateModel.sections);
                                    if ($scope.templateModel.html) {
                                        // this enabled the "viewEncounerWithHtmlFormLong" view to display raw html returned by the htmlformentryui module
                                        $scope.html = $sce.trustAsHtml($scope.templateModel.html);
                                        //$scope.doesNotHaveExistingObs = !$scope.templateModel.hasExistingObs;
                                    }
                                });
                            // TODO error handling
                        }
                        // otherwise load the standard OpenMRS REST representation of an object
                        else {
                            Encounter.get({ uuid: $scope.encounter.uuid, v: "full" }).
                                $promise.then(function(encounter) {
                                    $scope.encounter = encounter;
                                });
                        }
                    }

                    function getFieldsWithValue(sections) {
                        if (sections) {
                            sections.forEach((section) => {
                                let activeFields = [];
                                if (section.fields) {
                                    for (let j = 0; j < section.fields.length; j++) {
                                        const field = section.fields[j];
                                        if (field.hasOwnProperty("fields")){
                                            parseFields(activeFields, field.fields);
                                        } else {
                                            addFieldWithValue(activeFields, field);
                                        }
                                    }
                                    section.activeFields = activeFields;
                                }
                            })
                        }
                    }

                    function parseFields(fieldsWithValue, fields) {
                        if ( fields ) {
                            for (let i=0; i < fields.length; i++ ) {
                                const field = fields[i];
                                addFieldWithValue(fieldsWithValue, field);
                            }
                        }
                        return fieldsWithValue;
                    }
                    function addFieldWithValue(fieldsWithValue, field) {
                        if (field.value) {
                            let foundField = fieldsWithValue.find(({ name }) => name === field.name);
                            if (foundField) {
                                foundField.value = foundField.value + ", " + field.value;
                            } else {
                                fieldsWithValue.push({
                                    name: field.name,
                                    value: field.value
                                });
                            }
                        }
                    }

                    $scope.goToLabResults = function () {
                      var labResultsUrl = "owa/labworkflow/index.html?patient={{patient}}&returnUrl={{returnUrl}}#/LabResults";
                      var url = Handlebars.compile(labResultsUrl)({
                        patient: $scope.visit.patient.uuid,
                        returnUrl: window.encodeURIComponent(window.location.pathname + "?visit=" + $scope.visit.uuid )
                      });

                      emr.navigateTo({ applicationUrl: (url.indexOf("/") != 0 ? '/' : '') + url });
                    };

                    $scope.templateModelUrl= function () {
                        if (config) {
                            return config["templateModelUrl"];
                        }
                        return "";  // no custom template model by default
                    }

                    $scope.canExpand = function() {
                        return $scope.encounterState === 'short' && config
                            && (config.longTemplate || config.sections);
                    }

                    $scope.canContract = function() {
                        return $scope.encounterState === 'long' && config && config.shortTemplate;
                    }

                    $scope.canView = function() {
                        return config && config.viewUrl;
                    }

                    $scope.canEdit = function() {
                        var encounter = new OpenMRS.EncounterModel($scope.encounter);
                        var currentUser = new OpenMRS.UserModel($scope.session.user);
                        return config && config.editUrl &&
                            $scope.encounter.form &&
                            (encounter.canBeEditedBy(currentUser)
                                || encounter.participatedIn($scope.session.currentProvider)
                                || encounter.createdBy(currentUser));
                    }

                    $scope.canDelete = function() {
                        var encounter = new OpenMRS.EncounterModel($scope.encounter);
                        var currentUser = new OpenMRS.UserModel($scope.session.user);
                        return config && config.editUrl &&
                            (encounter.canBeDeletedBy(currentUser)
                                || encounter.participatedIn($scope.session.currentProvider)
                                || encounter.createdBy(currentUser));
                    }

                    $scope.expand = function() {
                        // Get the latest representation when we expand, in case things have been edited
                        loadFullEncounter(); // TODO make this a promise with a then
                        $scope.encounterState = 'long';
                        $scope.showSections = true;
                        // show long template if available, otherwise just stay with short template (use case: encounters with sections but no long view)
                        $scope.template = config && config["longTemplate"]  ?
                            config["longTemplate"] : config ? config["shortTemplate"] : "templates/encounters/defaultEncounterShort.page";
                    }

                    $scope.contract = function() {
                        $scope.encounterState = 'short';
                        $scope.showSections = false;
                        $scope.template = config ? config["shortTemplate"] : "templates/encounters/defaultEncounterShort.page"
                    }

                    $scope.view = function() {
                        $scope.$emit("request-view-encounter", $scope.encounter);
                    }

                    $scope.edit = function() {
                        $scope.$emit("request-edit-encounter", $scope.encounter);
                    }

                    $scope.delete = function() {
                        $scope.$emit("request-delete-encounter", $scope.encounter);
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

                    if ($scope.encounterState == "long" || $scope.expandOnLoad) {
                        $scope.expand();
                    }
                }],
                templateUrl: 'templates/encounters/encounter.page'
            }
    }])

    .directive("queueEntry", [ "QueueEntry", "Concepts", "DatetimeFormats", "SessionInfo", "$http", "$sce", "$filter",
        function(QueueEntry, Concepts, DatetimeFormats, SessionInfo, $http, $sce, $filter) {
            return {
                restrict: "E",
                scope: {
                    queueEntry: "=",
                    visit: "=",
                    selected: '=',
                    queueEntryDateFormat: "="
                },
                controller: ["$scope", function($scope) {

                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.Concepts = Concepts;
                    $scope.session = SessionInfo.get();

                    function loadQueueEntry() {
                        QueueEntry.get({ uuid: $scope.queueEntry.uuid, v: "custom:(uuid,display,queue:(uuid,display,location:(uuid,display),service:(uuid,display)),status:(uuid,display),priority:(uuid,display),startedAt,endedAt)" }).
                        $promise.then(function(queueEntry) {
                            $scope.queueEntry = queueEntry;
                        });
                    }

                }],
                templateUrl: 'templates/queue/queueEntry.page'
            }
        }])

    .directive("encounterSection", [ "Concepts", "OrderTypes", "DatetimeFormats", "SessionInfo", "$http", "$sce", "$timeout", "initialRouterState",
        function(Concepts, OrderTypes, DatetimeFormats, SessionInfo, $http, $sce, $timeout, initialRouterState) {
            return {
                restrict: "E",
                scope: {
                    section: "=",
                    encounter: "=",
                    visit: "=",
                    expandOnLoad: "="
                },
                controller: ["$scope", function($scope) {

                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.Concepts = Concepts;
                    $scope.OrderTypes = OrderTypes;
                    $scope.state = 'short';
                    $scope.sectionLoaded = false;
                    $scope.session = SessionInfo.get();
                    $scope.template = $scope.section.shortTemplate;

                    function loadSection() {
                        if ($scope.encounter && $scope.section.templateModelUrl && !$scope.sectionLoaded) {
                            var url = Handlebars.compile($scope.section.templateModelUrl)({
                                encounter: $scope.encounter
                            });
                            $scope.sectionLoaded = true; // not entirely accurate, because it hasn't been loaded yet, bu we want to avoid double loadings
                            $http.get("/" + OPENMRS_CONTEXT_PATH + url)
                                .then(function (response) {
                                    $scope.templateModel = response.data;
                                    if ($scope.templateModel.html) {
                                        // this enabled the "viewEncounerWithHtmlFormLong" view to display raw html returned by the htmlformentryui module
                                        $scope.html = $sce.trustAsHtml($scope.templateModel.html);
                                        //$scope.doesNotHaveExistingObs = !$scope.templateModel.hasExistingObs;
                                    }
                                });
                            // TODO error handling
                        }
                    }

                    function loadPrescriptionsSection() {
                        if ($scope.encounter && $scope.section.printTemplateUrl) {
                            var url = Handlebars.compile($scope.section.printTemplateUrl)({
                                encounter: $scope.encounter
                            });

                            $http.get("/" + OPENMRS_CONTEXT_PATH + url)
                                .then(function (response) {
                                    $scope.templateModel = response.data;
                                    if ($scope.templateModel.html) {
                                        // this enabled the "viewEncounerWithHtmlFormLong" view to display raw html returned by the htmlformentryui module
                                        $scope.html = $sce.trustAsHtml($scope.templateModel.html);
                                        //$scope.doesNotHaveExistingObs = !$scope.templateModel.hasExistingObs;
                                    }
                                });
                        }
                    }

                    function openSectionForEdit() {
                        var url = Handlebars.compile($scope.section.editUrl)({
                            visit: $scope.visit,
                            encounter: $scope.encounter,
                            patient: $scope.visit.patient,
                            breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                            returnUrl: window.encodeURIComponent(window.location.pathname
                              + "?visit=" + $scope.visit.uuid
                              + "&encounter=" + $scope.encounter.uuid
                              + "&currentSection=" + $scope.section.id
                              +  (initialRouterState ? "&initialRouterState=" + initialRouterState : ''))
                        });

                        emr.navigateTo({ applicationUrl: (url.indexOf("/") != 0 ? '/' : '') + url });
                    }

                    $scope.showEncounterDetails = $scope.section.showEncounterDetails;

                    $scope.hideIncompleteAlert = $scope.section.hideIncompleteAlert;

                    $scope.canExpand = function() {
                        return $scope.state === 'short' && $scope.section.longTemplate;
                    }
                    $scope.canContract = function() {
                        return $scope.state === 'long';
                    }

                    $scope.canEnter = function() {
                        var currentUser = new OpenMRS.UserModel($scope.session.user);
                        return (currentUser.hasPrivilege('Task: emr.enterConsultNote') && !$scope.visit.stopDatetime)
                            || currentUser.hasPrivilege('Task: emr.retroConsultNote');
                    }

                    $scope.canEdit = function() {
                        if (!$scope.encounter) {
                            return $scope.canEnter();
                        }
                        else {
                            var encounter = new OpenMRS.EncounterModel($scope.encounter);
                            var currentUser = new OpenMRS.UserModel($scope.session.user);
                          return $scope.section.editUrl
                            && (encounter.canBeEditedBy(currentUser)
                            || encounter.participatedIn($scope.session.currentProvider)
                            || encounter.createdBy(currentUser));
                        }
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
                        openSectionForEdit();
                    }

                    function printDiv(data) {
                        if (data) {
                            var mywindow = window.open('', 'my div', 'height=400,width=600');
                            mywindow.document.write('<html><head><title>Print Prescriptions</title>');
                            /*optional stylesheet*/ //mywindow.document.write('<link rel="stylesheet" href="main.css" type="text/css" />');
                            mywindow.document.write('</head><body >');
                            mywindow.document.write(data);
                            mywindow.document.write('</body></html>');

                            mywindow.document.close();
                            mywindow.focus();

                            mywindow.print();
                            mywindow.close();

                            return true;
                        }
                        return false;
                    }

                    $scope.printPrescriptions = function() {
                        $scope.sectionLoaded = false;
                        if (!$("#prescriptionsDiv").is(':visible')) {
                            loadPrescriptionsSection();
                            $scope.template = $scope.section.printTemplate;
                            $scope.state = 'long';

                            var print = function () {
                                if ($http.pendingRequests.length > 0) {
                                    $timeout(print); // Wait for the print template to be loaded
                                } else {
                                    printDiv($("#prescriptionsDiv").html());
                                }
                            }
                            $timeout(print);
                        } else {
                            printDiv($("#prescriptionsDiv").html());
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

                    if ($scope.expandOnLoad) {
                        $scope.expand();
                    }

                }],
                template: '<div class="visit-element indent"><div ng-include="template"></div></div>'
            }
        }])

    .directive("includeSection", [
        function() {
            return {
                restrict: "E",
                scope: {
                    section: "=",
                    encounter: "=",
                    visit: "=",
                    expandOnLoad: "="
                },
                controller: function($scope) {

                },
                template: '<div class="indent" ng-include="section.template"></div>'
            }
        }])

    // this is not a reusable directive, and it does not have an isolate scope
    .directive("visitDetails", [ "Visit", "LocationService", "ngDialog", function(Visit, LocationService, ngDialog) {
        // TODO make sure this at least gives as error message in case of failure
        return {
            restrict: 'E',
            controller: function($scope) {
                $scope.edit = function() {
                    LocationService.getLocations({ v: "default" }).then(function(locations) {
                        ngDialog.openConfirm({
                            showClose: true,
                            closeByEscape: true,
                            closeByDocument: false, // in case they accidentally click the background to close a datepicker
                            controller: ["$scope", function ($dialogScope) {
                                $dialogScope.now = new Date();
                                $dialogScope.visit = $scope.visit;
                                $dialogScope.newStartDatetime = $scope.visit.startDatetime;
                                $dialogScope.startDateLowerLimit = $scope.previousVisitEndDatetime($scope.visit);
                                $dialogScope.startDateUpperLimit = $scope.firstEncounterInVisitDatetime($scope.visit);
                                $dialogScope.endDateLowerLimit = $scope.mostRecentEncounterInVisitDatetime($scope.visit);
                                $dialogScope.endDateUpperLimit = $scope.nextVisitStartDatetime($scope.visit);
                                $dialogScope.newStopDatetime = $scope.visit.stopDatetime || '';
                                $dialogScope.locations = locations.filter(l => l.tags.some(t => t.display === "Visit Location" ));
                                $dialogScope.newLocation = $scope.visit.location;
                            }],
                            template: "templates/visitDetailsEdit.page"
                        }).then(function (opts) {
                            // we trim off the time zone, because we don't want to send it along: the server will just assume that it is in it's timezone
                            var start = moment(opts.start).startOf('day').format('YYYY-MM-DDTHH:mm:ss.SSS');
                            var stop = (opts.stop ?
                                moment(opts.stop).endOf('day').isBefore(moment()) ?      // set end date to end of day *unless* end of day is after current datetime (ie, end date is today)--then just set to current datetime)
                                    moment(opts.stop).endOf('day').format('YYYY-MM-DDTHH:mm:ss.SSS') :
                                    moment().format('YYYY-MM-DDTHH:mm:ss.SSS') :
                                null);
                            new Visit({
                                uuid: $scope.visit.uuid,
                                startDatetime: start,
                                stopDatetime: stop,
                                location: opts.location
                            }).$save(function (v) {
                                $scope.reloadVisits();
                                $scope.reloadVisit();
                            }, function(response) {
                                emr.serverValidationErrorMessage(response);
                            });
                        });
                    });
                }
            },
            templateUrl: 'templates/visitDetails.page'
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

    .directive("visitActions", [ function() {
      return {
        templateUrl: 'templates/visitActions.page'
      }
    }])

    .controller("VisitController", [ "$scope", "$rootScope", "$translate","$http", "Visit", "$state",
        "$timeout", "$filter", "ngDialog", "Encounter", "EncounterTypeConfig", "CoreappsService", "AppFrameworkService", "QueueEntry",
        "visitUuid", "visitTypeUuid", "encounterTypeUuid", "suppressActions", "patientUuid", "encounterUuid", "locale", "currentSection", "goToNext", "nextSection", "initialRouterState", "country", "site", "DatetimeFormats", "EncounterTransaction", "SessionInfo", "Concepts", "OrderTypes", "VisitTypes",
        function($scope, $rootScope, $translate, $http, Visit, $state, $timeout, $filter,
                 ngDialog, Encounter, EncounterTypeConfig, CoreappsService, AppFrameworkService, QueueEntry, visitUuid, visitTypeUuid, encounterTypeUuid, suppressActions, patientUuid, encounterUuid,
                 locale, currentSection, goToNext, nextSection, initialRouterState, country, site, DatetimeFormats, EncounterTransaction, SessionInfo, Concepts, OrderTypes, VisitTypes) {

          const visitRef = "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:(uuid,display,encounterDatetime,patient:default,location:ref,form:(uuid,version),encounterType:ref,obs:default,orders:ref,voided,visit:(uuid,display,location:(uuid)),encounterProviders:(uuid,encounterRole,provider,dateCreated),creator:ref),patient:default,visitType:ref,attributes:default)"
          const encountersRef = "custom:(uuid,encounterDatetime,patient:(uuid,patientId,display),encounterType:(uuid,display),location:(uuid,name,display),encounterProviders:(uuid,display),form:(uuid,display),obs:(uuid,value,concept:(id,uuid,name:(display),datatype:(uuid)))";
          const queueEntriesRef = "custom:(uuid,display,queue:(uuid,display,location:(uuid,display),service:(uuid,display)),status:(uuid,display),priority:(uuid,display),startedAt,endedAt)";

            // we are in the "Next" workflow and should immediately redirect to the next section
            if (goToNext && encounterUuid) {
                goToNextSection(currentSection, nextSection, patientUuid, encounterUuid, visitUuid);
            }
            else {
                // otherwise do standard loading
               loadPage(initialRouterState);
            }

            function loadPage(state) {

                $rootScope.DatetimeFormats = DatetimeFormats;
                $scope.Concepts = Concepts;
                $scope.OrderTypes = OrderTypes;
                $scope.VisitTypes = VisitTypes;

                $scope.session = SessionInfo.get();

                $scope.visitUuid = visitUuid;
                $scope.visitTypeUuid = visitTypeUuid;
                $scope.encounterTypeUuid = encounterTypeUuid;
                $scope.suppressActions = suppressActions;
                $scope.patientUuid = patientUuid;
                $scope.encounterUuid = encounterUuid;

                $scope.country = country;
                $scope.site = site;

                $scope.printButtonDisabled = false;
                $scope.allExpanded = false;

                $translate.use(locale);

                loadVisits(patientUuid);
                loadVisit(visitUuid);
                switchToState(state);

            }

            /**
             * Navigates to the next section of the form. If the parameter nextSection has a value, jump to that section,
             * otherwise jump to the next section in chronological sequence based on the encounterTypeConfig.js
             * @param currentSection
             * @param nextSection
             * @param patientUuid
             * @param encounterUuid
             * @param visitUuid
             */
            function goToNextSection(currentSection, nextSection, patientUuid, encounterUuid, visitUuid) {

                // fetch the visit and encounter so we add them to the context, and can determine the encounter type, which we need to determine where to redirect to (this all seems like a hack)
                Visit.get({
                    uuid: visitUuid,
                    v: visitRef
                }).
                    $promise.then(function(visit) {
                        var encounter = _.find(visit.encounters, function(encounter) { return encounter.uuid === encounterUuid} );
                        var sections =  $filter('allowedWithContext')(EncounterTypeConfig.get(encounter, country, site).sections, visit);

                        var redirectToSectionIdx = 0;

                        if ( nextSection ) {
                            redirectToSectionIdx = sections.findIndex(({ id }) => id === nextSection);
                        } else {
                            // if current section is null, we just want to jump to first "real" section (idx = 0)
                            // otherwise find the current section and add one
                            if (currentSection) {
                                i = 0;
                                while (i < sections.length && sections[i].id != currentSection) {
                                    i++;
                                }
                                redirectToSectionIdx = i + 1;
                            }
                        }
                        // we want to the redirect to the edit section of the *next* section
                        if (redirectToSectionIdx < sections.length && sections[redirectToSectionIdx].editUrl) {

                            var url = Handlebars.compile(sections[redirectToSectionIdx].editUrl)({
                                visit: {
                                    uuid: visitUuid,
                                    patient: {
                                        uuid: patientUuid
                                    }
                                },
                                encounter: {
                                    uuid: encounterUuid
                                },
                                patient: {
                                    uuid: patientUuid
                                },
                                breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                                returnUrl: window.encodeURIComponent(
                                  window.location.pathname
                                  + "?visit=" + visitUuid
                                  + "&encounter=" + encounterUuid
                                  + "&currentSection=" + sections[redirectToSectionIdx].id
                                  +  (initialRouterState ? "&initialRouterState=" + initialRouterState : ''))
                            });

                            emr.navigateTo({ applicationUrl: (url.indexOf("/") != 0 ? '/' : '') + url });
                        }
                        else {
                            // if we don't find an editurl, just open the page, passing in the id of the next section (which represents the router state to transition to, currently used for vaccines)
                            if (redirectToSectionIdx < sections.length) {
                                loadPage(sections[redirectToSectionIdx].id);
                            }
                            else {
                                // if there are any errors, just continue loading the page
                                loadPage();
                            }
                        }
                    });


            }

            function switchToState(state) {
                if (state) {
                    if (encounterUuid) {
                        Encounter.get({uuid: encounterUuid, v: "full"}).$promise.then(function (encounter) {
                            $scope.encounter = encounter;
                            $scope.expandOnLoad = true;
                            $state.go(state);
                        });
                    }
                }
            }

            function sameDate(d1, d2) {
                return d1 && d2 && d1.substring(0, 10) == d2.substring(0, 10);
            }

            function loadEncounters(patientUuid, encounterTypeUuid) {
                Encounter.get({
                  patient: patientUuid,
                  encounterType: encounterTypeUuid ? encounterTypeUuid : '',
                  order: 'desc',
                  v: encountersRef
                }).$promise.then(function(response) {
                  $scope.encounters = response.results;
                });
            }

            function getAllPatientEncounters(patientUuid) {
                let encounters = [];
                const encounterRequestArgs = {
                    patient: patientUuid,
                    encounterType: encounterTypeUuid ? encounterTypeUuid : '',
                    order: 'desc',
                    v: "custom:(encounterDatetime,encounterType:(uuid,display))"
                };
                return Encounter.get(encounterRequestArgs).$promise.then(function(response) {
                    encounters = transformEncounterData(response);
                    return getMoreEncountersIfPresent(response).then(function() {
                        return encounters;
                    });
                });
                function getMoreEncountersIfPresent(response) {
                    const nextLink = response.links && response.links.find(l => l.rel === "next");
                    if (nextLink) {
                        return $http.get(nextLink.uri).then(function(newResponse) {
                            const data = newResponse.data;
                            encounters = encounters.concat(transformEncounterData(data));
                            return getMoreEncountersIfPresent(data);
                        })
                    } else {
                        return Promise.resolve();
                    }
                }
                function transformEncounterData(response) {
                    return response.results.map(function (r) {
                        return {
                            encounterDateTime: r.encounterDatetime,
                            encounterTypeUuid: r.encounterType.uuid,
                            encounterTypeName: r.encounterType.display,
                        }
                    });
                }
            }

            function loadVisits(patientUuid) {
                Visit.get({
                  patient: $scope.patientUuid,
                  visitType: $scope.visitTypeUuid ? $scope.visitTypeUuid : $scope.VisitTypes.clinicalOrHospitalVisit.uuid,
                  v: "custom:(uuid,startDatetime,stopDatetime)"
                }).$promise.then(function(response) {
                    $scope.visits = response.results;
                });
            }

            function loadVisit(visitUuid) {
                if (visitUuid) {
                    Promise.all([
                        getAllPatientEncounters($scope.patientUuid),
                        Visit.get({uuid: visitUuid, v: visitRef}).$promise,
                        QueueEntry.get({visit: visitUuid, v: queueEntriesRef, isEnded: ""}).$promise
                    ]).then(function ([allEncounters, visit, queueEntries]) {
                        visit.encounters = _.reject(visit.encounters, function (it) {
                            return it.voided;
                        });
                        const scopeVisit = new OpenMRS.VisitModel(visit);
                        scopeVisit.allEncounters = allEncounters;
                        scopeVisit.queueEntries = queueEntries.results;

                        const visitEvents = [];
                        scopeVisit.encounters.forEach((encounter) => {
                            visitEvents.push({...encounter, eventType: "encounter", eventDate: encounter.encounterDatetime});
                        });
                        scopeVisit.queueEntries.forEach((queueEntry) => {
                            visitEvents.push({...queueEntry, eventType: "queueEntry", eventDate: queueEntry.startedAt});
                        });

                        scopeVisit.visitEvents = visitEvents.sort((e1, e2) => {
                            let ret = e1.eventDate > e2.eventDate ? -1 : (e1.eventDate < e2.eventDate ? 1 : 0);
                            if (ret === 0) {
                                ret = e1.eventType === 'encounter' ? 1 : -1;
                            }
                            return ret;
                        });

                        $scope.visit = scopeVisit;
                        $scope.visitIdx = $scope.getVisitIdx(visit);
                        $scope.encounterDateFormat = sameDate($scope.visit.startDatetime, $scope.visit.stopDatetime) ? "hh:mm a" : "hh:mm a (d-MMM)";

                        if ($scope.suppressActions !== true) {
                          CoreappsService.getUserExtensionsFor("patientDashboard.visitActions", $scope.patientUuid, $scope.visit.uuid).then(function (ext) {
                              $scope.visitActions = ext;
                          });
                        }
                    });
                }
            }

            $rootScope.$on("request-view-encounter", function(event, encounter) {
                var config = EncounterTypeConfig.get(encounter, country, site);
                if (config.viewUrl) {
                    var url = Handlebars.compile(config.viewUrl)({
                        patient: encounter.patient,
                        visit: $scope.visit,
                        encounter: encounter,
                        breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                        returnUrl: encodeURIComponent(
                          window.location.pathname
                          + "?visit=" + $scope.visit.uuid
                          + "&encounter=" + encounter.uuid
                          +  (initialRouterState ? "&initialRouterState=" + initialRouterState : ''))
                    });
                    emr.navigateTo({applicationUrl: url});
                }
            });

            $rootScope.$on("request-edit-encounter", function(event, encounter) {
                var config = EncounterTypeConfig.get(encounter, country, site);
                if (config.editUrl) {
                    var url = Handlebars.compile(config.editUrl)({
                        patient: encounter.patient,
                        visit: $scope.visit,
                        encounter: encounter,
                        breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                        returnUrl: encodeURIComponent(
                            window.location.pathname
                            + "?visit=" + $scope.visit.uuid
                          + "&encounter=" + encounter.uuid
                          +  (initialRouterState ? "&initialRouterState=" + initialRouterState : ''))
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

            $rootScope.$on('$stateChangeSuccess', function(event, toState) {
                if (toState.name == 'print') {
                    $scope.$on('$viewContentLoaded', function(){
                        $scope.printAndClose();
                    });

                }
            });

            $rootScope.$on('$stateChangeSuccess', function(event, toState) {
                // when we enter the visitList view, load the full visit information so we can disaply the encounters and diagnoses for each visit
                if (toState.name == 'visitList') {
                    // TODO: this is going to grow to be *way* too big--loads every obs for a patient!
                    Visit.get({
                        patient: $scope.patientUuid,
                        visitType: $scope.visitTypeUuid ? $scope.visitTypeUuid : $scope.VisitTypes.clinicalOrHospitalVisit.uuid,
                        v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:(uuid,display,encounterDatetime,location:ref,encounterType:ref,obs:default,voided),attributes:default)"
                    }).$promise.then(function (response) {
                        _.each(response.results, function (visit) {
                            visit.encounters =  _.reject(visit.encounters, function(it) {
                                return it.voided;
                            });
                        })
                        $scope.visits = response.results;
                    });
                } else if (toState.name == 'encounterList') {
                    loadEncounters($scope.patientUuid, $scope.encounterTypeUuid);
                }
            });

            $scope.$on('visit-changed', function(event, visit) {
                if ($scope.visitUuid == visit.uuid) {``
                    $scope.reloadVisit();
                }
            });

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

            $scope.previousVisitEndDatetime = function(visit) {
                if ($scope.visitIdx != -1) {
                    if (($scope.visitIdx + 1) == $scope.visits.length) {
                        //this is the oldest visit in the list and the there is no lower date limit
                        return null;
                    } else {
                        const previousVisitEndDate = new Date($scope.visits[$scope.visitIdx + 1].stopDatetime);
                        // return the day after the end date of the previous visit in the list
                        previousVisitEndDate.setDate(previousVisitEndDate.getDate() + 1);
                        return previousVisitEndDate;
                    }
                }
                return null;
            }

            $scope.firstEncounterInVisitDatetime = function(visit) {
                if (visit.encounters != null && visit.encounters.length > 0) {
                    // the visit cannot start after the date of the oldest encounter that is part of this visit
                    return visit.encounters[visit.encounters.length -1].encounterDatetime;
                }
                else {
                    return null;
                }
            }

            $scope.mostRecentEncounterInVisitDatetime = function(visit) {
                if (visit.encounters != null && visit.encounters.length > 0) {
                    // the visit cannot end before the date of the newest encounter that is part of this visit
                    return visit.encounters[0].encounterDatetime;
                }
                else {
                    return null;
                }

            }

            $scope.nextVisitStartDatetime = function(visit) {
                if ( $scope.visitIdx > 0) {
                    // if this isn't the most recent visit, return the day before the start date of the next visit in the list
                    const nextVisitStartDate = new Date($scope.visits[$scope.visitIdx -1].startDatetime);
                    nextVisitStartDate.setDate(nextVisitStartDate.getDate() -1);
                    return nextVisitStartDate;
                }
                else {
                  return null;
                }
            }

            $scope.goToNextSection = function(currentSection) {
                goToNextSection(currentSection, null, $scope.patientUuid, $scope.encounterUuid, $scope.visitUuid)
            }

            $scope.goToVisit = function(visit) {
                if (visit) {
                    $scope.visitUuid = visit.uuid;
                }
                $state.go("overview");
            }

          $scope.confirmDeleteEncounter = function(encounter) {
            if ( encounter ) {
                  ngDialog.openConfirm({
                    showClose: true,
                    closeByEscape: true,
                    closeByDocument: true,
                    controller: function($scope) {
                      $timeout(function() {
                        $(".dialog-content:visible button.confirm").focus();
                      }, 10)
                    },
                    template: "templates/confirmDeleteEncounter.page"
                  }).then(function() {
                    Encounter.delete({uuid: encounter.uuid})
                      .$promise.then(function() {
                       loadEncounters($scope.patientUuid, $scope.encounterTypeUuid);
                    });
                  });
            }
          }

          $scope.goToEncounter = function(encounter) {
            if (encounter) {
              var config = EncounterTypeConfig.get(encounter, country, site);
              if (config.editUrl) {
                var url = Handlebars.compile(config.editUrl)({
                  patient: encounter.patient,
                  encounter: encounter,
                  breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                  returnUrl: encodeURIComponent("/" + OPENMRS_CONTEXT_PATH
                    + "/pihcore/visit/visit.page?encounterType=" + encounter.encounterType.uuid
                    + "&patient=" + encounter.patient.uuid
                    + "&encounter=" + encounter.uuid
                    (initialRouterState ? "&initialRouterState=" + initialRouterState : ''))
                });
                emr.navigateTo({applicationUrl: url});
              }
            }
          }

            $scope.goToVisitList = function() {
                $state.go("visitList");
            }

            $scope.$watch('visitUuid', function(newVal, oldVal) {
                loadVisit(newVal);
            })

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
                        patient: $scope.visit.patient,
                        returnUrl: returnUrl
                    });

                    // if return URL hasn't been specified as a template in visitAction.url, make sure we append it
                    var index = url.indexOf('returnUrl');
                    var index = url.indexOf('returnUrl');
                    if (index == -1) {
                        url = url + "&returnUrl=" + returnUrl;
                    } else {
                        // we need to encode everything after "returnURL="
                        var encodedReturnUrl = window.encodeURIComponent(url.substr(index + 10));
                        url = url.substring(0, index + 10) + encodedReturnUrl;
                    }
                    emr.navigateTo({ applicationUrl: (url.indexOf("/") != 0 ? '/' : '') + url });
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

            $scope.print = function (closeAfterPrint) {

                if (!$scope.allExpanded) {
                    $scope.printButtonDisabled = true;

                    $scope.expandAll();

                    // wait on digest cycle and for all templates to be loaded (ie, wait for all sections to be fully expanded
                    // see: http://tech.endeepak.com/blog/2014/05/03/waiting-for-angularjs-digest-cycle/
                   var print = function () {
                        if ($http.pendingRequests.length > 0 || !$scope.allExpanded) {
                            $timeout(print); // Wait for all templates to be loaded
                        } else {
                            window.print();
                            $scope.printButtonDisabled = false;
                            if (closeAfterPrint) {
                                window.close();
                            }
                        }
                    }

                    $timeout(print);
                }
                else {
                    window.print();
                    if (closeAfterPrint) {
                        window.close();
                    }
                }
            }

            $scope.printAndClose = function () {
                $scope.print(true);
            }

            $scope.back = function() {
                window.history.back();
            };

            $scope.returnToEncounterList = function() {
              const encounterListUrl = "/pihcore/patient/encounterList.page?patientId={{patient}}";
              const url = Handlebars.compile(encounterListUrl)({
                patient: $scope.visit.patient.uuid,
              });
              emr.navigateTo({applicationUrl: url});
            };
        }]);

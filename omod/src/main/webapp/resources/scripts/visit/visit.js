angular.module("visit", [ "filters", "constants", "encounterTypeConfig", "visitService", "encounterService", "obsService",
    "allergies", "vaccinations", "ui.bootstrap", "ui.router", "session", "ngDialog", "appFramework",
    "configService", 'pascalprecht.translate'])

    .config(function ($stateProvider, $urlRouterProvider, $translateProvider) {

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
            .state("print", {
                url: "/print",
                templateUrl: "templates/print.page"
            })

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

    .directive("encounter", [ "Encounter", "Concepts", "EncounterRoles", "DatetimeFormats", "SessionInfo", "EncounterTypeConfig", "$http", "$sce",
        function(Encounter, Concepts, EncounterRoles, DatetimeFormats, SessionInfo, EncounterTypeConfig, $http, $sce) {
            return {
                restrict: "E",
                scope: {
                    encounter: "=",
                    visit: "=",
                    selected: '=',
                    encounterDateFormat: "=",
                    expandOnLoad: "="
                },
                controller: ["$scope", function($scope) {

                    var config = EncounterTypeConfig[$scope.encounter.encounterType.uuid];

                    $scope.DatetimeFormats = DatetimeFormats;
                    $scope.Concepts = Concepts;
                    $scope.EncounterRoles = EncounterRoles;
                    $scope.session = SessionInfo.get();
                    $scope.encounterState = $scope.selected ? "long" : (config ? config.defaultState : "short");
                    $scope.template = config ? config[$scope.encounterState + "Template"] :
                        ($scope.encounterState == 'long' ? "templates/encounters/defaultEncounterLong.page" : "templates/encounters/defaultEncounterShort.page");

                    $scope.icon = config ? config.icon : null;
                    $scope.primaryEncounterRoleUuid = config ? config.primaryEncounterRoleUuid : null;
                    $scope.sections = config && config.sections ? config.sections : [];
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

    .directive("encounterSection", [ "Concepts", "DatetimeFormats", "SessionInfo", "$http", "$sce", "$timeout",
        function(Concepts, DatetimeFormats, SessionInfo, $http, $sce, $timeout) {
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
                            returnUrl: window.encodeURIComponent(window.location.pathname + "?visit=" + $scope.visit.uuid + "&encounter=" + $scope.encounter.uuid)
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
                            return (encounter.canBeEditedBy(currentUser)
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
    .directive("visitDetails", [ "Visit", "ngDialog", "$filter", function(Visit, ngDialog, $filter) {
        // TODO make sure this at least gives as error message in case of failure
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
                            $dialogScope.newStartDatetime = new Date($filter('serverDate')($scope.visit.startDatetime));
                            $dialogScope.startDateLowerLimit = $scope.previousVisitEndDatetime($scope.visit);
                            $dialogScope.startDateUpperLimit = $scope.firstEncounterInVisitDatetime($scope.visit);
                            $dialogScope.endDateLowerLimit = $scope.mostRecentEncounterInVisitDatetime($scope.visit);
                            $dialogScope.endDateUpperLimit = $scope.nextVisitStartDatetime($scope.visit);
                            $dialogScope.newStopDatetime = $scope.visit.stopDatetime ? new Date($filter('serverDate')($scope.visit.stopDatetime)) : '';
                            $dialogScope.newLocation = $scope.visit.location;
                        }],
                        template: "templates/visitDetailsEdit.page"
                    }).then(function(opts) {
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

    .controller("VisitController", [ "$scope", "$rootScope", "$translate","$http", "Visit", "$state",
        "$timeout", "$filter", "ngDialog", "Encounter", "EncounterTypeConfig", "AppFrameworkService",
        "visitUuid", "patientUuid", "encounterUuid", "locale", "currentSection", "DatetimeFormats", "EncounterTransaction", "SessionInfo", "Concepts",
        function($scope, $rootScope, $translate, $http, Visit, $state, $timeout, $filter,
                 ngDialog, Encounter, EncounterTypeConfig, AppFrameworkService, visitUuid, patientUuid, encounterUuid,
                 locale, currentSection, DatetimeFormats, EncounterTransaction, SessionInfo, Concepts) {

            // if we've got a "currentSection", it means we are in the "Next" workflow and should immediately redirect to the next section
            if (currentSection && encounterUuid) {
                goToNextSection(currentSection, patientUuid, encounterUuid, visitUuid);
            }
            else {
                // otherwise do standard loading
               loadPage();
            }

            function loadPage(state) {

                $rootScope.DatetimeFormats = DatetimeFormats;
                $scope.Concepts = Concepts;

                $scope.session = SessionInfo.get();

                $scope.visitUuid = visitUuid;
                $scope.patientUuid = patientUuid;
                $scope.encounterUuid = encounterUuid;

                $scope.printButtonDisabled = false;
                $scope.allExpanded = false;

                $translate.use(locale);

                loadVisits(patientUuid);
                loadVisit(visitUuid);
                switchToState(state);

            }

            function goToNextSection(currentSection, patientUuid, encounterUuid, visitUuid) {

                // fetch the encounter so we can determine the encounter type, which we need to determine where to redirect to
                // TODO error handling, what if not
                Encounter.get({ uuid: encounterUuid }).
                    $promise.then(function(encounter) {
                        var sections = EncounterTypeConfig[encounter.encounterType.uuid].sections;

                        var redirectToSectionIdx = 0;

                        // if current section is the "encounter-info" section, we just want to jump to first "real" section (idx = 0)
                        // otherwise find the current section and add one
                        if (currentSection != "encounter-info") {
                            i = 0;
                            while (i < sections.length && sections[i].id != currentSection) {
                                i++;
                            }
                            redirectToSectionIdx = i + 1;
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
                                returnUrl: window.encodeURIComponent(window.location.pathname + "?visit=" + visitUuid + "&encounter=" + encounterUuid)
                            });

                            emr.navigateTo({ applicationUrl: (url.indexOf("/") != 0 ? '/' : '') + url });
                        }
                        else {
                            // if we don't find an editurl, just open the page, passing in the id of the next section
                            if (i + 1 < sections.length) {
                                loadPage(sections[i + 1].id);
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

            function loadVisits(patientUuid) {
                Visit.get({patient: $scope.patientUuid, v: "custom:(uuid,startDatetime,stopDatetime)"}).$promise.then(function(response) {
                    $scope.visits = response.results;
                });
            }

            function loadVisit(visitUuid) {
                if (visitUuid) {
                    Visit.get({
                            uuid: visitUuid,
                            v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:(uuid,display,encounterDatetime,patient:default,location:ref,form:ref,encounterType:ref,obs:default,orders:ref,voided,visit:ref,encounterProviders,creator:ref),patient:default,visitType:ref,attributes:default)"
                        })
                        .$promise.then(function (visit) {
                            visit.encounters = _.reject(visit.encounters, function (it) {
                                return it.voided;
                            });
                            $scope.visit = new OpenMRS.VisitModel(visit);
                            $scope.visitIdx = $scope.getVisitIdx(visit);
                            $scope.encounterDateFormat = sameDate($scope.visit.startDatetime, $scope.visit.stopDatetime) ? "hh:mm a" : "hh:mm a (d-MMM)";

                            AppFrameworkService.getUserExtensionsFor("patientDashboard.visitActions").then(function (ext) {
                                $scope.visitActions = ext;
                            })
                    });
                }
            }

            $rootScope.$on("request-view-encounter", function(event, encounter) {
                var config = EncounterTypeConfig[encounter.encounterType.uuid];
                if (config.viewUrl) {
                    var url = Handlebars.compile(config.viewUrl)({
                        patient: encounter.patient,
                        visit: $scope.visit,
                        encounter: encounter,
                        breadcrumbOverride: encodeURIComponent(JSON.stringify(breadcrumbOverride)),
                        returnUrl: "/" + OPENMRS_CONTEXT_PATH + "/pihcore/visit/visit.page?visit=" + $scope.visit.uuid
                    });
                    emr.navigateTo({applicationUrl: url});
                }
            });

            $rootScope.$on("request-edit-encounter", function(event, encounter) {
                var config = EncounterTypeConfig[encounter.encounterType.uuid];
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
                        v: "custom:(uuid,startDatetime,stopDatetime,location:ref,encounters:(uuid,display,encounterDatetime,location:ref,encounterType:ref,obs:default,voided),attributes:default)"
                    }).$promise.then(function (response) {
                        _.each(response.results, function (visit) {
                            visit.encounters =  _.reject(visit.encounters, function(it) {
                                return it.voided;
                            });
                        })
                        $scope.visits = response.results;
                    });
                }
            });

            $scope.$on('visit-changed', function(event, visit) {
                if ($scope.visitUuid == visit.uuid) {
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
                        var previousVisitEndDate = new Date($filter('serverDate')($scope.visits[$scope.visitIdx + 1].stopDatetime));
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
                    return new Date($filter('serverDate')(visit.encounters[visit.encounters.length -1].encounterDatetime));
                }
                else {
                    return null;
                }
            }

            $scope.mostRecentEncounterInVisitDatetime = function(visit) {
                if (visit.encounters != null && visit.encounters.length > 0) {
                    // the visit cannot end before the date of the newest encounter that is part of this visit
                    return new Date($filter('serverDate')(visit.encounters[0].encounterDatetime));
                }
                else {
                    return null;
                }

            }

            $scope.nextVisitStartDatetime = function(visit) {
                if ( $scope.visitIdx != -1) {
                    if ($scope.visitIdx  == 0) {
                        //this is the newest visit in the list and the upper date limit is today
                        return new Date();  // TODO technical is should be "today" in the server's time
                    } else {
                        var nextVisitStartDate = new Date($filter('serverDate')($scope.visits[$scope.visitIdx -1].startDatetime));
                        // return the day before the start date of the next visit in the list
                        nextVisitStartDate.setDate(nextVisitStartDate.getDate() -1);
                        return nextVisitStartDate;
                    }
                }
                return null;
            }

            $scope.goToNextSection = function(currentSection) {
                goToNextSection(currentSection, $scope.patientUuid, $scope.encounterUuid, $scope.visitUuid)
            }

            $scope.goToVisit = function(visit) {
                if (visit) {
                    $scope.visitUuid = visit.uuid;
                }
                $state.go("overview");
            }

            $scope.goToVisitList = function() {
                $state.go("visitList");
            }

           // TODO figure out if we can get rid of this function
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

                    // if return hasn't been specified as a template in visitAction.url, make sure we append it
                    if (url.indexOf('returnUrl') == -1) {
                        url = url + "&returnUrl=" + returnUrl;
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

        }]);
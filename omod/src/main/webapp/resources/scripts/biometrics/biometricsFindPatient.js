

jq(function() {

    var SEARCH_URL = '/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/patient';
    var CUSTOM_PATIENT_REPRESENTATION = 'custom:(uuid,patientIdentifier:(uuid,identifier),person:(gender,age,birthdate,birthdateEstimated,personName))';
    var IDENTIFIER_SEARCH_NAME = 'patientByIdentifier';
    // TODO add breadcrumbs? returnUrl?
    var SUMMARY_PAGE_LINK = '/' + OPENMRS_CONTEXT_PATH + '/registrationapp/registrationSummary.page?patientId=PATIENT_UUID';

    var matchedIds = [];
    var results = [];
    var enabled = false;
    var ready = false;
    var dataTable;

    // clear out any previous value on page load
    $.getJSON('http://localhost:8090/clearLastSearch', {})
        .success(function (response) {
            enabled = true;
            ready = true;
        })
        .error(function (xhr, status, err) {
            jq('#biometrics-find-patient').hide();
            enabled = false;
            ready = true;
        });

    // set up datatable
    dataTable = jq('#biometrics-results').dataTable({
        bFilter: false,
        bJQueryUI: true,
        bLengthChange: false,
        iDisplayLength: 15,
        sPaginationType: "full_numbers",
        bSort: false,
        sDom: 't<"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg"ip>',
        oLanguage: {
            // TODO add these
           /* "sInfo": config.messages.info,
            "sInfoEmpty": " ",
            "sZeroRecords": config.messages.noMatchesFound,
            "oPaginate": {
                "sFirst": config.messages.first,
                "sPrevious": config.messages.previous,
                "sNext": config.messages.next,
                "sLast": config.messages.last
            } */
        },
    });

    // poll client app at regular intervals
    var interval = setInterval(function() {
        if (ready) {

            if (!enabled) {
                clearInterval(interval);
            }

            $.getJSON('http://localhost:8090/getLastSearch', {})
                .success(function (response) {

                    // TODO handle connection failure?
                    if (response.result == "success") {

                        var newMatchedIds = [];

                        _.each(response.uuids, function (uuid) {
                            newMatchedIds.push(uuid);
                        })

                        // see if the matches have changed
                        if (matchedIds.length !== newMatchedIds.length) {
                            updateResults(newMatchedIds);
                        }
                        for (var i = 0; i < matchedIds.length; i++) {
                            if (matchedIds[i] !== newMatchedIds[i]) {
                                updateResults(newMatchedIds);
                            }
                        }
                    }
                })
                .error(function (xhr, status, err) {
                    // TODO error handling
                });

        }
    }, 2000);

    function updateResults(newMatchedIds) {
        matchedIds = newMatchedIds;
        results = [];
        dataTable.fnClearTable();
        jq('#biometrics-find-patient').show();
        _.each(matchedIds, function(id) {
            $.getJSON(SEARCH_URL, { identifier: id, v: CUSTOM_PATIENT_REPRESENTATION, s: IDENTIFIER_SEARCH_NAME })
                .success(function (response) {
                    if (response.results[0]) {
                        addRow(response.results[0]);
                    }
                })
                .error(function (xhr, status, err) {
                    // TODO error handling
                });

        })
    }

    function addRow(patient) {
        var birthdate = formatBirthdate(patient);
        var name = formatName(patient);
        dataTable.fnAddData([patient.patientIdentifier.identifier, name,
            patient.person.gender, patient.person.age, birthdate]);
    }

    function formatBirthdate(patient) {
        var birthdate = '';
        if(patient.person.birthdate){
            birthdate = moment(patient.person.birthdate).format('DD MMM YYYY');
            if( patient.person.birthdateEstimated ){
                birthdate = "~ "+birthdate;
            }else{
                birthdate = "&nbsp;&nbsp; "+birthdate;
            }
        }
        return birthdate;
    }

    function formatName(patient) {
        return '<a href="' + SUMMARY_PAGE_LINK.replace("PATIENT_UUID", patient.uuid) + '">' +
                patient.person.personName.display + '</a>';
    }

});
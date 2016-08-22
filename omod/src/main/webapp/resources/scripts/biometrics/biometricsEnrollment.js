

jq(function() {

    // clear out any previous value on page load
    // TODO should this really happen when the fingerprint section is loaded?
    $.getJSON('http://localhost:8090/clearLastEnrolled', {})
        .success(function (response) {
            // TODO some sort of vertification here?
        })
        .error(function (xhr, status, err) {
            // TODO this should dyanamically chose the fieldset defined in the app config?
            // TODO what if we aren't using the simple form navigator
            NavigatorController.getQuestionById('biometrics-fieldset').hide();
        });


    // handles polling for the last enrolled
    NavigatorController.getFieldById('biometrics-enrollment').element.focus(function() {

        var interval  = setInterval(function() {
            $.getJSON('http://localhost:8090/getLastEnrolled', {})
                .success(function (response) {

                    // TODO handle connection failure?

                    if (response.result == "success") {
                        jq('#biometrics-enrollment').val(response.uuid);
                        NavigatorController.stepForward();
                        clearInterval(interval);
                    }
                })
                .error(function (xhr, status, err) {
                    // TODO error handling
                });
        }, 1000);

    });




});
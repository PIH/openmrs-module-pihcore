$(function() {
    disableSubmitButton();

    $("#termsAcceptCheckbox-field").click(function() {
        if ($(this).is(':checked')) {
            enableSubmitButton();
        } else {
            disableSubmitButton();
        }
    });

    function disableSubmitButton(){
        $("#save-button").addClass("disabled").attr("disabled", "disabled");
    }

    function enableSubmitButton(){
        $("#save-button").removeClass("disabled").removeAttr("disabled");
    }
});
$(function() {

    const PASSWORD_LENGTH = 8;

    disableSubmitButton();

    var timer;

    $("#oldPassword").blur(function(){
       var oldPassword = $(this).val();

        if (!isPasswordValid(oldPassword)){
            $("#oldPasswordSection .field-error").text(errorMessageOldPassword);
            $("#oldPasswordSection .field-error").show();
            disableSubmitButton();
            return false;
        } else {
            $("#oldPasswordSection .field-error").hide();
        }
    });

    $("#newPassword").blur(function(){
        var newPassword = $(this).val();

        if (!isPasswordValid(newPassword)){
            $("#newPasswordSection .field-error").text(errorMessageNewPassword);
            $("#newPasswordSection .field-error").show();
            disableSubmitButton();
            return false;
        } else {
            $("#newPasswordSection .field-error").hide();
        }
    });

    $("#confirmPassword").keyup(function(){
        if (timer) {
            clearTimeout(timer);
        }
        timer = setTimeout(confirmPasswordAction, 500);
    });

    function confirmPasswordAction() {
        var newPassword = $("#newPassword").val();
        var confirmPassword = $("#confirmPassword").val();

        if (confirmPassword.length >= 1 && (newPassword != confirmPassword)) {
            $("#confirmPasswordSection .field-error").text(errorMessageNewAndConfirmPassword);
            $("#confirmPasswordSection .field-error").show();
            disableSubmitButton();
        } else if (isPasswordValid(newPassword) && newPassword == confirmPassword) {
            $("#confirmPasswordSection .field-error").hide();
            enableSubmitButton();
        }
    }
    function isPasswordValid(newPassword) {
        return newPassword && newPassword.length >= PASSWORD_LENGTH;
    }

    function disableSubmitButton(){
        $("#save-button").addClass("disabled");
        $("#save-button").attr("disabled", "disabled");
    }

    function enableSubmitButton(){
        $("#save-button").removeClass("disabled");
        $("#save-button").removeAttr("disabled");
    }
});
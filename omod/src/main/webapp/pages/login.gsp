<!-- This is the login page -->
<%
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("pihcore", "login.css")
    ui.includeJavascript("pihcore", "account/login.js")
%>
<!DOCTYPE html>
<html>
    <head>
        <title>${ welcomeMessage }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
        ${ ui.resourceLinks() }
        <script src="/${ui.contextPath()}/csrfguard" type="text/javascript"></script>
    </head>
    <body>
        <script type="text/javascript">
            var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
        </script>

        ${ ui.includeFragment("appui", "header") }

        <div id="body-wrapper" class="container">
            <div id="login-page">

                <form id="login-form" method="post" autocomplete="off">

                    <h1>${ welcomeMessage }</h1>

                    <fieldset>

                        <legend>
                            <i class="icon-lock small"></i>
                            ${ ui.message("mirebalais.login.loginHeading") }
                        </legend>

                        ${ ui.includeFragment("pihcore", "browserWarning") }
                        ${ ui.includeFragment("uicommons", "infoAndErrorMessage") }

                        <% if (authenticationSession.getErrorMessage()) { %>
                            <div id="error-message" class="note-container">
                                <div class="note error">
                                    ${ ui.message(authenticationSession.getErrorMessage()) }
                                </div>
                            </div>
                        <% } %>

                        <p class="left">
                            <label for="username">
                                ${ ui.message("mirebalais.login.username") }:
                            </label>
                            <input id="username" type="text" name="username" placeholder="${ ui.message("mirebalais.login.username.placeholder") }"/>
                        </p>

                        <p class="left">
                            <label for="password">
                                ${ ui.message("mirebalais.login.password") }:
                            </label>
                            <input id="password" type="password" name="password" placeholder="${ ui.message("mirebalais.login.password.placeholder") }"/>
                        </p>

                        <p>
                            <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
                        </p>
                        <p>
                            <a id="cant-login" href="javascript:void(0)">
                                <i class="icon-question-sign small"></i>
                                ${ ui.message("mirebalais.login.cannotLogin") }
                            </a>
                        </p>

                    </fieldset>

                </form>

            </div>
        </div>

        <div id="cannot-login-popup" class="dialog" style="display: none">
            <div class="dialog-header">
                <i class="icon-info-sign"></i>
                <h3>${ ui.message("mirebalais.login.cannotLogin") }</h3>
            </div>
            <div class="dialog-content">
                <p class="dialog-instructions">${ ui.message("mirebalais.login.usernameOrEmail") }</p>
                <p id="password-reset-message" style="padding-bottom:10px; color:red;"></p>
                <p style="padding-bottom: 20px;">
                    <input type="text" id="password-reset-username" size="35" autocomplete="off" data-lpignore="true"/>
                </p>
                <button class="cancel">${ ui.message("emr.cancel") }</button>
                <button class="confirm">${ ui.message("mirebalais.login.requestPasswordReset") }</button>
            </div>
        </div>


        <script type="text/javascript">
            document.getElementById('username').focus();

            jq(function() {
                var cannotLoginController = emr.setupConfirmationDialog({
                    selector: '#cannot-login-popup',
                    actions: {
                        confirm: function() {
                            const username = jq("#password-reset-username").val();
                            if (!isUsernameValid(username)) {
                                jq("#password-reset-message").html('${ ui.escapeJs(ui.encodeHtmlContent(ui.message("mirebalais.login.error.invalidUsername"))) }');
                            }
                            else {
                                jq("#password-reset-message").html('');
                                jq.post(emr.fragmentActionLink("pihcore", "account/resetPassword", "reset", { "username": username }));
                                emr.successMessage('${ ui.escapeJs(ui.encodeHtmlContent(ui.message("mirebalais.login.requestPasswordResponse"))) }');
                                cannotLoginController.close();
                                jq("#password-reset-username").val("");
                            }
                        }
                    }
                });
                jq('a#cant-login').click(function() {
                    cannotLoginController.show();
                })
            });
        </script>
    </body>
</html>

<!-- This is the login page -->
<%
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("pihcore", "login.css")
%>

<!DOCTYPE html>
<html>
    <head>
        <title>${ welcomeMessage }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
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

                    <h1>${ ui.message("authentication.totp.loginInstructions") }</h1>

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
                            <label for="code-input">${ ui.message("authentication.totp.code") }</label>
                            <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authentication.totp.code.placeholder") }"/>
                        </p>

                        <p>
                            <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
                        </p>

                    </fieldset>

                </form>

            </div>
        </div>
        <script type="text/javascript">
            document.getElementById('answer').focus();
        </script>
    </body>
</html>

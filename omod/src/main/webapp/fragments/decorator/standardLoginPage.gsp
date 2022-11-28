<%
    ui.includeFragment("appui", "standardEmrIncludes")
%>
<!DOCTYPE html>
<html>
    <head>
        <title>${ config.title ?: ui.message("pihcore.login.title") }</title>
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

            ${ ui.includeFragment("uicommons", "infoAndErrorMessage") }

            <% if (authenticationSession.getErrorMessage()) { %>
                <div id="error-message" class="note-container">
                    <div class="note error">
                        ${ ui.message(authenticationSession.getErrorMessage()) }
                    </div>
                </div>
            <% } %>

            <div id="content" class="container-fluid">
                <%= config.content %>
            </div>

        </div>
    </body>

</html>

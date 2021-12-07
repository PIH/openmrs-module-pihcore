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


                <!-- only show location selector if there are multiple locations to choose from -->
                <% if (locations.size > 1) { %>
                    <p class="clear">
                        <label for="sessionLocation">
                            ${ ui.message("mirebalais.login.sessionLocation") }:
                        </label>
                        <ul id="sessionLocation" class="select">
                            <% locations.sort { ui.format(it) }.each { %>
                                <li class="location-list-item" value="${it.id}">${ui.format(it)}</li>
                            <% } %>
                        </ul>
                    </p>
                <% } %>

                <input type="hidden" id="sessionLocationInput" name="sessionLocation"
                    <% if (locations.size == 1) { %>
                       value="${locations[0].id}"
                    <% } %>
                    <% if (lastSessionLocation != null) { %>
                    value="${lastSessionLocation.id}"
                    <% } %>
                />

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
        <p class="dialog-instructions">${ ui.message("mirebalais.login.cannotLoginInstructions") }</p>

        <button class="confirm">${ ui.message("coreapps.okay") }</button>
    </div>
</div>


<script type="text/javascript">
	document.getElementById('username').focus();

    updateSelectedOption = function() {
        jq('#sessionLocation li').removeClass('selected');
        var sessionLocationVal = jq('#sessionLocationInput').val();

        if(parseInt(sessionLocationVal, 10) > 0){
            jq('#sessionLocation li[value|=' + sessionLocationVal + ']').addClass('selected');
            jq('#login-button').removeClass('disabled');
            jq('#login-button').removeAttr('disabled');
        }else{
            jq('#login-button').addClass('disabled');
            jq('#login-button').attr('disabled','disabled');
        }
    };

    jq(function() {
        updateSelectedOption();

        jq('#sessionLocation li').click( function() {
            jq('#sessionLocationInput').val(jq(this).attr("value"));
            updateSelectedOption();
        });

        var cannotLoginController = emr.setupConfirmationDialog({
            selector: '#cannot-login-popup',
            actions: {
                confirm: function() {
                    cannotLoginController.close();
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

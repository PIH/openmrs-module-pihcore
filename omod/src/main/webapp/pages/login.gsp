<%
    ui.decorateWith("pihcore", "standardLoginPage", [ title: welcomeMessage ])
%>
<style>
    .location-list-item {
        border-top: 1px solid #EFEFEF;
        border-bottom: 0 !important;
        vertical-align: top;
    }
</style>

${ ui.includeFragment("pihcore", "browserWarning") }

<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ welcomeMessage }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("mirebalais.login.loginHeading") }
            </legend>

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
    jq( document ).ready(function() {
        jq('#username').focus();

        updateSelectedOption = function() {
            jq('#sessionLocation li').removeClass('selected');
            var sessionLocationVal = jq('#sessionLocationInput').val();

        if (jq('#sessionLocation li').size() === 0) {
            jq('#login-button').removeClass('disabled');
            jq('#login-button').removeAttr('disabled');
        }
        else if(parseInt(sessionLocationVal, 10) > 0) {
                jq('#sessionLocation li[value|=' + sessionLocationVal + ']').addClass('selected');
                jq('#login-button').removeClass('disabled');
                jq('#login-button').removeAttr('disabled');
            }else{
                jq('#login-button').addClass('disabled');
                jq('#login-button').attr('disabled','disabled');
            }
        };

        isUsernameValid = function(username) {
            return (username && username.length !== 0 && username.indexOf(' ') < 0);
        }

        updateSelectedOption();

        jq('#sessionLocation li').click( function() {
            jq('#sessionLocationInput').val(jq(this).attr("value"));
            updateSelectedOption();
        });

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
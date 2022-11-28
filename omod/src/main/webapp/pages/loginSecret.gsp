<%
    ui.decorateWith("pihcore", "standardLoginPage", [ title: ui.message("authentication.2fa.title") ])
%>
<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message("mirebalais.login.secret.message") }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("mirebalais.login.loginHeading") }
            </legend>

            <p>
                <label for="answer">
                    <input type="hidden" name="question" value="${question}"/>
                    ${ ui.message(question) }:
                </label>
                <input id="answer" type="password" name="answer" placeholder="${ ui.message("mirebalais.login.secret.placeholder") }"/>
            </p>

            <p>
                <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
            </p>

        </fieldset>

    </form>

</div>

<script type="text/javascript">
    document.getElementById('answer').focus();
</script>

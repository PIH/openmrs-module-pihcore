<%
    ui.decorateWith("pihcore", "standardLoginPage", [ title: ui.message("authentication.2fa.title") ])
%>
<div id="login-page">

    <form id="login-form" method="post" autocomplete="off">

        <h1>${ ui.message("authentication.totp.loginInstructions") }</h1>

        <fieldset>

            <legend>
                <i class="icon-lock small"></i>
                ${ ui.message("mirebalais.login.loginHeading") }
            </legend>

            <p>
                <label for="code-input">${ ui.message("authentication.totp.code") }</label>
                <input id="code-input" type="text" name="code" value="" placeholder="${ ui.message("authentication.totp.code.placeholder") }"/>
            </p>

            <p>
                <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
            </p>

        </fieldset>

    </form>

</div>
<script type="text/javascript">
    document.getElementById('code-input').focus();
</script>

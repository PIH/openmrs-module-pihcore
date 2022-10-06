<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<form id="login-form" method="post" autocomplete="off">

    <h1>${ ui.message("mirebalais.login.location.message") }</h1>

    <fieldset>

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

    </fieldset>

</form>

<script type="text/javascript">

    document.getElementById('login-button').focus();

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
    });
</script>


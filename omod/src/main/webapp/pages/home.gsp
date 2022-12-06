<%
	ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("pihcore", "home.css")

    def htmlSafeId = { extension ->
        "${ extension.id.replace(".", "-") }-app"
    }
%>


<script type="text/javascript">
    jq(function() {
        jq('#patient-search').focus();

        // make sure we reload the page if the location is changes; this custom event is emitted by by the location selector in the header
        jq(document).on('sessionLocationChanged', function() {
            window.location.reload();
        });
    });
</script>

<% if (showTwoFactorAlert) { %>

    <style>
        .two-factor-setup-link {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <div class="note-container">
        <div class="note warning" style="width: 100%;">
            <div class="text">
                <i class="fas fa-fw fa-lock" style="vertical-align: middle;"></i>
                <a class="two-factor-setup-link" href="${ ui.pageLink("authenticationui", "account/twoFactorSetup") }">${ ui.message("authentication.2fa.enableMessage") }</a>
            </div>
        </div>
    </div>

<% } %>

<div id="home-container">

    <% if (sessionContext.currentUser.hasPrivilege(privilegeSearchForPatients)) { %>

        ${ ui.message("mirebalais.searchPatientHeading") }
        ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget", [
            afterSelectedUrl: dashboardUrl,
            showLastViewedPatients: 'false',
            "columnConfig": findPatientColumnConfig
        ])}
    <% } %>


    <div id="apps">
        <% extensions.each { extension -> %>

            <a id="${ htmlSafeId(extension) }" href="/${ contextPath }/${ extension.url }" class="button app big">
                <% if (extension.icon) { %>
                    <i class="${ extension.icon }"></i>
                <% } %>
                ${ ui.message(extension.label) }
            </a>

        <% } %>
    </div>

</div>

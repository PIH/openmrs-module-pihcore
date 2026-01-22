<% if (sessionContext.currentUser.hasPrivilege(privilegeSearchForPatients)) { %>
    <script type="text/javascript">
        jq(function() {
            jq('#patient-search').focus();
        });
    </script>
    ${ ui.message("mirebalais.searchPatientHeading") }
    ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget", [
            afterSelectedUrl: dashboardUrl,
            showLastViewedPatients: 'false',
            "columnConfig": findPatientColumnConfig
    ])}
<% } %>
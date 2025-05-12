<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "${ ui.message("pih.app.admin.rolesandprivileges.view") }", link: "${ ui.pageLink("pihcore", "admin/rolesAndPrivileges") }" }
    ];

    jq(document).ready(function() {
        jq('#rolesList li > ul').hide(); // Hide all sub-lists initially
        jq('#rolesList li').click(function(event) {
            event.stopPropagation();
            jq(this).children('ul').slideToggle();
        });
    });

</script>

<fieldset>
    <div>
        <p>
            <strong>${ ui.message("emr.user.Capabilities") }</strong> (${ ui.message("emr.user.view.Privileges") })
        </p>
        <ol id="rolesList" style="padding-left:20px;">
            <% capabilities.sort { ui.format(it).toLowerCase() }.each { %>
            <li style="cursor: pointer; "><u>${ ui.format(it) }</u>
                    <ul style="padding-left:20px; list-style-type: disc;">
                        <% it.privileges.sort { ui.format(it).toLowerCase() }.each { %>
                            <li style="cursor: default; ">${ ui.format(it) }</li>
                        <% } %>
                    </ul>
                </li>
            <% } %>
        </ol>
    </div>

</fieldset>

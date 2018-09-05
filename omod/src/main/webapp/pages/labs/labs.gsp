<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("pih.app.labs.label") }"}
    ];
</script>

<div id="apps">

    <% if (context.hasPrivilege("App: labs")) { %>
        
        <!-- TODO: dynamically generate link based on context path, 
            is there a server-side way to do this? 
        -->
        <script type="text/javascript">
            document.write(
                '<a class="button app big" href="/' 
                    + OPENMRS_CONTEXT_PATH + '/owa/openmrs-owa-labworkflow/index.html'
                    + '">'
            );
        </script>
            <i class="icon-beaker"></i>
            ${ ui.message("pih.app.labtracking") }
        </a>
    <% } %>
</div>
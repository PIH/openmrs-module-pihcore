<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "Import Fingerprints", link: "${ ui.pageLink("pihcore", "admin/importFingerprints") }" }
    ];
</script>

<h3>Import Fingerprints</h3>
<div style="padding:10px;">
    <form method="post">
        <label for="fingerprintDbPath">Fingerprint database file path:</label>
        <input id="fingerprintDbPath" type="text" name="fingerprintDbPath" size="100" value="${ fingerprintDbPath }"/>
        <br/>

        <% if (existingRows) { %>
            Entries that already existed:  <%= existingRows.size() %><br/><br/>
        <% } %>
        <% if (successRows) { %>
            Entries that have been successfully saved:  <%= successRows.size() %><br/><br/>
        <% } %>
        <% if (errorRows) { %>
            <b>Errors</b><br/>
            <% for (errorRow in errorRows) { %>
                ${errorRow}<br/>
            <% } %>
        <% } %>

        <div style="display: table-cell; padding-left: 5px;">
            <label for="submit-button">&nbsp;</label>
            <input id="submit-button" type="submit" value="Import"/>
        </div>

    </form>
</div>
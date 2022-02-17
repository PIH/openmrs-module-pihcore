<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "Email Test", link: "${ ui.pageLink("pihcore", "admin/emailTest") }" }
    ];
</script>

<h3>Configuration</h3>
<% mailConfig.each{ setting -> %>
    <div style="display: table-row">
        <div style="display: table-cell; padding-right: 20px; font-weight: bold;">${ setting.key }:</div>
        <div style="display: table-cell">${ setting.key == "mail.password" ? "***********" : setting.value }</div>
    </div>
<% } %>
<br/>
<h3>Send a Test Email</h3>
<div style="padding:10px;">
    <form method="post">
        <label for="sender">From:</label>
        <input id="sender" type="text" name="sender" size="50" value="${ sender }"/>
        <br/>
        <label for="recipients">To:</label>
        <input id="recipients" type="text" name="recipients" size="50" value="${ recipients }"/>
        <br/>
        <label for="subject">Subject:</label>
        <input id="subject" type="text" name="subject" size="80" value="${ subject }"/>
        <br/>
        <label for="message">Message:</label>
        <textarea id="message" name="message" cols="80" rows="10">${ message }</textarea>
        <br/>
        <div style="display: table-cell; padding-left: 5px;">
            <label for="submit-button">&nbsp;</label>
            <input id="submit-button" type="submit" value="Send Message"/>
        </div>
        <br/>
    </form>
</div>
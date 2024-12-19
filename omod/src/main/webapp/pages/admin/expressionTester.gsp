<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("pihcore", "codemirror/lib/codemirror.css")
    ui.includeCss("pihcore", "codemirror/addon/hint/show-hint.css")
    ui.includeCss("pihcore", "codemirror/theme/monokai.css")
    ui.includeJavascript("pihcore", "json-formatter.umd.js")
    ui.includeJavascript("pihcore", "codemirror/lib/codemirror.js")
    ui.includeJavascript("pihcore", "codemirror/mode/javascript/javascript.js")
    ui.includeJavascript("pihcore", "codemirror/addon/edit/matchbrackets.js")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.system.administration.label") }", link: "${ ui.pageLink("coreapps", "systemadministration/systemAdministration") }" },
        { label: "Extension Expression Tester", link: "${ ui.pageLink("pihcore", "admin/expressionTester") }" }
    ];
    jq(document).ready(function() {
        window.statusExpressionEditor = CodeMirror.fromTextArea(document.getElementById('require-expression'), {
            mode: 'text/javascript',
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            viewportMargin: Infinity,
            matchBrackets: true,
            autofocus: true,
            theme: "monokai",
            extraKeys: {"Ctrl-Space": "autocomplete"}
        });
        <% if (!expressionToTest) { %>
            jq("#extension-section").hide();
        <% } %>
    });
</script>

<div>
    <form>
        <br/>
        <label for="patient-selector">Patient: </label>
        <input id="patient-selector" name="patientId" type="text" size="20" value="${patientId}" placeholder="Enter Patient ID or UUID" autocomplete="off"/>
        <br/>
        <div style="display: table-cell">
            <label for="extension-point-selector">Extension Point: </label>
            <select id="extension-point-selector" name="extensionPoint">
                <option value=""></option>
                <% extensionPoints.each { ep -> %>
                    <option value="${ep}"<%= ep.equals(extensionPoint) ? "selected" : "" %>>${ep}</option>
                <% } %>
            </select>
        </div>
        <div style="display: table-cell; padding-left: 5px;">
            <label for="submit-button">&nbsp;</label>
            <input id="submit-button" type="submit" value="Get Extensions"/>
        </div>
        <br/>
    </form>
    <hr/>

    <% if (extensionPoint && !expressionToTest) {%>
        <table>
            <tr>
                <th>Extension</th><th>Passed</th><th>Errors</th>
            </tr>
            <% evaluationResults.each { e -> %>
                <tr>
                        <td>
                            <a href="${ui.pageLink("pihcore", "admin/expressionTester", [
                                    "patientId": patientId,
                                    "extensionPoint": extensionPoint,
                                    "extension": e.extension.id
                            ])}">
                                ${e.extension.id}
                            </a>
                        </td>
                        <td>${e.passed}</td>
                        <td>${e.exception ?: ""}</td>
                </tr>
            <% } %>
        </table>
    <% } %>

    <div id="extension-section">
        <h3>${extension}</h3>
        <div><a id="context-toggle" onclick="jq('#context-display').toggle();">View/Hide Require Expression Context</a></div>
        <br/>
        <pre id="context-display" style="font-size: 8px; display:none;">
            ${requireExpressionContext}
        </pre>
        <br/>
        <form method="post">
            <textarea id="require-expression" name="expressionToTest">${expressionToTest}</textarea>
            <br/>
            <input type="submit" value="Test"/>
        </form>
        <hr/>
        <div>
            <b>Result: ${expressionToTestResult}${expressionToTestException ? "; ERROR: " + expressionToTestException : ""}</b>
        </div>
    </div>
</div>
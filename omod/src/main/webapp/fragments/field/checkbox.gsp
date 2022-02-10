<%
    config.require("label")
    config.require("formFieldName")
%>

<p>
    <input type="checkbox" id="${ config.id }-field" name="${ config.formFieldName }" value="${ config.value }" <% if(config.checked){ %>checked='checked'<% } %> />
    <label for="${ config.id }-field">${ config.label }</label>
</p>
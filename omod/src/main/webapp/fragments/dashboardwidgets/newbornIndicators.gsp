<style>
    .red {
        color: red;
    }
</style>
<div class="info-section newborn-indicator ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% fields.each { concept, obj -> %>
            <div style="font-size: smaller;">
                <span style="font-family: 'OpenSansBold'";>${ ui.message(obj.label) }:</span>
                <% if (concept == 'mother') { %>
                    <span>${ obj.name }</span><br>
                        <span>&emsp;<a href="${ "/" + contextPath + "/" + obj.url }">${ obj.primaryIdentifier }</a>&nbsp;&nbsp;${ obj.inpatientLocation ? obj.inpatientLocation : obj.queueName }</span>
                <% } else { %>
                    <span class="${obj.css ? obj.css : ''}">${ ui.format(obj.obs) }</span>
                <% } %>
            </div>
        <% } %>
    </div>
</div>
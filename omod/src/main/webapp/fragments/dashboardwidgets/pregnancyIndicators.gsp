<style>
.red {
    color: red;
}
#delivery-type {
    display: contents;
}
</style>
<div class="info-section">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
    </div>
    <div class="info-body">
        <% fields?.each { concept, obj -> %>
            <div style="font-size: smaller;">
                <span style="font-family: 'OpenSansBold'";>${ concept == 'ancVisits' ? ('# ' + ui.message(obj.label)) : ui.message(obj.label) }:</span>
                        <% if (concept == 'deliveryType') { %>
                            <span id="delivery-type">
                                <% obj.obs.eachWithIndex { it, index -> %>
                                     ${ ui.format(it.valueCoded) } ${obj.obs.size() - 1 > index ? ", " : ""}
                             <% } %>
                            </span>
                        <% } else { %>
                            <span class="${obj.css ? obj.css : ''}"> ${ ui.format(obj.obs) }</span>
                        <% } %>
            </div>
        <% } %>
    </div>
</div>

<div class="info-section pennding-lab-orders-widget ${app.id}">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <% if (app.url) { %>
           <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.id ]) }" class="right">
                <i class="icon-share-alt edit-action" title="Edit"></i>
           </a>
       <% } %>
    </div>
     <% if (orders != null && orders.size() > 0) { %>
        <div class="info-body">
            <ul>
            <% orders.each { order -> %>
                <li>
                    <div style="display: block; overflow: hidden; padding-right: 5px; padding-bottom: 2px">
                        <span style="float: left">
                            ${ui.format(order.concept.getPreferredName(ui.locale))} - ${ ui.formatDatePretty(order.dateActivated) }
                        </span>
                    </div>
                </li>
            <% } %>
            </ul>
        </div>
    <% } %>
</div>

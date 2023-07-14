<div class="info-section lab-results-widget">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + "/" + detailsUrl, [ "patient.uuid": patient.uuid ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <div class="info-body">
        <ul>
        <% labResults.each{ labResult -> %>
            <li>
                <div style="display: block; overflow: hidden; padding-right: 5px; padding-bottom: 2px">
                    <span style="float: left">
                        ${ui.format(labResult.concept.getShortNameInLocale(ui.locale) != null ? labResult.concept.getShortNameInLocale(ui.locale) : labResult.concept.getPreferredName(ui.locale))}:&nbsp;
                    </span>
                    <span style="float: left">
                        <strong>${ ui.format(labResult) }</strong>
                    </span>
                    <span style="float: right; font-size: small; color: #939393">
                        ${ ui.formatDatePretty(labResult.obsDatetime) }
                    </span>
                </div>
            </li>
        <% } %>
        </ul>
    </div>
</div>

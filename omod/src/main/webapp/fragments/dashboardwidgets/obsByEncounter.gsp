<style>
    #obsTable {
        table-layout: auto;
        display: block;
        overflow-x: auto;
    }

    ::-webkit-scrollbar {
        -webkit-appearance: none;
        height: 5px;
    }

    ::-webkit-scrollbar-thumb {
        border-radius: 4px;
        background-color: rgba(0, 0, 0, .5);
        -webkit-box-shadow: 0 0 1px rgba(255, 255, 255, .5);
    }

</style>

<div class="info-section encounters-by-type-widget">
    <div class="info-header">
        <i class="${app.icon}"></i>
        <h3>${ ui.message(app.label) }</h3>
        <a href="${ ui.urlBind("/" + contextPath + app.url, [ "patient.uuid": patient.id ]) }" class="right">
            <i class="icon-share-alt edit-action" title="Edit"></i>
        </a>
    </div>
    <% if ( encounterObs !=null && (encounterObs.size() > 0 ))  { %>
        <div class="info-body">
            <table id="obsTable">
                <thead>
                <tr>
                    <% headers.each { header -> %>
                        <th>${ui.message(header)}</th>
                    <% } %>
                </tr>
                </thead>
                <tbody>
                <% encounterObs.each { enc, obsList -> %>
                <tr>
                    <td>
                        <span class="encounter-date">
                            <a class="visit-link" href="${ ui.urlBind("/" + contextPath + visitUrl, [ "patient.uuid": patient.id, "visit.uuid": enc.visit.uuid ]) }">
                                <% if (showTime) { %>
                                    ${ui.formatDatetimePretty(enc.encounterDatetime)}
                                <% } else { %>
                                    ${ui.formatDatePretty(enc.encounterDatetime)}
                                <% } %>
                            </a>
                        </span>
                    </td>
                    <td>
                        <span>
                            <% obsList.eachWithIndex { obs, index -> %>
                                <% if (obs.valueNumeric) { %>
                                    <%  if ((minValue && obs.valueNumeric && (new BigDecimal(obs.valueNumeric) < new BigDecimal(minValue)))
                                            || (maxValue && obs.valueNumeric && (new BigDecimal(obs.valueNumeric) > new BigDecimal(maxValue)))) {  %>
                                        <span style="color: red">${ui.format(obs.valueNumeric)}</span>
                                    <% } else { %>
                                        ${ui.format(obs.valueNumeric)}
                                    <% }  %>
                                <% } else { %>
                                    ${ui.format(obs)}${obsList.size() - 1 > index ? ", " : ""}
                                <% } %>
                            <% } %>
                        </span>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</div>
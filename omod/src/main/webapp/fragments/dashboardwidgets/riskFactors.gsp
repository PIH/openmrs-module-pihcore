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
                    <th>${ ui.message("coreapps.date") } </th>
                    <th>${ ui.message("pihcore.riskFactors") }</th>
                </tr>
                </thead>
                <tbody>
                <% encounterObs.each { enc, obsList -> %>
                <tr>
                    <td>
                        <span class="encounter-date">
                            <a class="visit-link" href="${ ui.urlBind("/" + contextPath + visitUrl, [ "patient.uuid": patient.id, "visit.uuid": enc.visit.uuid ]) }">
                                ${ui.formatDatePretty(enc.encounterDatetime)}
                            </a>
                        </span>
                    </td>
                    <td>
                        <span>
                            <% obsList.each { obs -> %>
                                ${ui.format(obs)},
                            <%  }%>
                        </span>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</div>
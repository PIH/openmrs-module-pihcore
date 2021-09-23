<p id="${ config.id }">
    <label for="${ config.id }-field">${ ui.message(config.label) }</label>

    <select id="${ config.id }-field" name="${ config.formFieldName }">
        <% wards.each { %>
            <option value="${ it.id }">${ ui.format(it) }</option>
        <% } %>
    </select>
</p>
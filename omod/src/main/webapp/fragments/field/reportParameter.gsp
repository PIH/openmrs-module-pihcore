<%
    // Required Config:  parameter
    // Optional Config:  formFieldName, initialValue

    config.require("parameter")
    def parameter = config.parameter;
    def name = parameter.name;
    def labelOrName = parameter.labelOrName
    def type = parameter.type
    def collectionType = parameter.collectionType;
    def defaultValue = parameter.defaultValue
    def required = parameter.required
    def widgetConfiguration = parameter.widgetConfiguration

    def widgetType = type.simpleName.toLowerCase()
    def displayLabel = ui.message(labelOrName)
    def formFieldName = config.formFieldName ?: name
    def initialValue = config.initialValue ?: defaultValue ?: ''
    def classes = [(required == true ? "required" : "")]
%>

<% if (collectionType) { %>
    Collections are not yet supported
<% } else if (widgetConfiguration?.uiframeworkFragmentProvider) { %>
    ${ ui.includeFragment(widgetConfiguration.uiframeworkFragmentProvider, widgetConfiguration.uiframeworkFragment, [
            formFieldName: formFieldName,
            label: displayLabel,
            classes: classes
    ])}
<% } else if (widgetType == 'date') { %>
    ${ ui.includeFragment("uicommons", "field/datetimepicker", [
            formFieldName: formFieldName,
            label: displayLabel,
            useTime: false,
            defaultDate: initialValue,
    ])}
<% } else if (widgetType == 'location') { %>
    ${ ui.includeFragment("uicommons", "field/location", [
            formFieldName: formFieldName,
            label: displayLabel,
            initialValue: initialValue ?: sessionContext.sessionLocation,
            classes: ["drop-down-list " + (required == true ? "required" : "")]
    ])}
<% } else if (widgetType == 'encountertype') { %>
    ${ ui.includeFragment("uicommons", "field/encounterType", [
            formFieldName: formFieldName,
            label: displayLabel,
            initialValue: initialValue,
            classes: ["drop-down-list " + (required == true ? "required" : "")]
    ])}
<% } else if (widgetType == 'string' && widgetConfiguration?.optionValues) { %>
    <%
            def options = []
            widgetConfiguration.optionValues.split(",").eachWithIndex { optionValue, index ->
                options[index] = [label: optionValue, value: optionValue, selected: false]
            }
            if (widgetConfiguration?.optionLabels) {
                widgetConfiguration.optionLabels.split(",").eachWithIndex { optionLabel, index ->
                    options[index].label = ui.message(optionLabel)
                }
            }
    %>
    ${ ui.includeFragment("uicommons", "field/dropDown", [
            formFieldName: formFieldName,
            label: displayLabel,
            initialValue: initialValue,
            options: options,
            hideEmptyLabel: true,
            classes: ["drop-down-list " + (required == true ? "required" : "")]
    ])}
<% } else if (widgetType == "string" || type == "integer") { %>
    ${ ui.includeFragment("uicommons", "field/text", [
            formFieldName: "parameterValues[" + it.name + "]",
            label: paramLabel,
            initialValue: it.defaultValue,
            classes: ["drop-down-list " + (required == true ? "required" : "")]
    ])}
<% } else { %>
    Unknown parameter type: ${ type }
<% } %>
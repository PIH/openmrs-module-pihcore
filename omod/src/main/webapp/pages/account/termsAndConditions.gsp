<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihcore", "account/termsAndConditions.js")
%>

<style>
    ul, li {
        list-style: disc;
        padding-left: 20px;
        padding-bottom: 5px;
        padding-top: 5px;
    }
</style>

<h3>${ ui.message("pihcore.termsAndConditions") }</h3>

<form method="post" id="termsAndConditionsForm" autocomplete="off">
    <p>
        ${ ui.message("pihcore.termsAndConditionsText") }
    </p>
    <p>
        ${ ui.includeFragment("pihcore", "field/checkbox", [
                label: ui.message("pihcore.termsAndConditionsAccept"),
                id: "termsAcceptCheckbox",
                formFieldName: "termsAndConditionsAccepted",
                value: "true",
                checked: false
        ])}
    </p>
    <div>
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.continue") }" class="disabled" />
    </div>
</form>

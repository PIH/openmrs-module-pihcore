<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authentication.2fa.title") ])
%>

<style>
    .note {
        width: 100%;
    }
    #options-choices {
        padding: 20px
    }
    .option-info {
        padding-left: 10px;
        font-style: italic;
        font-size: smaller;
    }
    .config-page-link {
        text-decoration: underline;
        color: #007FFF;
    }
</style>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("pihcore", "account/myAccount")}' },
        { label: "${ ui.message("authentication.2fa.title")}" }
    ];

    jQuery(function() {
        jQuery("#save-button").addClass("disabled").attr("disabled", "disabled");
        jQuery('input[name="secondaryMethod"]').change(function() {
            var checkedVal = jQuery('input[name="secondaryMethod"]:checked').val()
            if (checkedVal === '${ existingOption }') {
                jQuery("#save-button").addClass("disabled").attr("disabled", "disabled");
            }
            else {
                jQuery("#save-button").removeClass("disabled").removeAttr("disabled");
            }
        });
    });

</script>

<h3>${ui.message("authentication.2fa.title")}</h3>
<div>
    <% if (twoFactorAvailable) { %>
        <div class="section note-container">
            <% if (existingOption) { %>
                <div class="note success">
                    ${ui.message("authentication.2fa.accountEnabled")}
                </div>
            <% } else { %>
                <div class="note error">
                    ${ui.message("authentication.2fa.accountNotEnabled")}
                </div>
            <% } %>
        </div>
        <div>
            <div class="section" id="options-title">
                ${ui.message("authentication.2fa.changeMethod")}
            </div>
            <form id="options-form" method="post">
                <div id="options-choices">
                    <div class="option-choice">
                        <input id="empty-option" type="radio" name="secondaryMethod" value="" <%= existingOption ? "" : "checked" %> />
                        <label for="empty-option">
                            ${ ui.message("authentication.2fa.noneSelected") }
                            <% if (!existingOption) { %>
                                <span class="option-info">( ${ui.message("authentication.2fa.currentlySelected")} )</span>
                            <% } %>
                        </label>
                    </div>
                    <% secondaryOptions.eachWithIndex { option, index ->
                        def schemeId = option.schemeId %>
                        <div class="option-choice">
                            <input id="option-${schemeId}" type="radio" name="secondaryMethod" value="${schemeId}" <%= option.currentlySelected ? "checked" : option.configurationRequired ? "disabled" : "" %> />
                            <label for="option-${schemeId}">
                                ${ ui.message("authentication." + schemeId + ".name") }
                                <% if (option.currentlySelected) { %>
                                    <span class="option-info">( ${ui.message("authentication.2fa.currentlySelected")} )</span>
                                <% } else if (option.configurationRequired) { %>
                                    <a class="config-page-link" href="${option.configurationPage}">
                                        <span class="option-info">${ ui.message("authentication." + schemeId + ".configurationRequiredMessage") }</span>
                                    </a>
                                <% } %>
                            </label>
                        </div>
                    <% } %>
                </div>
                <div>
                    <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/pihcore/account/myAccount.page'" />
                    <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
                </div>
            </form>
        </div>

    <% } else { %>
        <div class="section note-container">
            <div class="note error">
                <span class="text">${ ui.message("authentication.2fa.systemNotEnabled") }</span>
            </div>
        </div>
    <% } %>
</div>

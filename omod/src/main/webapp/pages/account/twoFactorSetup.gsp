<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("authentication.2fa.title") ])
    def returnUrl = isOwnAccount ? "myAccount.page" : "account.page?personId=" + userToSetup.person.personId;
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
    var breadcrumbs = [];
    breadcrumbs.push({ icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' });
    <% if (isOwnAccount) { %>
        breadcrumbs.push({ label: "${ ui.message("emr.app.system.administration.myAccount.label")}", link: '${ui.pageLink("pihcore", "account/myAccount")}' });
    <% } else { %>
        breadcrumbs.push({ label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' });
        breadcrumbs.push({ label: "${ ui.message("emr.task.accountManagement.label")}" , link: '${ui.pageLink("pihcore", "account/manageAccounts")}'});
        breadcrumbs.push({ label: "${ ui.format(userToSetup.person) }", link: '${ui.pageLink("pihcore", "account/account", [personId: userToSetup.person.personId])}' });
    <% } %>
    breadcrumbs.push({ label: "${ ui.message("authentication.2fa.title")}" });

    jQuery(function() {
        let nextButton = jQuery("#next-button");
        nextButton.addClass("disabled").attr("disabled", "disabled");
        jQuery('input[name="schemeId"]').change(function() {
            var checkedVal = jQuery('input[name="schemeId"]:checked').val()
            if (checkedVal === '${ existingOption }') {
                nextButton.addClass("disabled").attr("disabled", "disabled");
            }
            else {
                nextButton.removeClass("disabled").removeAttr("disabled");
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
                <% if (!isOwnAccount) { %>
                    <input type="hidden" name="userId" value="${userToSetup.userId}"/>
                <% } %>
                <div id="options-choices">
                    <div class="option-choice">
                        <input id="empty-option" type="radio" name="schemeId" value="" <%= existingOption ? "" : "checked" %> />
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
                            <input id="option-${schemeId}" type="radio" name="schemeId" value="${schemeId}" <%= option.currentlySelected ? "checked" : "" %> />
                            <label for="option-${schemeId}">
                                ${ ui.message("authentication." + schemeId + ".name") }
                                <% if (option.currentlySelected) { %>
                                    <span class="option-info">( ${ui.message("authentication.2fa.currentlySelected")} )</span>
                                <% } %>
                            </label>
                        </div>
                    <% } %>
                </div>
                <div>
                    <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="window.location='/${ contextPath }/pihcore/account/${ returnUrl }'" />
                    <input type="submit" class="confirm" id="next-button" value="${ ui.message("emr.next") }"  />
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

<%
    def currentLocaleDisplay = account.defaultLocale ? account.defaultLocale.getDisplayLanguage(emrContext.userContext.locale) : ""
    if (currentLocaleDisplay == "Haitian") {
        currentLocaleDisplay = "Haitian Creole"
    }

    def editLink = isOwnAccount ?
            ui.pageLink("pihcore", "account/myAccount", [ edit: true ]) :
            ui.pageLink("pihcore", "account/account", [ personId: personId, edit: true ])
%>

<style>
    #unlock-button {
        margin-top: 1em;
    }
    .account-info-item {
        display: table-row;
    }
    .account-info-label {
        display: table-cell;
        font-weight: bold;
        padding-right: 20px;
        white-space: nowrap;
    }
    .account-info-value {
        display: table-cell;
        white-space: nowrap;
    }
    .float-left {
        float: left;
        clear: left;
        width: 97.91666%;
    }
    .warning {
        width: 100%;
        background-color: yellow;
        padding: 10px;
    }
</style>

<% if (account.locked) { %>
    <div id="locked-warning" class="note warning">
        <span class="icon"><i class="icon-warning-sign medium"></i></span>
        <span class="text">
            <strong>${ ui.message("emr.account.locked.title") }</strong>
            <em>${ ui.message("emr.account.locked.description") }</em>
            <% if (isSysAdmin && !isOwnAccount) { %>
                <button id="unlock-button" value="${ account.person.personId }">${ ui.message("emr.account.locked.button") }</button>
            <% } %>
        </span>
    </div>
<% } %>

<% if (account.user && !account.userEnabled) { %>
    <div id="disabled-warning" class="note warning">
        <span class="icon"><i class="icon-warning-sign medium"></i></span>
        <span class="text">
            <strong>${ ui.message("emr.account.disabled.title") }</strong>
            <em>${ ui.message("emr.account.disabled.description") }</em>
        </span>
    </div>
<% } %>

<div class="account-section">
    <div id="content" class="container-fluid">
        <div class="dashboard clear row">
            <div class="col-12 col-lg-8">
                <div class="row">
                    <div class="col-12 col-lg-12">

                        <div class="info-section">
                            <div class="info-header">
                                <h3>${ ui.message("emr.person.details") }</h3>
                            </div>
                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.person.familyName") }: </span>
                                <span class="account-info-value">${ account.familyName }</span>
                            </div>
                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.person.givenName") }: </span>
                                <span class="account-info-value">${ account.givenName }</span>
                            </div>

                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.gender") }: </span>
                                <span class="account-info-value">${ account.gender ? ui.message("emr.gender." + account.gender) : "" }</span>
                            </div>
                        </div>

                        <% if (account.user) { %>

                            <div class="info-section">
                                <div class="info-header">
                                    <h3>${ ui.message("emr.user.account.details") }</h3>
                                </div>

                                <div class="account-info-item">
                                    <span class="account-info-label">${ ui.message("emr.user.username") }: </span>
                                    <span class="account-info-value">${ account.username }</span>
                                </div>

                                <div class="account-info-item">
                                    <span class="account-info-label">${ ui.message("emr.person.email") }: </span>
                                    <span class="account-info-value">${ account.email ?: '' }</span>
                                </div>

                                <div class="account-info-item">
                                    <span class="account-info-label">${ ui.message("emr.person.phoneNumber") }: </span>
                                    <span class="account-info-value">${ account.phoneNumber ?: '' }</span>
                                </div>

                                <div class="account-info-item">
                                    <span class="account-info-label">${ ui.message("emr.user.defaultLocale") }: </span>
                                    <span class="account-info-value">${ currentLocaleDisplay }</span>
                                </div>

                                <% if (isSysAdmin) { %>

                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("emr.user.privilegeLevel") }: </span>
                                        <span class="account-info-value">${ ui.format(account.privilegeLevel) }</span>
                                    </div>

                                    <div class="account-info-item">
                                        <span class="account-info-label">${ ui.message("emr.user.Capabilities") }: </span>
                                        <span class="account-info-value">
                                            <% account.capabilities.sort { ui.format(it).toLowerCase() }.each { %>
                                                <div>${it.name}</div>
                                            <% } %>
                                        </span>
                                    </div>

                                <% } %>

                            </div>

                        <% } %>

                        <% if (account.providerRole) { %>
                            <div class="info-section">
                                <div class="info-header">
                                    <h3>${ ui.message("emr.provider.details") }</h3>
                                </div>

                                <div class="account-info-item">
                                    <span class="account-info-label">${ ui.message("emr.account.providerRole.label") }: </span>
                                    <span class="account-info-value">${ ui.format(account.providerRole) }</span>
                                </div>

                            </div>
                        <% } %>

                    </div>
                </div>
            </div>
            <div class="dashboard col-12 col-lg-4 p-0">
                <div class="action-section">
                    <ul class="float-left">
                        <h3 >${ ui.message("pihcore.actions") }</h3>
                        <li class="float-left">
                            <a class="float-left" href="${editLink}">
                                <div class="row">
                                    <div class="col-1 col-lg-2">
                                        <i class="fas fa-fw fa-user"></i>
                                    </div>
                                    <div class="col-11 col-lg-10">
                                        ${ ui.message("emr.editAccount") }
                                    </div>
                                </div>
                            </a>
                        </li>
                        <% if (isOwnAccount) { %>
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/changePassword") }">
                                    <div class="row">
                                        <div class="col-1 col-lg-2">
                                            <i class="fas fa-fw fa-lock"></i>
                                        </div>
                                        <div class="col-11 col-lg-10">
                                            ${ ui.message("emr.task.myAccount.changePassword.label") }
                                        </div>
                                    </div>
                                </a>
                            </li>
                        <% } %>
                        <% if (isOwnAccount) { %>
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/changeSecurityQuestion") }">
                                    <div class="row">
                                        <div class="col-1 col-lg-2">
                                            <i class="fas fa-fw fa-question"></i>
                                        </div>
                                        <div class="col-11 col-lg-10">
                                            ${ ui.message("emr.user.changeSecretQuestion") }
                                        </div>
                                    </div>
                                </a>
                            </li>
                        <% } %>
                        <% if (isOwnAccount) { %>
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/twoFactorSetup") }">
                                    <div class="row">
                                        <div class="col-1 col-lg-2">
                                            <i class="fas fa-fw fa-user-lock"></i>
                                        </div>
                                        <div class="col-11 col-lg-10">
                                            ${ ui.message("authentication.2fa.title") }
                                        </div>
                                    </div>
                                </a>
                            </li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
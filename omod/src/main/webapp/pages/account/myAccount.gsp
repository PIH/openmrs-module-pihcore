<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("emr.myAccount") ])
    ui.includeCss("pihcore", "app.css")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    def currentLocaleDisplay = account.defaultLocale.getDisplayLanguage(emrContext.userContext.locale);
    if (currentLocaleDisplay == "Haitian") {
        currentLocaleDisplay = "Haitian Creole"
    }

    def allowedLocalesOptions = []
    allowedLocales.each {
        def displayLanguage = it.getDisplayLanguage(emrContext.userContext.locale);
        if (displayLanguage == "Haitian") { displayLanguage = "Haitian Creole" };  // Hack to fix the fact that ISO standard lists Creole as "Haitian"
        allowedLocalesOptions.push([ label: displayLanguage, value: it ]);
    }
%>

<style>
    .account-info-item {
        display: table-row;
    }
    .account-info-label {
        display: table-cell;
        font-weight: bold;
        padding-right: 20px;
    }
    .account-info-value {
        display: table-cell;
    }
    .float-left {
        float: left;
        clear: left;
        width: 97.91666%;
    }
</style>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}" }
    ];
</script>

<h2>${ ui.message("emr.myAccount") }</h2>

<div class="account-section">
    <% if (mode == "editAccount") { %>

        <form method="post" id="accountForm" autocomplete="off">

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.user.username"),
                    formFieldName: "username",
                    initialValue: (account.username ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.person.givenName"),
                    formFieldName: "givenName",
                    initialValue: (account.givenName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.person.familyName"),
                    formFieldName: "familyName",
                    initialValue: (account.familyName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/radioButtons", [
                    label: ui.message("emr.gender"),
                    formFieldName: "gender",
                    initialValue: (account.gender ?: 'M'),
                    options: genderOptions
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.person.email"),
                    formFieldName: "email",
                    initialValue: (account.email ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.person.phoneNumber"),
                    formFieldName: "phoneNumber",
                    initialValue: (account.phoneNumber ?: '')
            ])}

            <p>
                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("emr.user.defaultLocale"),
                        emptyOptionLabel: ui.message("emr.chooseOne"),
                        formFieldName: "defaultLocale",
                        initialValue: (account.defaultLocale ?: ''),
                        options: allowedLocalesOptions
                ])}
            </p>

            <div>
                <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/pihcore/account/myAccount.page'" />
                <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
            </div>
        </form>

    <% } else { %>
        <div id="content" class="container-fluid">
            <div class="dashboard clear row">
                <div class="col-12 col-lg-8">
                    <div class="row">
                        <div class="col-12 col-lg-6">
                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.user.username") }: </span>
                                <span class="account-info-value">${ account.username }</span>
                            </div>

                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.person.givenName") }: </span>
                                <span class="account-info-value">${ account.givenName }</span>
                            </div>

                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.person.familyName") }: </span>
                                <span class="account-info-value">${ account.familyName }</span>
                            </div>

                            <div class="account-info-item">
                                <span class="account-info-label">${ ui.message("emr.gender") }: </span>
                                <span class="account-info-value">${ ui.message("emr.gender." + account.gender) }</span>
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
                        </div>
                    </div>
                </div>
                <div class="dashboard col-12 col-lg-4 p-0">
                    <div class="action-section">
                        <ul class="float-left">
                            <h3 >${ ui.message("pihcore.actions") }</h3>
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/myAccount", [ mode: "editAccount" ]) }">
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
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/changePassword") }">
                                    <div class="row">
                                        <div class="col-1 col-lg-2">
                                            <i class="icon-book"></i>
                                        </div>
                                        <div class="col-11 col-lg-10">
                                            ${ ui.message("emr.task.myAccount.changePassword.label") }
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li class="float-left">
                                <a class="float-left" href="${ ui.pageLink("pihcore", "account/changeSecurityQuestion") }">
                                    <div class="row">
                                        <div class="col-1 col-lg-2">
                                            <i class="icon-book"></i>
                                        </div>
                                        <div class="col-11 col-lg-10">
                                            ${ ui.message("emr.user.changeSecretQuestion") }
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    <% } %>

</div>

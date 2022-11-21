<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("pihcore", "account.css")
    ui.includeJavascript("pihcore", "account/account.js")

    def createAccount = (account.person.personId == null);

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    def allowedLocalesOptions = []
    allowedLocales.each {
        def displayLanguage = it.getDisplayLanguage(emrContext.userContext.locale);
        if (displayLanguage == "Haitian") { displayLanguage = "Haitian Creole" };  // Hack to fix the fact that ISO standard lists Creole as "Haitian"
        allowedLocalesOptions.push([ label: displayLanguage, value: it ]);
    }

    def providerRolesOptions = []
    providerRoles. each {
        providerRolesOptions.push([ label: ui.format(it), value: it.id ])
    }
    providerRolesOptions = providerRolesOptions.sort { it.label }

    def cancelUrl = createAccount ? "pihcore/account/manageAccounts.page" : "/pihcore/account/account.page?personId=${account.person.personId}"
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}' },
        { label: "${ ui.message("emr.task.accountManagement.label")}" , link: '${ui.pageLink("pihcore", "account/manageAccounts")}'},
        { label: "${ createAccount ? ui.message("emr.createAccount") : ui.format(account.person) }" }
    ];
</script>

<h3>${ createAccount ? ui.message("emr.createAccount") : ui.format(account.person) }</h3>

<% if (editMode) { %>
    <form method="post" id="accountForm" autocomplete="off">

        <!-- dummy fields so that Chrome doesn't autocomplete the real username/password fields with the users own password -->
        <input style="display:none" type="text" name="wrong-username-from-autocomplete"/>
        <input style="display:none" type="password" name="wrong-username-from-autocomplete"/>

        <fieldset>
            <legend>${ ui.message("emr.person.details") }</legend>

            ${ ui.includeFragment("uicommons", "field/text", [
                label: ui.message("emr.person.familyName"),
                formFieldName: "familyName",
                initialValue: (account.familyName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.person.givenName"),
                    formFieldName: "givenName",
                    initialValue: (account.givenName ?: '')
            ])}

            ${ ui.includeFragment("uicommons", "field/radioButtons", [
                label: ui.message("emr.gender"),
                formFieldName: "gender",
                initialValue: (account.gender ?: 'M'),
                options: genderOptions
            ])}

        </fieldset>

        <fieldset>
            <legend>${ ui.message("emr.user.account.details") }</legend>
            <div class="emr_userDetails" <% if (!account.user) { %> style="display: none" <% } %>>

                ${ ui.includeFragment("pihcore", "field/checkbox", [
                    label: ui.message("emr.user.enabled"),
                    id: "userEnabled",
                    formFieldName: "userEnabled",
                    value: "true",
                    checked: account.userEnabled
                ])}

                ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("emr.user.username"),
                    formFieldName: "username",
                    initialValue: (account.username ?: '')
                ])}

                <% if (!account.password && !account.confirmPassword) { %>
                    <button class="emr_passwordDetails emr_userDetails" type="button" onclick="javascript:jQuery('.emr_passwordDetails').toggle()">${ ui.message("emr.user.changeUserPassword") }</button>
                    <p></p>
                <% } %>

                <p class="emr_passwordDetails" <% if(!account.password && !account.confirmPassword) { %>style="display: none"<% } %>>
                    <label class="form-header" for="password">${ ui.message("emr.user.password") }</label>
                    <input type="password" id="password" name="password" value="${ account.password ?: ''}" autocomplete="off"/>
                    <label id="format-password">${ ui.message("emr.account.passwordFormat") }</label>
                    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "password" ])}
                </p>

                <p class="emr_passwordDetails" <% if(!account.password && !account.confirmPassword) { %>style="display: none"<% } %>>
                    <label class="form-header" for="confirmPassword">${ ui.message("emr.user.confirmPassword") }</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" value="${ account.confirmPassword ?: '' }" autocomplete="off" />
                    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "confirmPassword" ])}
                </p>

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

                <p>
                    <strong>${ ui.message("emr.user.Capabilities") }</strong>
                </p>

                <% capabilities.sort { ui.format(it).toLowerCase() }.each{ %>
                    ${ ui.includeFragment("pihcore", "field/checkbox", [
                        label: ui.format(it),
                        formFieldName: "capabilities",
                        value: it.name,
                        checked: account.capabilities?.contains(it)
                    ])}
                <% } %>
            </div>
            <div class="emr_userDetails">
                <% if(!account.user) { %>
                    <button id="createUserAccountButton" type="button" onclick="javascript:emr_createUserAccount()"> ${ ui.message("emr.user.createUserAccount") }</button>
                <% } %>
            </div>
        </fieldset>

        <fieldset>
            <legend>${ ui.message("emr.provider.details") }</legend>
            <div class="emr_providerDetails">
                <p>
                    ${ ui.includeFragment("uicommons", "field/dropDown", [
                            label: ui.message("emr.account.providerRole.label"),
                            emptyOptionLabel: ui.message("emr.chooseOne"),
                            formFieldName: "providerRole",
                            initialValue: (account.providerRole?.id ?: ''),
                            options: providerRolesOptions
                    ])}
                </p>
            </div>
        </fieldset>

        <div>
            <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/${cancelUrl}'" />
            <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
        </div>

    </form>
<% } else { %>
    ${ ui.includeFragment("pihcore", "account/viewAccount", [ person: account.person ])}
<% } %>
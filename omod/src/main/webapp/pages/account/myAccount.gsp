<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("emr.myAccount") ])
    ui.includeCss("pihcore", "app.css")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    def allowedLocalesOptions = []
    allowedLocales.each {
        def displayLanguage = it.getDisplayLanguage(emrContext.userContext.locale);
        if (displayLanguage == "Haitian") { displayLanguage = "Haitian Creole" };  // Hack to fix the fact that ISO standard lists Creole as "Haitian"
        allowedLocalesOptions.push([ label: displayLanguage, value: it ]);
    }
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}" }
    ];
</script>

<h2>${ ui.message("emr.myAccount") }</h2>

<div class="account-section">
    <% if (editMode) { %>

        <form method="post" id="accountForm" autocomplete="off">

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
        ${ ui.includeFragment("pihcore", "account/viewAccount", [ person: account.person ])}
    <% } %>

</div>

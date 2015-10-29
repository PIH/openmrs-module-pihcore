<div class="selectable header" ng-click="expandAllergies(showAlergiesDetails)">
    <span class="one-third">
        <i class="icon-medical"></i>
        <span class="title">${ ui.message("allergyui.allergies") }</span>
    </span>
    <span class="details">
        <span ng-show="allergies.status == 'Unknown'">${ ui.message("general.unknown") }</span>
        <span ng-show="allergies.status == 'No known allergies'">${ ui.message("allergyui.noKnownAllergy") }</span>
        <span ng-show="allergies.status == 'See list'" ng-repeat="allergy in allergies.allergies">
            {{ allergy | omrs.display }} {{\$last ? '' : (\$index==allergies.allergies.length-2) ? ' and ' : ', '}}
        </span>
    </span>
    <span class="overall-actions">
        <a class="expand-encounter" ng-show="!showAlergiesDetails" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="showAlergiesDetails" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a ng-click="goToPage('allergyui', 'allergies', { patientId: patient.uuid })"><i class="icon-pencil"></i></a>
        <a><i class="icon-delete-blank"></i></a

    </span>
</div>
<div class="content" ng-show="showAlergiesDetails">
    <table class="vaccination-table">
        <thead>
        <tr>
            <th>${ ui.message("allergyui.allergen") }</th>
            <th>${ ui.message("allergyui.reaction") }</th>
            <th>${ ui.message("allergyui.severity") }</th>
            <th>${ ui.message("allergyui.comment") }</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="allergy in allergies.allergies">
            <th>{{ allergy.display }}</th>
            <td>
                <span class="value" ng-repeat="reaction in allergy.reactions">
                    {{ reaction.reaction | omrs.display }}
                </span>
            </td>
            <td>
                {{ allergy.severity | omrs.display }}
            </td>
            <td>
                {{ allergy.comment }}
            </td>
        </tr>
        </tbody>
    </table>
</div>
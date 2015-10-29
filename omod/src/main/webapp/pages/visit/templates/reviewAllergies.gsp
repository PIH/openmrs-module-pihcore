<div class="visit-element">
    <div class="selectable header" ng-click="expandAllergies(showAlergiesDetails)">
        <span class="one-third">
            <i class="icon-medical"></i>
            <span class="title">${ ui.message("allergyui.allergies") }</span>
        </span>
        <span class="details">
            <current-allergies patient="visit.patient"></current-allergies>
        </span>
        <span class="overall-actions">
            <a ng-click="goToPage('allergyui', 'allergies', { patientId: visit.patient.uuid })"><i class="icon-pencil"></i></a>
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
</div>

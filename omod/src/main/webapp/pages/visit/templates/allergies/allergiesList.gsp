<!-- allergies-section, edit-allergies classes currently only used for smoke tests -->
<div class="selectable header allergies-section" ng-click="expandAllergies(showAlergiesDetails)">
    <span class="selectable ninety-percent">
        <span class="title encounter-name encounter-span">
            <i class="icon-medical"></i>
            <span class="title">${ ui.message("allergyui.allergies") }</span>
            <!-- <i class="icon-exclamation-sign highlight" ng-show="allergies.status == 'Unknown'" ></i> -->
        </span>
        <span class="obs-span">
            <span ng-show="allergies.status == 'Unknown'">${ ui.message("allergyui.unknown") }</span>
            <span ng-show="allergies.status == 'No known allergies'">${ ui.message("allergyui.noKnownAllergies") }</span>
            <span ng-show="allergies.status == 'See list' && !showAlergiesDetails" ng-repeat="allergy in allergies.allergies">
                {{ allergy | omrsDisplay }}{{\$last ? '' : (\$index==allergies.allergies.length-2) ? ' and ' : ', '}}
            </span>
        </span>
    </span>
    <span class="overall-actions">
        <a class="expand-encounter" ng-show="!showAlergiesDetails" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="showAlergiesDetails" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a class="edit-allergies" ng-show="canEdit()" ng-click="goToPage('allergyui', 'allergies', { patientId: patient.uuid })"><i class="icon-pencil"></i></a>
        <a ng-show="!canEdit()"><i class="icon-delete-blank"></i></a>
        <i class="icon-delete-blank"></i></>
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
                    {{ reaction.reaction | omrsDisplay }}
                </span>
            </td>
            <td>
                {{ allergy.severity | omrsDisplay }}
            </td>
            <td>
                {{ allergy.comment }}
            </td>
        </tr>
        </tbody>
    </table>
</div>
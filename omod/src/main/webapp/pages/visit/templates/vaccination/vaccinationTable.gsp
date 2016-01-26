<div class="selectable header" ng-click="expandVaccinations(showVaccinationTable)">
    <span class="selectable ninety-percent">
        <span class="title encounter-name encounter-span">
            <i class="icon-umbrella"></i>
            <span class="title">${ ui.message("pihcore.visitNote.vaccinations.label") }</span>
        </span>
        <span>
            <span>
                {{ currentVaccinations }}
            </span>
        </span>
    </span>
    <span class="overall-actions">
        <a class="expand-encounter" ng-show="!showVaccinationTable" ng-click="expand()"><i class="icon-caret-right"></i></a>
        <a class="contract-encounter" ng-show="showVaccinationTable" ng-click="contract()"><i class="icon-caret-down"></i></a>
        <a><i class="icon-delete-blank"></i></a>
        <a><i class="icon-delete-blank"></i></a>
    </span>
</div>
<div class="content" ng-show="showVaccinationTable">
    <table class="vaccination-table">
        <thead>
        <tr>
            <th>${ ui.message("pihcore.visitNote.vaccination.header") }</th>
            <th nowrap ng-repeat="sequence in sequences">{{ sequence.label }}</th>
        </tr>
        </thead>
        <tbody>
            <tr ng-repeat="vaccination in vaccinations">
                <th>{{ vaccination.label }}</th>
                <td nowrap ng-repeat="sequence in sequences" ng-class="{ impossible: !isDoseValidForVaccination(sequence, vaccination) }">
                    <span ng-show="existingDose(sequence, vaccination)">
                        {{ existingDose(sequence, vaccination) | groupMember:Concepts.vaccinationDate | obs:"value" | date }}
                        <a class="delete-action" ng-show="canDelete()" ng-click="confirmDelete(sequence, vaccination)"><i class="icon-remove"></i></a>
                    </span>
                    <span ng-show="canEdit() && !existingDose(sequence, vaccination) && isDoseValidForVaccination(sequence, vaccination)">
                        <a ng-click="openDialog(sequence, vaccination)" class="edit-action"><i class="icon-plus"></i></a>
                    </span>
                </td>
            </tr>
        </tbody>
    </table>
</div>
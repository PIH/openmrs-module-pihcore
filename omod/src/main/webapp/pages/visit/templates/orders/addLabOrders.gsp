<h2>${ ui.message("pihcore.visitNote.orders.requestTests") }</h2>

<div ng-repeat="group in labs">
    <h3>{{ group.label }}</h3>
    <ul>
        <li ng-repeat="lab in group.labs">
            <label>
                <input type="checkbox" ng-model="orderLabs[lab.concept.uuid].selected"/>
                {{ lab.label }}
            </label>
        </li>
    </ul>

</div>

<br/>

<div class="actions">
    <button class="confirm right" ng-click="apply()" ng-disabled="!anySelected()">${ ui.message("uicommons.add") }</button>
    <button class="cancel" ui-sref="editPlan">${ ui.message("uicommons.cancel") }</button>
</div>


<div id="active-visit-template" ng-show="multipleTemplates">
    <form ng-show="choosingTemplate || !selectedTemplate">
        <span class="templateName">
            <select ng-model="newVisitTemplate" ng-options="t.label | translate for t in availableTemplates" ng-change="save()">
                <option value="">${ ui.message("pihcore.visitNote.chooseTemplate") }</option>
            </select>
        </span>
    </form>

    <div ng-show="selectedTemplate && !choosingTemplate">
        <span class="templateName">
            <strong>{{ activeTemplate.label | translate }}</strong>
        </span>
        <span class="actions">
            <a ng-click="choosingTemplate = true"><i class="icon-pencil edit-action"></i></a>
        </span>
    </div>
</div>
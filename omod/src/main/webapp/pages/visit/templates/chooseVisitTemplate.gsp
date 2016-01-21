<div id="active-visit-template" ng-show="multipleTemplates">
    <form ng-show="choosingTemplate || !selectedTemplate">
        <span class="template-name">
            <select ng-model="newVisitTemplate" ng-options="t.label | translate for t in availableTemplates" ng-change="save()">
                <option value="">${ ui.message("pihcore.visitNote.chooseTemplate") }</option>
            </select>
        </span>
    </form>

    <div ng-show="selectedTemplate && !choosingTemplate">
        <span class="template-name">
            {{ activeTemplate.label | translate }}
        </span>
        <span class="actions">
            <a ng-click="choosingTemplate = true"><i class="icon-pencil edit-action"></i></a>
        </span>
    </div>
</div>
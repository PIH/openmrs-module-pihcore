<div id="active-visit-template" ng-show="multipleTemplates">
    <form ng-show="choosingTemplate || !selectedTemplate">
        <span class="templateName">
            <select ng-model="newVisitTemplate" ng-options="t.label for t in availableTemplates">
                <option value="">${ ui.message("pihcore.visitNote.chooseTemplate") }</option>
            </select>
        </span>
        <span class="actions">
            <button ng-click="save()">Save</button>
            <button ng-show="selectedTemplate" ng-click="choosingTemplate = false">Cancel</button>
        </span>
    </form>

    <div ng-show="selectedTemplate && !choosingTemplate">
        <span class="templateName">
            <strong>{{ activeTemplate.label | omrs.display }}</strong>
        </span>
        <span class="actions">
            <a ng-click="choosingTemplate = true"><i class="icon-pencil edit-action"></i></a>
        </span>
    </div>
</div>
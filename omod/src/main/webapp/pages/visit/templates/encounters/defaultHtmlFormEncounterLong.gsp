<!-- this template expects the custom template model provided by Html Form Entry to be available in the model -->
<!-- simply lists the fields in the order they appear on the form -->
<!-- TODO currently doesn't support nested sections or more than single-level obsgroups -->
<!-- TODO can we create some sort of recursive template instead? -->

<!-- obs-value and encounter-summary-long classes currently used just for Selenium tests -->

<div class="header" ng-include="'templates/defaultEncounterHeader.page'">
</div>

<div class="content encounter-summary-long">

    <span ng-repeat="section in templateModel.sections" class="aligned">
        <span ng-repeat="field in section.fields" class="aligned">
            <p ng-show="field.value" class="aligned">
                <!-- this cryptic expression is to suppress label name if it is same as previous name -->
                <!-- TODO: can we encapsulate this somewhere else -->
                <label>{{ (\$index == 0 || field.name != section.fields[\$index-1].name) ? field.name : '' }}</label>
                <span class="obs-value">{{ field.value }}</span>
            </p>
            <span ng-repeat="childField in field.fields" class="aligned">
                <p ng-show="childField.value" class="aligned">
                    <label>{{ (\$index == 0 || childField.name != field.fields[\$index-1].name) ? childField.name : '' }}</label>
                    <span class="obs-value">{{ childField.value }}</span>
                </p>
            </span>
        </span>
    </span>

    <span ng-repeat="field in templateModel.fields" class="aligned">
        <p ng-show="field.value" class="aligned">
            <label>{{ (\$index == 0 || field.name != templateModel.fields[\$index-1].name) ? field.name : '' }}</label>
            <span class="obs-value">{{ field.value }}</span>
        </p>
        <span ng-repeat="childField in field.fields" class="aligned">
            <p ng-show="childField.value" class="aligned">
                <label>{{ (\$index == 0 || childField.name != field.fields[\$index-1].name) ? childField.name : '' }}</label>
                <span class="obs-value">{{ childField.value }}</span>
            </p>
        </span>
    </span>

    <div class="book-keeping" ng-include="'templates/standardEncounterBookkeeping.page'"></div>
</div>
<div id="visit-type">
    <choose-visit-template></choose-visit-template>
</div>

<div id="visit-details">
    <visit-details visit="visit"></visit-details>
</div>

<div id="overview-buttons">
    <div id="choose-another-visit">
        <visit-list-dropdown></visit-list-dropdown>
    </div>

    <div id="visit-actions">
        <visit-actions-dropdown></visit-actions-dropdown>
    </div>
</div>

<div ng-repeat="element in visitTemplate.elements">
    <display-element></display-element>
</div>

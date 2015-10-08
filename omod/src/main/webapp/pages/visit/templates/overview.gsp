<div id="visit-type">
    <choose-visit-template></choose-visit-template>
</div>

<div id="visit-details">
    <visit-details visit="visit"></visit-details>
</div>

<div id="choose-another-visit">
    <visit-list></visit-list>
</div>

<div ng-repeat="element in visitTemplate.elements">
    <display-element></display-element>
</div>

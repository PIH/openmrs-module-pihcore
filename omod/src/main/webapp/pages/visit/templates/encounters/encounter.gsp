<div class="visit-element">
    <div ng-include="template"></div>
</div>

<span ng-repeat="section in sections">
    <encounter-section  ng-show="section.type == 'encounter-section'" section="section" encounter="encounter" visit="visit" visits="visits"></encounter-section>
    <include-section  ng-show="section.type == 'include-section'" section="section" encounter="encounter" visit="visit" visits="visits"></include-section>
</span>


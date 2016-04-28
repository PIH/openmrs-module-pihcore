<div class="visit-element">
    <div ng-include="template"></div>
</div>

<span ng-repeat="section in sections" ng-show="showSections">
    <encounter-section  ng-show="section.type == 'encounter-section'" section="section" encounter="encounter" visit="visit"></encounter-section>
    <include-section  ng-show="section.type == 'include-section'" section="section" encounter="encounter" visit="visit"></include-section>
</span>


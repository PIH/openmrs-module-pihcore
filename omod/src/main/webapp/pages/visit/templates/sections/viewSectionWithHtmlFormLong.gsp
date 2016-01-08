<!-- view with Html Form is our default (and currently only) section long view -->
<div class="header" ng-include="'templates/sections/defaultSectionHeader.page'">
</div>

<div class="content encounter-summary-long">

    <div  ng-bind-html="html">  <!-- encounter-summary-long currently only used for Selenium tests -->

    </div>

</div>
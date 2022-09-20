<span class="encounter-name title encounter-span">
    <i class="{{ section.icon }}"></i>  {{ section.label | translate }}
    <i class="icon-exclamation-sign highlight" ng-show="doesNotHaveExistingObs && !hideIncompleteAlert"></i>
</span> <!-- encounter-type class added for smoke tests -->
<span class="obs-span">

    <span>{{ encounter.obs | byConcept:Concepts.dispositionConstruct:true | dispositionLong }}  </span>

    <!-- add a hyphen between disposition and referral site -->
    <span ng-show="(encounter.obs | byConcept:Concepts.dispositionConstruct).length > 0 && (encounter.obs | byConcept:Concepts.referralSite).length > 0" >--
     </span>

    <span ng-show="(encounter.obs | byConcept:Concepts.referralSite).length > 0" >
        {{ 'pihcore.mch.referral' | translate }}(s):
    </span>

    <!-- add referral site -->
    <span class="obs-span">
        <span ng-repeat="obs in encounter.obs | byConcept:Concepts.referralSite">
            {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
        </span>
    </span>

    <!-- ToDo: Add drugOrder
    <span ng-repeat="obs in encounter.obs | byConcept:Concepts.prescriptionConstruct">
        {{ (obs | groupMember:Concepts.medicationOrders).value.concept | omrsDisplay }}{{ \$last ? "" : "," }}
    </span> -->
</span>
<span ng-show="showEncounterDetails" ng-include="'templates/showEncounterDetails.page'" />
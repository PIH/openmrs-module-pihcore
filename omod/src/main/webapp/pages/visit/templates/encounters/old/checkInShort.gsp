<div class="header">
    <span class="one-third" ng-include="'templates/encounters/defaultEncounterHeading.page'"></span>
    <span class="one-half details">
        <span class="one-half">{{ encounter.obs | byConcept:Concepts.typeOfVisit:true | obs }}</span>
      <!--  <span class="one-half">{{ encounter.obs | byConcept:Concepts.paymentInformation:true | obs }}</span> -->
    </span>
    <span class="overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></span>
</div>
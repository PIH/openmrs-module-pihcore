<div class="row justify-content-between">
    <div class="encounter-type encounter-name col-8">
        <i ng-show="icon" class="{{ icon }}"></i> {{ encounter.encounterType.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span">
            <i class="icon-calendar"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.dateLocalized }}
        </span>
        <span class="time-span">
            <i class="icon-time"></i>{{ encounter.encounterDatetime | serverDateLocalized:DatetimeFormats.timeLocalized }}
        </span>
    </div>
</div>
<div class="row col-12">
    <span class="encounter-card-summary">
        <span ng-show="encounter.obs | byConcept:Concepts.referralPriority:true | withCodedValue:Concepts.emergency" class="emergency">{{ encounter.obs | byConcept:Concepts.referralPriority:true | withCodedValue:Concepts.emergency | obs:{ mode:"value"} }}:</span>
        {{ encounter.obs | byConcept:Concepts.airwayExam:true | withCodedValue:Concepts.impairedAirway | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.disability:false | withCodedValue:Concepts.blurredVision | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.disability:false | withCodedValue:Concepts.severeHeadache | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.disability:false | withCodedValue:Concepts.convulsing | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.disability:false | withCodedValue:Concepts.coma | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.spotting | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.labor | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.srom | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.abdPain | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.vaginalBleeding | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.laborWithImminentDelivery | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.pelvicExam:false | withCodedValue:Concepts.severeAbdPain | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.fetusExam:false | withCodedValue:Concepts.decreasedFM | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.fetusExam:false | withCodedValue:Concepts.absentFM | obs:{ mode:"value", appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.temperature:true | obs:{ appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.heartRate:true | obs:{ appendComma:true } }}
        {{ encounter.obs | byConcept:Concepts.respiratoryRate:true | obs }}
    </span>
</div>
<div class="row justify-content-between">
    <div class="col-8">
        <span class="encounter-card-summary provider-and-location-span encounter-provider">
            {{ 'coreapps.by' | translate }}
            {{ encounter.encounterProviders | encounterRole:primaryEncounterRoleUuid | getProviderName }}
        </span>
        <span class="encounter-card-summary admission-location provider-and-location-span"
              ng-hide="encounter.location.uuid == encounter.visit.location.uuid">
            ${ ui.message("uicommons.at") } {{encounter.location | omrsDisplay}}
        </span>
    </div>
    <div class="col-4 text-right overall-actions" ng-include="'templates/encounters/defaultEncounterActions.page'"></div>
</div>

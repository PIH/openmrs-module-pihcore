<i ng-show="icon" class="{{ icon }}"></i>
<span class="title">{{ encounterStub.encounterType | omrs.display }}</span>
<span class="encounter-provider">{{ 'emr.by' | translate }} {{ encounterStub.encounterProviders | omrs.display }}</span>
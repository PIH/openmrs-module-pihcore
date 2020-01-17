# openmrs-module-pihcore
Core metadata and configuration included in all PIH distributions

## Components
Components are chunks of PIH EMR functionality. They can be added or removed to your
[PIH Config file](https://github.com/PIH/mirebalais-puppet/tree/master/mirebalais-modules/openmrs/files/config).

- **activeVisits**
- **adt**
- **allDataExports**: show all data exports, regardless of what components are actually enabled (used by Mirebalais reporting server)
- **allergies**
- **ancProgram**: needs ANCProgramBundle, used by CES
- **appointmentScheduling**
- **archives**
- **asthmaProgram**: needs AsthmaProgramBundle, used by CES
- **biometricsFingerPrints**
- **bmiOnClinicianDashboard**
- **chartSearch**
- **checkIn**
- **checkInHomepageApp**
- **chemotherapy**
- **chwApp**: openmrs-module-providermanagement
- **clinicianDashboard**
- **cohortBuilder**
- **conditionList**: a dashboard widget that lists "conditions"
- **consult**
- **dataExports**: enabled the data export section
- **deathCertificate**
- **diabetesProgram**: needs DiabetesProgram bundle, used by CES
- **dispensing**
- **edConsult**
- **edTriage**
- **edTriageQueue**
- **epilepsyProgram**: needs EpilepsyProgramBundle, used by CES
- **exportPatients**
- **growthChart**
- **hiv**: program. Needs HIVProgramBundle and Haiti HIV MDS package
- **hypertensionProgram**: needs HypertensionProgram bundle, used by CES
- **idcardPrinting**
- **importPatients**
- **labResults**
- **labs**
- **lacollinePatientRegistrationEncounterTypes**
- **legacyMpi**
- **malnutritionProgram**: needs MalnutritionProgramBundle, used by CES
- **managePrinters**
- **mch**: mchForms + mchProgram
- **mchForms**: used by ZL (via "mch")
- **mchProgram**: needs MCHProgramBundle[ZL], used by ZL (via "mch")
- **mentalHealth**: mentalHealthForm + mentalHealthProgram
- **mentalHealthForm**: used by ZL (via "mentalHealth")
- **mentalHealthProgram**: needs MentalHealthProgram bundle, used by ZL and CES
- **monitoringReports**
- **myAccount**
- **ncd**: program
- **oncology**: program
- **orderEntry**: TODO we probably want a different name for this?  break up by drug orders and lab orders, etc?
- **overviewReports**
- **pacsIntegration**
- **pathologyTracking**
- **patientDocuments**: openmrs-module-attachments
- **patientRegistration**
- **prescriptions**
- **primaryCare**: primary care forms for Haiti, Mexico, and Sierra Leone (country-dependent)
- **programs**
- **providerRelationships**: the Relationships widget, configured for providers
- **radiology**
- **relationships**
- **socioEconomics**
- **spa**: the Single-SPA UI
- **surgery**
- **systemAdministration**
- **todaysVisits**
- **uhmVitals**
- **vaccination**
- **vct**
- **visitManagement**
- **visitNote**: get rid of the left visits bar and enable form sections
- **vitals**
- **waitingForConsult**
- **wristbands**
- **zika**: program
# openmrs-module-pihcore
Core metadata and configuration included in all PIH distributions

## Components
Components are chunks of PIH EMR functionality. They can be added or removed to your
[PIH Config file](https://github.com/PIH/mirebalais-puppet/tree/master/mirebalais-modules/openmrs/files/config).

- **activeVisits**: Adds the "Active Visits" app to the homepage. It lists the current 
  active visits. On humdemo
- **adt**: Enables "Awaiting Admission" and "Inpatients" apps on the homepage. Adds 
  the Admission Note as an action. This adds pts to Awaiting Admission, which provides
  a couple forms for Admitting or Cancelling. Requires Admission Note location tag.
- **allDataExports**: show all data exports, regardless of what components are actually
  enabled (used by Mirebalais reporting server)
- **allergies**: Adds Allergy Summary widget to clinician dashboard.
- **ancProgram**: Needs ANCProgramBundle, used by CES.
- **appointmentScheduling**: Adds Actions to request and schedule appointments. Provides
  Appointment Summary widget on clinician dashboard. Adds Schedule Appointment app to home page.
  The Schedule Appointment app provides the UI for configuring appointments.
- **archives**: Enables functionality for paper record management. Pretty targeted for Mirebalais.
  Request paper records, print labels.
- **asthmaProgram**: Needs AsthmaProgramBundle, used by CES.
- **biometricsFingerPrints**: Enables fingerprint search as part of main pt search. Adds
  Biometrics summary to pt registration summary page. Requires a biometrics server to be
  running.
- **bmiOnClinicianDashboard**: Adds height and weight graph to pt dashboard.
- **chartSearch**: Not used.
- **checkIn**: Adds the check-in form. Requires Check-in location tag.
- **checkInHomepageApp**: Adds Cyclical Check-In App on home page. Requires Check-in location tag.
  Requires checkIn component.
- **chemotherapy**: Adds chemo forms as visit actions. Under construction -- cannot be used yet.
- **chwApp**: openmrs-module-providermanagement
- **clinicianDashboard**: Enables the main clinician dashboard (aka patient dashboard).
- **cohortBuilder**: Adds cohort builder OWA. openmrs-owa-cohortbuilder
- **conditionList**: A dashboard widget that lists "conditions".
- **consult**: Adds outpatientConsult form to visit actions. Uses Consult Note Location tag.
- **dataExports**: Enables the "data exports" section of the Reports app on the home page.
- **deathCertificate**: Custom death certificate built for Mirabalis, not currently used.
- **diabetesProgram**: Needs DiabetesProgram bundle, used by CES
- **dispensing**: Adds dispensing form as visit action, Dispensing app to home page, and
  dispensing summary to clinician dashboard.
- **edConsult**: Adds edNote htmlform. Requires ED Note Location Tag.
- **edTriage**: Adds edTriage app/form as visit action. Adds cyclical ED Triage app to home page.
- **edTriageQueue**: Adds ED Triage Queue to Home page. Lists queue of patients based on `edTriage` app.
- **epilepsyProgram**: Needs EpilepsyProgramBundle, used by CES.
- **exportPatients**: App that creates a JSON of all the patients in the system. Adds the button
  to System Administration app from the home page. `exportPatients.page`.
- **growthChart**: Adds an action that leads to a growth chart.
- **hiv**: Program. Needs HIVProgramBundle and Haiti HIV MDS package.
- **hypertensionProgram**: needs HypertensionProgram bundle, used by CES.
- **idcardPrinting**: Adds registration action to print an ID card. Adds an ID Card Printing widget
  to the pt registration summary. Requires "registerPatient" privilege. Printers must be configured.
- **importPatients**: App that takes a JSON from `exportPatients` and creates all of the patients in
  the database. Also linked from the System Administration app from the home page.
- **labResults**: Adds labResults htmlform as a visit action. Superceded by `labs`.
- **labs**: Adds Labs app to home page. Adds Order Labs and View Lab Results actions to pt dashboard.
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

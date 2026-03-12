# OpenMRS App Definitions

This directory contains JSON configuration files for OpenMRS apps, converted from CustomAppLoaderFactory.java.

## Organization

Apps are organized by functional category:

### Core Clinical
- visit-management-apps.json - Visit management and active visits tracking
- check-in-triage-apps.json - Patient check-in and triage workflows
- vitals-apps.json - Vitals capture and monitoring
- clinical-consults-apps.json - Clinical consultation forms and workflows
- adt-inpatient-apps.json - Admission, discharge, transfer and inpatient management

### Diagnostics & Treatment
- radiology-apps.json - Radiology orders and imaging
- labs-pathology-apps.json - Laboratory and pathology management
- dispensing-medication-apps.json - Medication dispensing and prescriptions
- surgery-apps.json - Surgical notes and procedures

### Programs
- hiv-program-apps.json - HIV care and treatment programs
- mch-forms-apps.json - Maternal and child health programs and forms
- ncd-programs-apps.json - Non-communicable disease programs
- mental-health-apps.json - Mental health services
- oncology-chemotherapy-apps.json - Oncology and chemotherapy services
- other-programs-apps.json - Other disease programs and services

### Patient Management
- patient-registration-apps.json - Patient registration and demographics
- patient-dashboard-apps.json - Patient dashboard and clinical summaries
- appointments-apps.json - Appointment scheduling and management
- primary-care-apps.json - Primary care and family services

### Administrative
- reports-exports-apps.json - Reports and data exports
- system-admin-apps.json - System administration
- archives-records-apps.json - Archives and medical records
- queues-lists-apps.json - Patient queues and lists

### Specialized
- specialized-tools-apps.json - Specialized tools and utilities

## JSON Format

Each app definition follows the OpenMRS App Framework JSON structure:

```json
{
  "id": "unique.app.id",
  "description": "App description",
  "label": "Display label",
  "icon": "icon-class",
  "url": "app/url/path",
  "config": {
    "key": "value"
  },
  "require": "privilege or expression"
}
```

## Notes

- These files were automatically converted from Java code
- Some fields marked with TODO need manual review and completion
- The _java_source field shows the original Java code for reference
- Conditional logic from Java is preserved as comments

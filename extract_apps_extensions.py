#!/usr/bin/env python3
"""
Extract apps and extensions from CustomAppLoaderFactory.java and convert to JSON format.
"""

import re
import json
from collections import defaultdict
from pathlib import Path

# Define the categorization mapping
CATEGORIES = {
    'visit-management': {
        'methods': ['enableVisitManagement', 'enableActiveVisits'],
        'description': 'Visit management and active visits tracking'
    },
    'check-in-triage': {
        'methods': ['enableCheckIn', 'enableMCHTriage', 'enableEDTriage', 'enableEDTriageQueue'],
        'description': 'Patient check-in and triage workflows'
    },
    'vitals': {
        'methods': ['enableVitals'],
        'description': 'Vitals capture and monitoring'
    },
    'clinical-consults': {
        'methods': ['enableConsult', 'enableConsultInitial', 'enableNurseConsult', 'enableEDConsult', 'enablePrimaryCare', 'enableFamilyMedicine'],
        'description': 'Clinical consultation forms and workflows'
    },
    'adt-inpatient': {
        'methods': ['enableADT', 'enableADTO3', 'enableBedAdministration', 'enableDeathCertificate'],
        'description': 'Admission, discharge, transfer and inpatient management'
    },
    'radiology': {
        'methods': ['enableRadiology'],
        'description': 'Radiology orders and imaging'
    },
    'labs-pathology': {
        'methods': ['enableLabResults', 'enableLabs', 'enablePathologyTracking', 'enableEcho'],
        'description': 'Laboratory and pathology management'
    },
    'dispensing-medication': {
        'methods': ['enableDispensing', 'enableMedicationDispensing', 'enablePrescription'],
        'description': 'Medication dispensing and prescriptions'
    },
    'surgery': {
        'methods': ['enableSurgery'],
        'description': 'Surgical notes and procedures'
    },
    'hiv-program': {
        'methods': ['enableHIV', 'enableHIVProgram', 'enableHIVForms', 'enableHIVIntakeForm', 'enableHIVActions', 'enableHIViSantePlus', 'enablePrEP', 'enableHaitiHIVLink'],
        'description': 'HIV care and treatment programs'
    },
    'mch-forms': {
        'methods': ['enableMCHForms', 'enableMCHGainMaternal', 'enableMCHGainNewborn', 'enableMCOEForms', 'enableVaccinationOnly', 'enableMCHProgram', 'enableANCProgram', 'enableInfantProgram', 'enablePregnancyProgram', 'enablePMTCTProgram', 'enablePMTCTForms', 'enableEIDForm'],
        'description': 'Maternal and child health programs and forms'
    },
    'ncd-programs': {
        'methods': ['enableNCDs', 'enableAsthmaProgram', 'enableDiabetesProgram', 'enableHypertensionProgram', 'enableEpilepsyProgram', 'enableEpilepsyForm', 'enableMalnutritionProgram'],
        'description': 'Non-communicable disease programs'
    },
    'mental-health': {
        'methods': ['enableMentalHealthForm', 'enableMentalHealthProgram', 'enablePregnancyMentalHealthProgram'],
        'description': 'Mental health services'
    },
    'oncology-chemotherapy': {
        'methods': ['enableOncology', 'enableChemotherapy'],
        'description': 'Oncology and chemotherapy services'
    },
    'other-programs': {
        'methods': ['enablePrograms', 'enableTuberculosis', 'enableTBProgram', 'enableCovid19', 'enableCovid19IntakeForm', 'enableVCT', 'enableOvc', 'enableAYFSProgram', 'enableFamilyPlanningProgram', 'enableGynecologyProgram', 'enablePhysicalRehab', 'enableDrugRehab'],
        'description': 'Other disease programs and services'
    },
    'patient-registration': {
        'methods': ['enablePatientRegistration', 'registerLacollinePatientRegistrationEncounterTypes'],
        'description': 'Patient registration and demographics'
    },
    'patient-dashboard': {
        'methods': ['enableClinicianDashboard', 'enableAllergies', 'enableConditionList', 'enableGrowthChart'],
        'description': 'Patient dashboard and clinical summaries'
    },
    'appointments': {
        'methods': ['enableAppointments', 'enableAppointmentScheduling'],
        'description': 'Appointment scheduling and management'
    },
    'primary-care': {
        'methods': ['enableChildren', 'enableRelationships', 'enableProviderRelationships', 'enableSocioEconomics', 'enablePatientDocuments'],
        'description': 'Primary care and family services'
    },
    'reports-exports': {
        'methods': ['enableReportsAndExports', 'enableExportPatients', 'enableImportPatients', 'enableCohortBuilder'],
        'description': 'Reports and data exports'
    },
    'system-admin': {
        'methods': ['enableSystemAdministration', 'enableManagePrinters', 'enableMyAccount'],
        'description': 'System administration'
    },
    'archives-records': {
        'methods': ['enableArchives', 'enableWristbands'],
        'description': 'Archives and medical records'
    },
    'queues-lists': {
        'methods': ['enableWaitingForConsult', 'enableTodaysVisits'],
        'description': 'Patient queues and lists'
    },
    'specialized-tools': {
        'methods': ['enableBiometrics', 'enableChartSearch', 'enableMarkPatientDead', 'enableCommentForm', 'enableSpaPreview', 'enableJ9', 'enableOrderEntry'],
        'description': 'Specialized tools and utilities'
    }
}

def extract_java_content(file_path):
    """Read the Java file content."""
    with open(file_path, 'r', encoding='utf-8') as f:
        return f.read()

def find_method_body(content, method_name):
    """Extract the body of a method from Java content."""
    # Find the method declaration
    pattern = rf'private\s+void\s+{method_name}\s*\([^)]*\)\s*\{{'
    match = re.search(pattern, content)

    if not match:
        return None

    start_pos = match.end() - 1  # Position of opening brace
    brace_count = 1
    pos = start_pos + 1

    while pos < len(content) and brace_count > 0:
        if content[pos] == '{':
            brace_count += 1
        elif content[pos] == '}':
            brace_count -= 1
        pos += 1

    return content[start_pos+1:pos-1]

def extract_app_calls(method_body):
    """Extract all app() and addToHomePage() calls from method body."""
    apps = []

    # Pattern to match app creation calls
    app_pattern = r'apps\.add\([^;]+\);'
    matches = re.finditer(app_pattern, method_body, re.DOTALL)

    for match in matches:
        apps.append({
            'type': 'app',
            'raw': match.group(0),
            'line_content': match.group(0)
        })

    return apps

def extract_extension_calls(method_body):
    """Extract all extension() calls from method body."""
    extensions = []

    # Pattern to match extension creation calls
    ext_pattern = r'extensions\.add\([^;]+\);'
    matches = re.finditer(ext_pattern, method_body, re.DOTALL)

    for match in matches:
        extensions.append({
            'type': 'extension',
            'raw': match.group(0),
            'line_content': match.group(0)
        })

    return extensions

def categorize_methods():
    """Create reverse mapping from methods to categories."""
    method_to_category = {}
    for category, info in CATEGORIES.items():
        for method in info['methods']:
            method_to_category[method] = category
    return method_to_category

def main():
    java_file = Path('/home/mgoodrich/development/openmrs/modules/pihcore/api/src/main/java/org/openmrs/module/pihcore/apploader/CustomAppLoaderFactory.java')

    if not java_file.exists():
        print(f"Error: File not found: {java_file}")
        return

    print(f"Reading Java file: {java_file}")
    content = extract_java_content(java_file)

    # Get method to category mapping
    method_to_category = categorize_methods()

    # Storage for categorized apps and extensions
    categorized_apps = defaultdict(list)
    categorized_extensions = defaultdict(list)

    # Extract from all methods
    total_methods = sum(len(info['methods']) for info in CATEGORIES.values())
    processed = 0

    for category, info in CATEGORIES.items():
        for method_name in info['methods']:
            processed += 1
            print(f"Processing {processed}/{total_methods}: {method_name} -> {category}")

            method_body = find_method_body(content, method_name)
            if not method_body:
                print(f"  Warning: Could not find method {method_name}")
                continue

            # Extract apps
            apps = extract_app_calls(method_body)
            if apps:
                categorized_apps[category].extend(apps)
                print(f"  Found {len(apps)} app(s)")

            # Extract extensions
            extensions = extract_extension_calls(method_body)
            if extensions:
                categorized_extensions[category].extend(extensions)
                print(f"  Found {len(extensions)} extension(s)")

    # Print summary
    print("\n=== EXTRACTION SUMMARY ===")
    print(f"\nTotal categories: {len(CATEGORIES)}")
    print(f"\nApps by category:")
    total_apps = 0
    for category in sorted(categorized_apps.keys()):
        count = len(categorized_apps[category])
        total_apps += count
        print(f"  {category}: {count}")

    print(f"\nExtensions by category:")
    total_extensions = 0
    for category in sorted(categorized_extensions.keys()):
        count = len(categorized_extensions[category])
        total_extensions += count
        print(f"  {category}: {count}")

    print(f"\nTotal apps extracted: {total_apps}")
    print(f"Total extensions extracted: {total_extensions}")

    # Save raw extracted data to JSON for inspection
    output_dir = Path('/home/mgoodrich/development/openmrs/modules/pihcore')

    with open(output_dir / 'extracted_apps.json', 'w') as f:
        json.dump(dict(categorized_apps), f, indent=2)

    with open(output_dir / 'extracted_extensions.json', 'w') as f:
        json.dump(dict(categorized_extensions), f, indent=2)

    print(f"\nRaw extractions saved to:")
    print(f"  {output_dir / 'extracted_apps.json'}")
    print(f"  {output_dir / 'extracted_extensions.json'}")

if __name__ == '__main__':
    main()

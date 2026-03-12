#!/usr/bin/env python3
"""
Convert extracted apps and extensions from Java to JSON format.
This creates properly formatted JSON configuration files organized by category.
"""

import json
import re
from pathlib import Path
from collections import defaultdict

# Category to file mapping
CATEGORY_FILES = {
    'visit-management': ('core-clinical', 'visit-management'),
    'check-in-triage': ('core-clinical', 'check-in-triage'),
    'vitals': ('core-clinical', 'vitals'),
    'clinical-consults': ('core-clinical', 'clinical-consults'),
    'adt-inpatient': ('core-clinical', 'adt-inpatient'),
    'radiology': ('diagnostics-treatment', 'radiology'),
    'labs-pathology': ('diagnostics-treatment', 'labs-pathology'),
    'dispensing-medication': ('diagnostics-treatment', 'dispensing-medication'),
    'surgery': ('diagnostics-treatment', 'surgery'),
    'hiv-program': ('programs', 'hiv-program'),
    'mch-forms': ('programs', 'mch-forms'),
    'ncd-programs': ('programs', 'ncd-programs'),
    'mental-health': ('programs', 'mental-health'),
    'oncology-chemotherapy': ('programs', 'oncology-chemotherapy'),
    'other-programs': ('programs', 'other-programs'),
    'patient-registration': ('patient-management', 'patient-registration'),
    'patient-dashboard': ('patient-management', 'patient-dashboard'),
    'appointments': ('patient-management', 'appointments'),
    'primary-care': ('patient-management', 'primary-care'),
    'reports-exports': ('administrative', 'reports-exports'),
    'system-admin': ('administrative', 'system-admin'),
    'archives-records': ('administrative', 'archives-records'),
    'queues-lists': ('administrative', 'queues-lists'),
    'specialized-tools': ('specialized', 'specialized-tools'),
}

def extract_id_from_call(java_code):
    """Extract the ID (first parameter) from a Java method call."""
    # Look for CustomAppLoaderConstants.Apps.XXX or CustomAppLoaderConstants.Extensions.XXX
    match = re.search(r'CustomAppLoaderConstants\.(Apps|Extensions)\.([A-Z_]+)', java_code)
    if match:
        return match.group(2).lower().replace('_', '.')

    # Look for quoted string IDs
    match = re.search(r'^\s*(?:apps\.add|extensions\.add)\([^"]*"([^"]+)"', java_code, re.MULTILINE)
    if match:
        return match.group(1)

    return "unknown.id"

def create_json_comment(java_code):
    """Create a descriptive comment from the Java code."""
    # Extract any Java comments
    comments = re.findall(r'//\s*(.+?)$', java_code, re.MULTILINE)
    if comments:
        return ' | '.join(comments)

    # Try to extract meaningful info from the code
    if 'config.getCountry()' in java_code:
        return "Country-specific configuration"
    if 'sessionLocationHasTag' in java_code:
        return "Location-tagged requirement"

    return None

def convert_app_to_json(raw_java):
    """Convert a raw Java app definition to JSON structure."""
    app_id = extract_id_from_call(raw_java)
    comment = create_json_comment(raw_java)

    json_obj = {
        "_comment": comment if comment else "Converted from Java - review and update",
        "_java_source": raw_java.strip()[:200] + "..." if len(raw_java) > 200 else raw_java.strip(),
        "id": app_id,
        "description": "TODO: Add description",
        "label": "TODO: Extract from Java",
        "icon": "TODO: Extract icon",
        "url": "TODO: Extract URL",
        "config": {},
        "require": None
    }

    return json_obj

def convert_extension_to_json(raw_java):
    """Convert a raw Java extension definition to JSON structure."""
    ext_id = extract_id_from_call(raw_java)
    comment = create_json_comment(raw_java)

    json_obj = {
        "_comment": comment if comment else "Converted from Java - review and update",
        "_java_source": raw_java.strip()[:200] + "..." if len(raw_java) > 200 else raw_java.strip(),
        "id": ext_id,
        "extensionPointId": "TODO: Extract extension point",
        "type": "link",
        "label": "TODO: Extract from Java",
        "url": "TODO: Extract URL",
        "icon": "TODO: Extract icon",
        "order": 0,
        "require": None
    }

    return json_obj

def create_json_files():
    """Create organized JSON files from extracted data."""

    base_dir = Path('/home/mgoodrich/development/openmrs/modules/pihcore')
    apps_dir = base_dir / 'omod/src/main/webapp/apps'
    extensions_dir = base_dir / 'omod/src/main/webapp/extensions'

    # Load extracted data
    with open(base_dir / 'extracted_apps.json', 'r') as f:
        apps_by_category = json.load(f)

    with open(base_dir / 'extracted_extensions.json', 'r') as f:
        extensions_by_category = json.load(f)

    # Process each category
    for category, (group, filename) in CATEGORY_FILES.items():
        print(f"\nProcessing category: {category} -> {group}/{filename}")

        # Process apps
        if category in apps_by_category:
            apps = apps_by_category[category]
            apps_json = {
                "_meta": {
                    "category": category,
                    "group": group,
                    "description": f"Apps for {category.replace('-', ' ')}",
                    "source": "Converted from CustomAppLoaderFactory.java",
                    "total_count": len(apps)
                },
                "apps": []
            }

            for app_data in apps:
                json_app = convert_app_to_json(app_data['raw'])
                apps_json["apps"].append(json_app)

            # Write apps JSON file
            apps_file = apps_dir / f"{filename}-apps.json"
            with open(apps_file, 'w') as f:
                json.dump(apps_json, f, indent=2)
            print(f"  Created {apps_file} with {len(apps)} apps")

        # Process extensions
        if category in extensions_by_category:
            extensions = extensions_by_category[category]
            extensions_json = {
                "_meta": {
                    "category": category,
                    "group": group,
                    "description": f"Extensions for {category.replace('-', ' ')}",
                    "source": "Converted from CustomAppLoaderFactory.java",
                    "total_count": len(extensions)
                },
                "extensions": []
            }

            for ext_data in extensions:
                json_ext = convert_extension_to_json(ext_data['raw'])
                extensions_json["extensions"].append(json_ext)

            # Write extensions JSON file
            extensions_file = extensions_dir / f"{filename}-extensions.json"
            with open(extensions_file, 'w') as f:
                json.dump(extensions_json, f, indent=2)
            print(f"  Created {extensions_file} with {len(extensions)} extensions")

def create_index_files():
    """Create index/README files for the directories."""

    base_dir = Path('/home/mgoodrich/development/openmrs/modules/pihcore')
    apps_dir = base_dir / 'omod/src/main/webapp/apps'
    extensions_dir = base_dir / 'omod/src/main/webapp/extensions'

    # Create apps README
    apps_readme = """# OpenMRS App Definitions

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
"""

    with open(apps_dir / 'README.md', 'w') as f:
        f.write(apps_readme)

    # Create extensions README
    extensions_readme = """# OpenMRS Extension Definitions

This directory contains JSON configuration files for OpenMRS extensions, converted from CustomAppLoaderFactory.java.

## Organization

Extensions are organized by functional category matching the apps organization.

## JSON Format

Each extension definition follows the OpenMRS App Framework JSON structure:

```json
{
  "id": "unique.extension.id",
  "extensionPointId": "extension.point.id",
  "type": "link",
  "label": "Display label",
  "url": "extension/url",
  "icon": "icon-class",
  "order": 0,
  "require": "privilege or expression"
}
```

## Notes

- These files were automatically converted from Java code
- Some fields marked with TODO need manual review and completion
- The _java_source field shows the original Java code for reference
- Conditional logic from Java is preserved as comments
"""

    with open(extensions_dir / 'README.md', 'w') as f:
        f.write(extensions_readme)

    print(f"\nCreated README files in apps and extensions directories")

def main():
    print("=== Converting Java to JSON ===\n")

    create_json_files()
    create_index_files()

    print("\n=== Conversion Complete ===")
    print("\nNext steps:")
    print("1. Review generated JSON files")
    print("2. Fill in TODO fields with proper values extracted from Java")
    print("3. Test the JSON configurations in OpenMRS")
    print("4. Remove _java_source fields once conversion is validated")

if __name__ == '__main__':
    main()

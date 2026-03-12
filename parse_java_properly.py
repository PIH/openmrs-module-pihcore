#!/usr/bin/env python3
"""
Enhanced parser to properly extract app and extension parameters from Java code.
"""

import re
import json
from pathlib import Path

def parse_string_param(java_code, param_index=None, param_name=None):
    """Extract a string parameter from a method call."""
    # Remove comments first
    java_code = re.sub(r'//.*?$', '', java_code, flags=re.MULTILINE)

    # Extract all quoted strings
    strings = re.findall(r'"([^"]+)"', java_code)

    if param_index is not None and len(strings) > param_index:
        return strings[param_index]

    return None

def parse_app_definition(raw_java):
    """Parse an app() method call from Java."""
    # Clean the code
    code = re.sub(r'//.*?$', '', raw_java, flags=re.MULTILINE)

    # Extract app ID (first parameter or constant reference)
    app_id = "unknown.app.id"
    const_match = re.search(r'CustomAppLoaderConstants\.Apps\.([A-Z_]+)', code)
    if const_match:
        app_id = const_match.group(1).lower().replace('_', '.')
    else:
        string_match = re.search(r'app\s*\(\s*"([^"]+)"', code)
        if string_match:
            app_id = string_match.group(1)

    # Extract all string parameters
    strings = re.findall(r'"([^"]+)"', code)

    # Try to identify parameters by position or content
    label = None
    icon = None
    url = None
    privilege = None

    for s in strings:
        if '.' in s and not '/' in s and not s.startswith('fas ') and len(s) < 100:
            if not label and ('label' in s.lower() or 'title' in s.lower() or 'app.' in s.lower()):
                label = s
        if s.startswith('fas ') or s.startswith('icon-'):
            icon = s
        if '/' in s or '.page' in s or '.htm' in s or 'spa/' in s:
            url = s
        if 'App:' in s or 'Task:' in s or 'Privilege' in s:
            privilege = s

    # Extract config object if present
    config = {}
    object_match = re.search(r'objectNode\((.*?)\)\)', code, re.DOTALL)
    if object_match:
        config['_note'] = 'Config object present in Java - needs manual extraction'

    # Extract require expression
    require = None
    if 'sessionLocationHasTag' in code:
        tag_match = re.search(r'sessionLocationHasTag\("([^"]+)"\)', code)
        if tag_match:
            require = f"sessionLocationHasTag('{tag_match.group(1)}')"
    elif privilege:
        require = privilege

    return {
        "id": app_id,
        "description": label or "App description",
        "label": label or "App Label",
        "icon": icon or "fas fa-fw fa-file",
        "url": url or "",
        "config": config if config else {},
        "require": require
    }

def parse_extension_definition(raw_java):
    """Parse an extension method call from Java."""
    # Clean the code
    code = re.sub(r'//.*?$', '', raw_java, flags=re.MULTILINE)

    # Extract extension ID
    ext_id = "unknown.extension.id"
    const_match = re.search(r'CustomAppLoaderConstants\.Extensions\.([A-Z_]+)', code)
    if const_match:
        ext_id = const_match.group(1).lower().replace('_', '.')
    else:
        string_match = re.search(r'(?:extension|overallAction|visitAction|fragmentExtension|dashboardTab)\s*\(\s*"([^"]+)"', code)
        if string_match:
            ext_id = string_match.group(1)

    # Determine extension type
    ext_type = "link"
    if 'fragmentExtension' in code:
        ext_type = "fragment"
    elif 'script' in code:
        ext_type = "script"
    elif 'dashboardTab' in code:
        ext_type = "tab"

    # Extract extension point
    extension_point = None
    point_match = re.search(r'CustomAppLoaderConstants\.ExtensionPoints\.([A-Z_]+)', code)
    if point_match:
        extension_point = point_match.group(1).lower().replace('_', '.')
    else:
        # Try to infer from method name
        if 'overallAction' in code:
            extension_point = "patientDashboard.overallActions"
        elif 'visitAction' in code:
            extension_point = "patientDashboard.visitActions"
        elif 'dashboardTab' in code:
            extension_point = "patientDashboard.tabs"

    # Extract parameters
    strings = re.findall(r'"([^"]+)"', code)

    label = None
    icon = None
    url = None
    privilege = None

    for s in strings:
        if '.' in s and not '/' in s and not s.startswith('fas ') and len(s) < 100:
            if not label and ('label' in s.lower() or 'title' in s.lower() or 'task.' in s.lower()):
                label = s
        if s.startswith('fas ') or s.startswith('icon-'):
            icon = s
        if '/' in s or '.page' in s or '.htm' in s or 'spa/' in s or 'script' in ext_type:
            if not url:
                url = s
        if 'App:' in s or 'Task:' in s or 'Privilege' in s:
            privilege = s

    # Extract require expression
    require = None
    if 'sessionLocationHasTag' in code:
        tag_match = re.search(r'sessionLocationHasTag\("([^"]+)"\)', code)
        if tag_match:
            require = f"sessionLocationHasTag('{tag_match.group(1)}')"
    elif privilege:
        require = privilege

    return {
        "id": ext_id,
        "extensionPointId": extension_point or "unknown.extension.point",
        "type": ext_type,
        "label": label or "Extension Label",
        "url": url or "",
        "icon": icon,
        "order": 0,
        "require": require
    }

def reprocess_all_files():
    """Reprocess all JSON files with better parsing."""

    base_dir = Path('/home/mgoodrich/development/openmrs/modules/pihcore')
    apps_dir = base_dir / 'omod/src/main/webapp/apps'
    extensions_dir = base_dir / 'omod/src/main/webapp/extensions'

    # Load extracted data
    with open(base_dir / 'extracted_apps.json', 'r') as f:
        apps_by_category = json.load(f)

    with open(base_dir / 'extracted_extensions.json', 'r') as f:
        extensions_by_category = json.load(f)

    print("=== Reprocessing with Enhanced Parser ===\n")

    # Reprocess apps
    for category, apps in apps_by_category.items():
        filename = category
        print(f"Processing {len(apps)} apps for {category}...")

        apps_json = {
            "_meta": {
                "category": category,
                "description": f"Apps for {category.replace('-', ' ')}",
                "source": "Converted from CustomAppLoaderFactory.java",
                "total_count": len(apps),
                "note": "Auto-converted from Java - review all fields"
            },
            "apps": []
        }

        for app_data in apps:
            parsed_app = parse_app_definition(app_data['raw'])
            # Keep Java source for reference
            parsed_app['_java_source'] = app_data['raw'].strip()
            apps_json["apps"].append(parsed_app)

        # Write apps JSON file
        apps_file = apps_dir / f"{filename}-apps.json"
        with open(apps_file, 'w') as f:
            json.dump(apps_json, f, indent=2)

    # Reprocess extensions
    for category, extensions in extensions_by_category.items():
        filename = category
        print(f"Processing {len(extensions)} extensions for {category}...")

        extensions_json = {
            "_meta": {
                "category": category,
                "description": f"Extensions for {category.replace('-', ' ')}",
                "source": "Converted from CustomAppLoaderFactory.java",
                "total_count": len(extensions),
                "note": "Auto-converted from Java - review all fields"
            },
            "extensions": []
        }

        for ext_data in extensions:
            parsed_ext = parse_extension_definition(ext_data['raw'])
            # Keep Java source for reference
            parsed_ext['_java_source'] = ext_data['raw'].strip()
            extensions_json["extensions"].append(parsed_ext)

        # Write extensions JSON file
        extensions_file = extensions_dir / f"{filename}-extensions.json"
        with open(extensions_file, 'w') as f:
            json.dump(extensions_json, f, indent=2)

    print("\n=== Reprocessing Complete ===")
    print(f"\nProcessed files in:")
    print(f"  {apps_dir}")
    print(f"  {extensions_dir}")

if __name__ == '__main__':
    reprocess_all_files()

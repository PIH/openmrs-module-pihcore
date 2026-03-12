# OpenMRS Extension Definitions

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

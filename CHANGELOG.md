# Changelog

All notable changes to the Cookie Monster extension will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2025-11-07

### Added
- Domain-based filtering with three modes:
  - All Domains: Filter cookies on all requests
  - In-Scope Only: Filter cookies only on in-scope domains
  - Custom Domain List: Filter cookies on specific domains with subdomain support
- Enhanced UI with split-pane layout
- Custom domain management (add, remove, clear)
- Subdomain matching for custom domains
- Domain validation with helpful error messages
- GitHub Actions workflows for automated builds and releases
- Comprehensive workflow documentation

### Changed
- Updated Montoya API from 2023.10.3.4 to 2025.10
- Redesigned UI to accommodate domain filtering options
- Project version bumped to 1.1.0
- Enhanced README with domain filtering documentation

### Fixed
- HTML tags displaying in UI text (replaced with proper Swing components)

## [1.0.0] - 2025-11-07

### Added
- Initial release
- Dynamic cookie filtering for all HTTP requests
- User-friendly GUI for managing blocked cookies
- Thread-safe cookie blocklist manager
- HTTP request interceptor
- Real-time cookie removal with logging
- Support for all Burp Suite tools (Proxy, Scanner, Intruder, Repeater, etc.)
- Maven build configuration
- Comprehensive README documentation

### Technical Details
- Built with Burp Suite Montoya API 2023.10.3.4
- Java 11 compatibility
- Thread-safe operations using CopyOnWriteArraySet

---

## Release Links

- [1.1.0 - Latest](https://github.com/yourusername/cookie_monster/releases/tag/v1.1.0)
- [1.0.0 - Initial Release](https://github.com/yourusername/cookie_monster/releases/tag/v1.0.0)

## Upgrade Guide

### From 1.0.0 to 1.1.0

No breaking changes. The extension will work exactly as before with added domain filtering capabilities.

**What's New:**
- New right panel for domain filtering settings
- Three filtering modes to choose from
- Custom domain list management

**Migration Steps:**
1. Download the new JAR from the releases page
2. Unload the old extension in Burp Suite
3. Load the new extension
4. Your blocked cookies will need to be re-added (settings are not persisted between sessions)

## Support

For bug reports and feature requests, please visit:
https://github.com/yourusername/cookie_monster/issues

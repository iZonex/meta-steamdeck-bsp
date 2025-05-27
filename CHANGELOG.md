# Changelog

All notable changes to meta-steamdeck-bsp will be documented in this file.

## [1.1.0] - 2024-12-19

### 🚀 Major Updates
- **Updated to Linux 6.12.x** - latest LTS kernel version
- **Migrated to Yocto Scarthgap (5.0)** - modern Yocto Project release
- **Walnasacar (5.2) compatibility** - support for newest release
- **🆕 Interactive Installer** - GUI installer for internal SSD installation
- **📦 GitHub Repository** - Official public repository: [https://github.com/iZonex/meta-steamdeck-bsp](https://github.com/iZonex/meta-steamdeck-bsp)

### 🔧 Kernel Updates
- Updated AMD GPU configuration for DCN 3.5 support
- Added AMD P-State EPP support for better power management
- Improved Sound Open Firmware support for Van Gogh APU
- Updated drivers for modern AMDGPU capabilities

### 🛠️ New Features
- **Interactive Installer Image** - Complete GUI installer with dialog interface
- **Auto-detection** - Automatically detects Steam Deck hardware and internal SSD
- **Multiple Installation Types** - Minimal, gaming, and custom installation options
- **Safety Features** - Multiple confirmations and progress indicators
- **Auto-login** - Automatic startup of installer on boot

### 📦 Version Alignment
Aligned with [official Yocto Project recipe versions](https://wiki.yoctoproject.org/wiki/Recipe_Versions):
- **Linux kernel**: 6.8.x → 6.12.x
- **GCC**: uses system version 14.2
- **Python**: supports 3.13.x
- **Binutils**: current versions from Scarthgap

### 🎮 Steam Deck Improvements
- Optimized settings for OLED display
- Enhanced gaming controller support
- Modern WiFi 6E and Bluetooth drivers
- Updated firmware for AMD Van Gogh

### 🔄 Breaking Changes
- Requires Yocto Scarthgap (5.0) or newer
- Kirkstone no longer supported
- Update build environment according to new instructions

### 📖 Documentation
- Updated build instructions for Scarthgap
- Added system requirements recommendations
- Updated configuration examples
- Added GitHub repository links throughout documentation

---

## [1.0.0] - 2024-12-19

### ✨ Initial Release
- Initial BSP version for Steam Deck OLED
- Basic AMD Van Gogh APU support
- Kernel configuration for all Steam Deck components
- Minimal and full gaming images
- Hardware management tools

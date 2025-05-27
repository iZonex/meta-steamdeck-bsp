# Changelog

All notable changes to this project will be documented in this file.

## [2.0.0] - 2025-05-27

### 🚀 Major Features Added

#### Advanced Failsafe System

- **A/B Root Partitions**: Dual root partitions for safe system updates
- **Automatic Rollback**: System automatically rolls back after 3 failed boot attempts
- **OTA Updates**: Over-the-air updates with verification and safety checks
- **Recovery Partition**: Dedicated partition with backup/restore tools
- **Shared Data Partition**: Games and user data preserved across updates

#### Dual Boot Support

- **SteamOS Preservation**: Automatically detects and preserves existing SteamOS
- **Boot Menu**: 10-second timeout boot menu for OS selection
- **Shared EFI Partition**: Single EFI partition supports multiple operating systems
- **Installation Modes**: Choose between failsafe, dual boot, or simple installation

#### Enhanced Installer

- **Interactive GUI**: Dialog-based installer with multiple options
- **Hardware Detection**: Automatic Steam Deck hardware detection
- **Installation Modes**: Three installation modes (failsafe/dualboot/simple)
- **Advanced Partitioning**: Intelligent partitioning based on selected mode

### 🛠️ New Tools and Commands

#### A/B System Management

- `steamdeck-ab-manager status` - Show A/B system status
- `steamdeck-ab-manager rollback` - Force rollback to previous version
- `steamdeck-ab-manager mark-successful` - Mark current boot as successful
- `steamdeck-ab-manager prepare-update` - Prepare slot for update
- `steamdeck-ab-manager apply-update` - Apply update to slot

#### OTA Update System

- `steamdeck-ota-update` - Interactive update process
- `steamdeck-ota-update check` - Check for available updates
- `steamdeck-ota-update download` - Download pending updates
- `steamdeck-ota-update install` - Install downloaded updates
- `steamdeck-ota-update verify` - Verify update after reboot
- `steamdeck-ota-update rollback` - Rollback to previous version

#### Recovery Tools

- `steamdeck-recovery menu` - Interactive recovery menu
- `steamdeck-recovery backup` - Create system backups
- `steamdeck-recovery restore` - Restore from backup
- `steamdeck-recovery dual-boot` - Setup dual boot configuration
- `steamdeck-recovery repair` - Emergency repair mode

### 🔧 Technical Improvements

#### Partition Layout

- **Shared EFI**: 1GB EFI partition for multiple OS support
- **Root A/B**: 8GB each for A/B system slots
- **Recovery**: 2GB partition for emergency tools
- **Shared Data**: 16GB+ partition for user data and games
- **Optional SteamOS**: 8GB partition for dual boot

#### Boot Process

- **systemd-boot**: Enhanced boot loader configuration
- **Boot Verification**: Automatic boot success verification
- **Health Checks**: System health monitoring and verification
- **Rollback Logic**: Intelligent rollback decision making

#### Safety Features

- **Checksum Verification**: SHA256 verification for all downloads
- **Atomic Updates**: Updates applied atomically to inactive slot
- **Data Protection**: User data preserved during all operations
- **Emergency Recovery**: Always-available recovery partition

### 📦 New Packages

- `steamdeck-failsafe` - A/B system management and OTA updates
- `jq` - JSON processing for update metadata
- Enhanced installer with advanced partitioning support

### 🔄 Updated Components

#### Images

- **steamdeck-image**: Now includes failsafe system by default
- **steamdeck-minimal-image**: Includes basic failsafe tools
- **steamdeck-installer-image**: Complete rewrite with GUI installer

#### WIC Configurations

- **steamdeck-oled-dualboot.wks**: New dual boot partition layout
- **steamdeck-installer.wks**: Bootable USB installer layout

### 🐛 Bug Fixes

- Fixed installer hardware detection for various Steam Deck models
- Improved partition alignment for better performance
- Enhanced error handling in installation scripts
- Better cleanup of temporary files during installation

### 📚 Documentation

- Updated README with comprehensive failsafe documentation
- Added detailed installation mode descriptions
- Enhanced troubleshooting guides
- New safety feature documentation

### ⚠️ Breaking Changes

- **Partition Layout**: New installations use different partition scheme
- **Boot Configuration**: systemd-boot configuration format changed
- **Image Structure**: Images now include failsafe components by default

### 🔄 Migration Notes

- Existing simple installations can be migrated to failsafe mode
- Backup existing data before upgrading to new partition layout
- Use recovery tools for safe migration path

---

## [1.0.0] - 2025-05-14

### Added

- Initial Steam Deck OLED BSP implementation
- Linux kernel 6.12.x with Steam Deck optimizations
- AMD Van Gogh APU support (Zen 2 + RDNA2)
- OLED display configuration
- Audio subsystem support
- WiFi 6E and Bluetooth connectivity
- Gaming controls and input devices
- Power management optimizations
- Steam runtime integration

### Images

- `steamdeck-minimal-image` - Minimal console system
- `steamdeck-image` - Full gaming system with Steam
- `steamdeck-installer-image` - Basic installer

### Hardware Support

- AMD Van Gogh APU (Zen 2 CPU + RDNA 2 GPU)
- 7" OLED display (1280x800)
- Enhanced audio with DSP
- WiFi 6E and Bluetooth 5.3
- Steam Input controls
- NVMe SSD storage
- USB-C connectivity

### Build System

- Yocto Project Scarthgap (5.0) support
- BitBake recipes for all components
- WIC disk image creation
- Complete BSP layer structure

### Documentation

- Initial README and build instructions
- Hardware compatibility documentation
- Installation guides

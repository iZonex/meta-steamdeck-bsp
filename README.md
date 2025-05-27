# Steam Deck OLED BSP

A comprehensive Board Support Package (BSP) for Steam Deck OLED using Yocto Project, featuring advanced failsafe systems and dual boot capabilities.

## Features

### Core System

- **Linux Kernel 6.12.x** with Steam Deck optimizations
- **AMD Van Gogh APU** support (Zen 2 + RDNA2)
- **OLED Display** with proper color management
- **Audio subsystem** with enhanced DSP support
- **WiFi 6E and Bluetooth** connectivity
- **Gaming controls** and haptic feedback

### Advanced Failsafe System

- **A/B Root Partitions** for safe system updates
- **Automatic Rollback** on boot failure (max 3 attempts)
- **OTA Updates** with verification and safety checks
- **Recovery Partition** with backup/restore tools
- **Shared Data Partition** for games and user data

### Dual Boot Support

- **SteamOS Preservation** during installation
- **Boot Menu** with 10-second timeout
- **Shared EFI Partition** for multiple operating systems
- **Automatic Detection** of existing installations

### Installation Modes

1. **Failsafe Mode** (Recommended) - A/B system with automatic rollback
2. **Dual Boot Mode** - Failsafe + SteamOS preservation
3. **Simple Mode** - Traditional single partition setup

## Quick Start

### Easy Installation (Recommended)

```bash
# Download and run dependency installer
curl -fsSL https://raw.githubusercontent.com/iZonex/meta-steamdeck-bsp/main/scripts/install-deps.sh | bash

# Clone and build
git clone https://github.com/iZonex/meta-steamdeck-bsp.git
cd meta-steamdeck-bsp
# Follow BUILD.md for complete instructions
```

### Building Images

```bash
# Clone the repository
git clone https://github.com/iZonex/meta-steamdeck-bsp.git
cd meta-steamdeck-bsp

# Initialize Yocto environment
source oe-init-build-env build

# Add the BSP layer
bitbake-layers add-layer ../meta-steamdeck-bsp

# Set machine configuration
echo 'MACHINE = "steamdeck-oled"' >> conf/local.conf

# Build images
bitbake steamdeck-image                    # Full gaming system
bitbake steamdeck-minimal-image           # Minimal console system
bitbake steamdeck-installer-image         # Interactive installer
```

### Installation Options

#### Option 1: Interactive Installer (Recommended)

1. Flash `steamdeck-installer-image` to USB drive
2. Boot Steam Deck from USB
3. Follow interactive installer prompts
4. Choose installation mode (failsafe/dualboot/simple)

#### Option 2: Direct Image Deployment

```bash
# Flash complete system image
sudo bmaptool copy steamdeck-image.wic.bz2 /dev/sdX
```

#### Option 3: Manual Installation

See [BUILD.md](BUILD.md) for detailed manual installation instructions.

## Failsafe System Usage

### A/B System Management

```bash
# Check system status
steamdeck-ab-manager status

# Force rollback to previous version
steamdeck-ab-manager rollback

# Mark current boot as successful
steamdeck-ab-manager mark-successful
```

### OTA Updates

```bash
# Interactive update process
steamdeck-ota-update

# Check for updates only
steamdeck-ota-update check

# Download and install updates
steamdeck-ota-update download
steamdeck-ota-update install
```

### Recovery Tools

```bash
# Launch recovery menu
steamdeck-recovery menu

# Create system backup
steamdeck-recovery backup current

# Restore from backup
steamdeck-recovery restore <backup_name>

# Setup dual boot
steamdeck-recovery dual-boot
```

### Steam Deck System Tools

The BSP includes specialized tools for managing Steam Deck hardware:

#### Power Management
```bash
# Show current power status
steamdeck-power-management status

# Apply power profiles
steamdeck-power-management profile performance  # Maximum performance
steamdeck-power-management profile balanced     # Balanced power/performance  
steamdeck-power-management profile powersave    # Battery saving mode

# Run power monitoring service
steamdeck-power-management monitor
```

#### Controller Configuration  
```bash
# Show current controller settings
steamdeck-controller-config show

# Reset to default settings
steamdeck-controller-config reset

# Run controller calibration
steamdeck-controller-config calibrate
```

#### Display Configuration
```bash
# Show display status
steamdeck-display-config status

# Set brightness (0-100)
steamdeck-display-config brightness 80

# Set display resolution
steamdeck-display-config resolution eDP-1 1280x800

# Switch display modes
steamdeck-display-config gaming-mode    # Optimized for gaming
steamdeck-display-config desktop-mode   # Optimized for desktop use

# List connected displays
steamdeck-display-config list-displays
```

#### Fan Control
```bash
# Fan control runs automatically as a systemd service
systemctl status steamdeck-fan-control

# Manual control (advanced users)
steamdeck-fan-control --help
```

## System Architecture

### Partition Layout (Failsafe Mode)

```
/dev/nvme0n1p1  1GB     EFI Boot (shared)
/dev/nvme0n1p2  8GB     Root A (active)
/dev/nvme0n1p3  8GB     Root B (standby)
/dev/nvme0n1p4  4GB     Swap (shared)
/dev/nvme0n1p5  2GB     Recovery
/dev/nvme0n1p6  16GB+   Data (shared)
/dev/nvme0n1p7  8GB     SteamOS (dual boot only)
```

### Boot Process

1. **systemd-boot** loads from shared EFI partition
2. **A/B Manager** checks boot count and health
3. **Automatic rollback** if boot fails 3 times
4. **Update verification** after successful boot

## Hardware Support

- **CPU**: AMD Van Gogh (Zen 2 cores)
- **GPU**: RDNA2 integrated graphics
- **Display**: 7" OLED 1280x800 HDR
- **Audio**: Enhanced DSP with spatial audio
- **Storage**: NVMe SSD (64GB/256GB/512GB/1TB)
- **Connectivity**: WiFi 6E, Bluetooth 5.3
- **Controls**: Steam Input with haptics
- **Sensors**: Gyroscope, accelerometer

## Development

### Adding Custom Packages

```bash
# Create new recipe
recipes-custom/mypackage/mypackage_1.0.bb

# Add to image
IMAGE_INSTALL += "mypackage"
```

### Kernel Modifications

```bash
# Kernel configuration
recipes-kernel/linux/linux-steamdeck/defconfig

# Device tree
recipes-kernel/linux/linux-steamdeck/steamdeck-oled.dts
```

## Safety Features

### Automatic Rollback

- Boot failure detection after 3 attempts
- Automatic switch to previous working slot
- System health verification
- Recovery partition access

### Update Safety

- Downloads verified with SHA256 checksums
- Updates applied to inactive slot first
- System verification before switching
- Automatic rollback on verification failure

### Data Protection

- Shared data partition preserved during updates
- Automatic backups before major changes
- Recovery tools for emergency situations
- Dual boot preservation of original OS

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/iZonex/meta-steamdeck-bsp/issues)
- **Documentation**: [Build Guide](BUILD.md)
- **Changelog**: [CHANGELOG.md](CHANGELOG.md)

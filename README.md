# Steam Deck OLED BSP - Professional Work Station

Board Support Package (BSP) for Steam Deck OLED as a **professional work station** with full hardware support. This BSP transforms your Steam Deck into a powerful portable workstation while preserving ALL hardware capabilities including controllers, multimedia, and streaming.

## Key Features

- **Full Hardware Support**: Controllers, haptics, sensors, all Steam Deck features
- **Multimedia Capabilities**: Video streaming, media playback, GStreamer, VLC
- **Professional Tools**: SSH, VPN, network monitoring, system administration
- **Development Environment**: Python (with NumPy, OpenCV), Node.js, GCC, debugging tools  
- **Remote Access**: OpenSSH server, VPN clients, secure file transfer
- **Hardware Control**: GPIO, I2C, SPI tools for hardware interfacing
- **System Monitoring**: htop, iotop, sensors, performance tools
- **Failsafe Updates**: A/B partition system with automatic rollback
- **Dual Boot Support**: Preserve original SteamOS alongside custom system

## Features

### Professional Work Station

- **Full Hardware Support**: All Steam Deck controllers, haptics, sensors, and hardware features
- **Multimedia & Streaming**: VLC, MPV, GStreamer, video streaming capabilities
- **Controller Support**: Joystick tools, Steam Input, controller configuration
- **Remote Access**: SSH server, VPN clients (OpenVPN, WireGuard)
- **Development Tools**: Python (NumPy, OpenCV), Node.js, GCC, GDB, Git
- **System Monitoring**: htop, iotop, sensors, network tools
- **Hardware Control**: GPIO, I2C, SPI tools for interfacing
- **Network Tools**: nmap, tcpdump, Wireshark, iperf3
- **File Management**: rsync, scp, compression tools

### Core System

- **Linux Kernel 6.12.x** with Steam Deck optimizations
- **AMD Van Gogh APU** support (Zen 2 + RDNA2)
- **OLED Display** with proper color management
- **Audio subsystem** with enhanced DSP support
- **WiFi 6E and Bluetooth** connectivity
- **Hardware controls** for professional applications

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

# Build images (choose based on your needs)
bitbake steamdeck-minimal-image           # Minimal console system (~2-4 GB) - AUTO-BUILT
bitbake steamdeck-image                   # Basic work station with common packages (~6-8 GB)
bitbake steamdeck-image-extended          # Extended work station with advanced packages (~10-12 GB)
bitbake steamdeck-installer-image         # Interactive installer (~1-2 GB)
```

#### Image Types

- **steamdeck-minimal-image**: Console-only system with basic tools and failsafe features ✅ **AUTO-BUILT**
- **steamdeck-image**: **Basic work station** with commonly available packages (manual build)
- **steamdeck-image-extended**: **Extended work station** with advanced packages (requires additional meta layers)
- **steamdeck-installer-image**: Interactive installer for USB deployment (manual build)

#### Current Status

🤖 **GitHub Actions** automatically builds: `steamdeck-minimal-image`  
🔧 **Manual build required** for: `steamdeck-image`, `steamdeck-image-extended`, `steamdeck-installer-image`

The basic and extended images require package availability fixes and additional meta layers.

#### What's Included

**Basic Work Station (steamdeck-image)**:
✅ **Hardware**: Basic controller support, USB/PCI tools  
✅ **Multimedia**: GStreamer, FFmpeg (basic)  
✅ **Development**: Python, Node.js, GCC, Git  
✅ **Networking**: SSH, basic tools  
✅ **System Tools**: htop, tree, basic monitoring  

**Extended Work Station (steamdeck-image-extended)**:
✅ **All basic features** plus:  
✅ **Advanced Multimedia**: VLC, MPV, hardware acceleration  
✅ **Advanced Python**: NumPy, OpenCV  
✅ **Advanced Networking**: VPN, Wireshark, nmap  
✅ **Advanced Controllers**: jstest-gtk, antimicrox  
✅ **Advanced System Tools**: tmux, strace, sensors

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

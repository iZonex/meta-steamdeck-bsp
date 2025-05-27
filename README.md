# meta-steamdeck-bsp

Board Support Package (BSP) layer for Steam Deck OLED based on Yocto Project.

**🆕 Updated to latest versions:** Linux 6.12.x, Yocto Scarthgap (5.0), following [official Yocto recipe versions](https://wiki.yoctoproject.org/wiki/Recipe_Versions).

**🔗 GitHub Repository:** [https://github.com/iZonex/meta-steamdeck-bsp](https://github.com/iZonex/meta-steamdeck-bsp)

## Overview

This meta-layer provides hardware support for Steam Deck OLED devices, including:

- AMD Van Gogh APU (Zen 2 CPU + RDNA 2 GPU) support
- OLED display configuration
- Audio subsystem (speakers, microphone, audio jack)
- Wireless connectivity (WiFi 6E, Bluetooth)
- Input devices (gamepad controls, touchscreen, trackpads)
- Power management optimizations
- Steam runtime integration

## Quick Start

### Prerequisites

- Yocto Project (Scarthgap 5.0 or newer)
- Host system with required dependencies

### Building

```bash
# Clone poky
git clone -b scarthgap git://git.yoctoproject.org/poky
cd poky

# Clone this meta-layer
git clone https://github.com/iZonex/meta-steamdeck-bsp.git

# Initialize build environment
source oe-init-build-env build-steamdeck

# Add meta-steamdeck-bsp to bblayers.conf
echo 'BBLAYERS += "${TOPDIR}/../meta-steamdeck-bsp"' >> conf/bblayers.conf

# Set machine in local.conf
echo 'MACHINE = "steamdeck-oled"' >> conf/local.conf

# Build image
bitbake steamdeck-image
```

## Supported Machines

- `steamdeck-oled` - Steam Deck OLED model

## Images

### 🔥 **Ready-to-Flash Bootable Images**

All images create complete disk images that can be directly flashed to USB drives:

- **`steamdeck-minimal-image`** - Minimal base system (~2-4 GB)
  - Console-only environment
  - SSH access, basic tools
  - Hardware drivers and firmware
  - Perfect for development/testing

- **`steamdeck-image`** - Full gaming-optimized image (~8-12 GB)
  - Complete desktop environment
  - Steam client pre-installed
  - Gaming tools (Wine, Lutris, MangoHUD)
  - Multimedia support

- **`steamdeck-installer-image`** - Interactive installer (~1-2 GB) ⭐ **NEW!**
  - Bootable installer with GUI interface
  - Installs system to internal SSD
  - Multiple installation options
  - Auto-detects Steam Deck hardware

### 💾 **Installation Methods**

#### Live USB (Boot from external drive)

```bash
# Flash any image to USB drive
sudo bmaptool copy steamdeck-minimal-image-steamdeck-oled.wic.bmap /dev/sdX
# Boot Steam Deck from USB (Volume Down + Power → Select USB)
```

#### Install to Internal SSD ⭐ **NEW!**

```bash
# Flash installer to USB
sudo bmaptool copy steamdeck-installer-image-steamdeck-oled.wic.bmap /dev/sdX
# Boot from USB → Installer will guide you through installation to internal SSD
```

## File Formats

Each build produces multiple formats:

- **`.wic`** - Complete disk image (use this for flashing)
- **`.wic.bmap`** - Block map for fast flashing with bmaptool
- **`.ext4`** - Root filesystem only

## Contributing

Please submit issues and pull requests to improve hardware support.

**GitHub Repository:** [https://github.com/iZonex/meta-steamdeck-bsp](https://github.com/iZonex/meta-steamdeck-bsp)

## License

MIT License - see LICENSE file for details.

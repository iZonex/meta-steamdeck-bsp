# Build Instructions for meta-steamdeck-bsp

**🔗 GitHub Repository:** [https://github.com/iZonex/meta-steamdeck-bsp](https://github.com/iZonex/meta-steamdeck-bsp)

## System Requirements

### Host System

- Ubuntu 20.04 LTS or newer / Fedora 35+
- Minimum 8 GB RAM (16 GB recommended)
- Minimum 100 GB free disk space
- Multi-core processor (8+ cores recommended)

### Dependencies

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pip python3-pexpect xz-utils \
  debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa \
  libsdl1.2-dev pylint3 xterm python3-subunit mesa-common-dev zstd liblz4-tool

# Fedora
sudo dnf install gawk make wget tar bzip2 gzip python3 unzip perl patch \
  diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo chrpath \
  ccache perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue \
  perl-bignum socat python3-pexpect findutils which file cpio python3-pip \
  xz python3-GitPython python3-jinja2 SDL-devel xterm rpcgen \
  mesa-libGL-devel perl-FindBin perl-File-Compare perl-File-Copy \
  perl-locale zstd lz4
```

## Step-by-Step Build Instructions

### 1. Environment Setup

```bash
# Create working directory
mkdir steamdeck-build
cd steamdeck-build

# Clone Poky (Yocto Project reference distribution)
git clone -b scarthgap git://git.yoctoproject.org/poky
cd poky

# Clone required meta-layers
git clone -b scarthgap git://git.openembedded.org/meta-openembedded
git clone -b scarthgap https://github.com/meta-qt5/meta-qt5.git

# Clone our BSP layer
git clone https://github.com/iZonex/meta-steamdeck-bsp.git
```

### 2. Build Environment Setup

```bash
# Initialize build environment
source oe-init-build-env build-steamdeck

# Edit conf/bblayers.conf
cat >> conf/bblayers.conf << 'EOF'
BBLAYERS += " \
  ${TOPDIR}/../meta-openembedded/meta-oe \
  ${TOPDIR}/../meta-openembedded/meta-python \
  ${TOPDIR}/../meta-openembedded/meta-networking \
  ${TOPDIR}/../meta-openembedded/meta-multimedia \
  ${TOPDIR}/../meta-steamdeck-bsp \
"
EOF
```

### 3. Build Configuration

```bash
# Configure conf/local.conf
cat >> conf/local.conf << 'EOF'
# Machine for Steam Deck OLED
MACHINE = "steamdeck-oled"

# Build optimization (adjust for your processor)
BB_NUMBER_THREADS = "8"
PARALLEL_MAKE = "-j 8"

# Additional disk space for image (in MB)
IMAGE_ROOTFS_EXTRA_SPACE = "2048"

# Enable systemd
INIT_MANAGER = "systemd"

# Distro features
DISTRO_FEATURES:append = " systemd vulkan opengl wayland x11"
DISTRO_FEATURES:remove = "sysvinit"

# Allow commercial licenses for firmware
LICENSE_FLAGS_WHITELIST = "commercial"

# Caching for faster subsequent builds
SSTATE_DIR = "${TOPDIR}/../sstate-cache"
DL_DIR = "${TOPDIR}/../downloads"
EOF
```

### 4. Building Images

#### Installer Image ⭐ **RECOMMENDED FOR FIRST TIME**

```bash
# Build installer image (takes 1-2 hours, smallest download)
bitbake steamdeck-installer-image
```

#### Minimal Image

```bash
# Build minimal image (takes 2-4 hours on first build)
bitbake steamdeck-minimal-image
```

#### Full Gaming Image

```bash
# Build full image with Desktop and Steam (takes 4-8 hours)
bitbake steamdeck-image
```

### 5. Build Results

Built images will be located in:

```
build-steamdeck/tmp/deploy/images/steamdeck-oled/
```

Main files:

- **Installer**: `steamdeck-installer-image-steamdeck-oled.wic`
- **Minimal**: `steamdeck-minimal-image-steamdeck-oled.wic`
- **Gaming**: `steamdeck-image-steamdeck-oled.wic`
- **Block maps**: `*.wic.bmap` (for fast flashing with bmaptool)

## Installation Methods

### Method 1: Interactive Installer ⭐ **RECOMMENDED**

The installer provides a user-friendly way to install the system to Steam Deck's internal SSD:

```bash
# Flash installer to USB drive
sudo bmaptool copy steamdeck-installer-image-steamdeck-oled.wic.bmap /dev/sdX

# Boot Steam Deck from USB:
# 1. Insert USB drive into Steam Deck
# 2. Hold Volume Down + Power to enter BIOS
# 3. Select USB boot option
# 4. Follow the interactive installer wizard
```

**Installer Features:**

- 🖥️ Text-based GUI with dialog interface
- 🔍 Auto-detects Steam Deck hardware
- 💾 Automatically finds internal SSD
- ⚙️ Multiple installation options (minimal/gaming/custom)
- 🛡️ Safety confirmations before making changes
- 📊 Progress indication during installation
- 🔄 Automatic reboot after successful installation

### Method 2: Live USB (No Installation)

Boot directly from USB without installing to internal SSD:

```bash
# Flash any image to USB
sudo bmaptool copy steamdeck-minimal-image-steamdeck-oled.wic.bmap /dev/sdX

# Boot from USB (system runs from USB drive)
```

### Method 3: Manual Installation

Advanced users can manually install using dd:

```bash
# Flash to internal SSD (⚠️ DANGER: This erases everything!)
sudo dd if=steamdeck-minimal-image-steamdeck-oled.wic of=/dev/nvme0n1 bs=4M status=progress
```

## Development and Debugging

### Adding Packages

Add packages to `recipes-core/images/steamdeck-image.bb`:

```bitbake
IMAGE_INSTALL += "new-package"
```

### Kernel Modification

Kernel configuration is located in:

- `recipes-kernel/linux/linux-steamdeck/steamdeck-*.cfg`

### Build Debugging

```bash
# Detailed recipe information
bitbake -e steamdeck-image | grep IMAGE_INSTALL

# Force package rebuild
bitbake -c cleanall steamdeck-tools
bitbake steamdeck-tools

# Debug shell
bitbake -c devshell steamdeck-tools
```

## Known Issues

1. **First build is very slow** - this is normal, use sstate and download caches
2. **Disk space shortage** - ensure you have enough space (100+ GB)
3. **Download errors** - check internet connection for source downloads

## Support

For questions and bug reports, please create issues in the [GitHub repository](https://github.com/iZonex/meta-steamdeck-bsp).

## Known Issues

1. **First build is very slow** - this is normal, use sstate and download caches
2. **Disk space shortage** - ensure you have enough space (100+ GB)
3. **Download errors** - check internet connection for source downloads

## Support

For questions and bug reports, please create issues in the project repository.

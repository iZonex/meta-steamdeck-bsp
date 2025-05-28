# Steam Deck OLED BSP - Build Guide

Comprehensive guide for building Steam Deck Linux images using the Yocto Project.

## System Requirements

### Minimum Requirements

- **OS**: Ubuntu 22.04 LTS / Fedora 38+ / openSUSE 15.5+
- **CPU**: 4 cores (8+ recommended)
- **RAM**: 8 GB (16+ GB recommended)
- **Disk**: 100 GB free space (200+ GB on SSD recommended)
- **Network**: Stable internet connection for package downloads

### Recommended Configuration

- **CPU**: Intel i7/Ryzen 7 or better
- **RAM**: 32 GB or more
- **Disk**: NVMe SSD with 500+ GB free space
- **OS**: Ubuntu 22.04 LTS (most tested)

## Installing Dependencies

### Automatic Installation (Recommended)

Use our automated installer script that detects your Ubuntu version and installs the correct packages:

```bash
# Download and run the dependency installer
curl -fsSL https://raw.githubusercontent.com/iZonex/meta-steamdeck-bsp/main/scripts/install-deps.sh | bash
```

### Manual Installation

### Ubuntu/Debian

```bash
sudo apt-get update
sudo apt-get install -y \
    gawk wget git diffstat unzip texinfo gcc build-essential \
    chrpath socat cpio python3 python3-pip python3-pexpect \
    xz-utils debianutils iputils-ping python3-git python3-jinja2 \
    libegl1-mesa-dev libsdl1.2-dev python3-subunit mesa-common-dev \
    zstd liblz4-tool file locales libacl1 pylint
```

**Note for Ubuntu 24.04 (Noble)**: If you encounter package not found errors, try:
```bash
# Alternative package names for Ubuntu 24.04
sudo apt-get install -y \
    gawk wget git diffstat unzip texinfo gcc build-essential \
    chrpath socat cpio python3 python3-pip python3-pexpect \
    xz-utils debianutils iputils-ping python3-git python3-jinja2 \
    libegl1 libsdl1.2-dev python3-subunit mesa-common-dev \
    zstd liblz4-tool file locales libacl1 pylint
```

### Fedora/CentOS/RHEL

```bash
sudo dnf install -y \
    gawk make wget tar bzip2 gzip python3 unzip perl patch \
    diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo \
    chrpath ccache perl-Data-Dumper perl-Text-ParseWords \
    perl-Thread-Queue perl-bignum xz python3-pexpect \
    hostname file which socat cpio python3-pip python3-jinja2 \
    python3-git python3-subunit zstd lz4 rpcgen
```

### openSUSE

```bash
sudo zypper install -y \
    python3 python3-pip python3-pexpect gcc gcc-c++ git chrpath make \
    wget python3-jinja2 python3-subunit tar gzip gawk which diffstat \
    makeinfo python3-curses patch socat python3-git python3-distutils \
    file cpio findutils zstd lz4
```

## Setting Up Build Environment

### 1. Clone Yocto Project

```bash
# Create working directory
mkdir ~/steamdeck-build && cd ~/steamdeck-build

# Clone Poky (Yocto base)
git clone -b scarthgap git://git.yoctoproject.org/poky
cd poky

# Clone required meta-layers
git clone -b scarthgap git://git.openembedded.org/meta-openembedded

# Clone Steam Deck BSP layer
git clone https://github.com/iZonex/meta-steamdeck-bsp.git
```

### 3. Initialize Build Environment

```bash
# Initialize Yocto environment
source oe-init-build-env build-steamdeck

# Add required layers
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-openembedded/meta-multimedia
bitbake-layers add-layer ../meta-openembedded/meta-networking

# Add Steam Deck BSP layer
bitbake-layers add-layer ../meta-steamdeck-bsp

# Verify added layers
bitbake-layers show-layers
```

## Build Configuration

### Basic Configuration

Edit `conf/local.conf`:

```bash
# Set target machine
MACHINE = "steamdeck-oled"

# Set distribution
DISTRO = "poky"

# Package format
PACKAGE_CLASSES = "package_rpm"

# Multi-core build optimization
BB_NUMBER_THREADS = "8"        # Number of CPU cores
PARALLEL_MAKE = "-j 8"         # Parallel compilation

# Disk space monitoring
BB_DISKMON_DIRS = "\
    STOPTASKS,${TMPDIR},1G,100K \
    STOPTASKS,${DL_DIR},1G,100K \
    STOPTASKS,${SSTATE_DIR},1G,100K \
    STOPTASKS,/tmp,100M,100K \
    ABORT,${TMPDIR},100M,1K \
    ABORT,${DL_DIR},100M,1K \
    ABORT,${SSTATE_DIR},100M,1K \
    ABORT,/tmp,10M,1K"

# Configuration version
CONF_VERSION = "2"
```

### Advanced Configuration

Additional options for `conf/local.conf`:

```bash
# Remove temporary files to save space
INHERIT += "rm_work"

# Caching for faster rebuilds
SSTATE_DIR = "${TOPDIR}/../sstate-cache"
DL_DIR = "${TOPDIR}/../downloads"

# Compiler optimization
EXTRA_OECONF:append = " --enable-optimizations"

# Additional image features
EXTRA_IMAGE_FEATURES += "debug-tweaks tools-debug dev-pkgs"

# Localization
IMAGE_LINGUAS = "en-us"

# Additional drivers
MACHINE_EXTRA_RRECOMMENDS += "kernel-modules"

# Failsafe system by default
DISTRO_FEATURES:append = " systemd"
INIT_MANAGER = "systemd"
```

## Building Images

### Available Images

1. **steamdeck-minimal-image** - Minimal console system
2. **steamdeck-image** - Full gaming system with Steam
3. **steamdeck-installer-image** - Interactive installer

### Build Commands

```bash
# Prepare environment (run in each new session)
cd ~/steamdeck-build/poky
source oe-init-build-env build-steamdeck

# Build minimal image
bitbake steamdeck-minimal-image

# Build full image (takes 2-6 hours)
bitbake steamdeck-image

# Build installer
bitbake steamdeck-installer-image
```

### Parallel Building

To speed up, build multiple images in parallel:

```bash
# In different terminals
bitbake steamdeck-minimal-image &
bitbake steamdeck-installer-image &
wait
bitbake steamdeck-image
```

## Build Results

### Image Location

```bash
cd tmp/deploy/images/steamdeck-oled
ls -la
```

Main files:
- `*.wic.bz2` - Compressed disk images (for direct flashing)
- `*.wic.bmap` - Block maps for fast flashing
- `*.ext4` - Root filesystems
- `*.manifest` - List of installed packages

### Verifying Images

```bash
# Check image sizes
du -h *.wic.bz2

# Create checksums
sha256sum *.wic.bz2 > SHA256SUMS.txt

# Check WIC image structure
wic ls tmp/deploy/images/steamdeck-oled/steamdeck-image-steamdeck-oled.wic
```

## Flashing Images

### Using bmaptool (Recommended)

```bash
# Install bmaptool
sudo apt-get install bmap-tools

# Flash using bmap (faster)
sudo bmaptool copy steamdeck-image-steamdeck-oled.wic.bz2 /dev/sdX
```

### Using dd

```bash
# Decompress and flash
bunzip2 -c steamdeck-image-steamdeck-oled.wic.bz2 | sudo dd of=/dev/sdX bs=4M status=progress
```

### Creating Bootable USB

```bash
# For installer
sudo bmaptool copy steamdeck-installer-image-steamdeck-oled.wic.bz2 /dev/sdX

# Verify USB
lsblk /dev/sdX
```

## Troubleshooting

### Common Build Issues

#### 1. Tune Configuration Errors

**Error**: `PACKAGE_ARCHS variable for DEFAULTTUNE (core2-64) does not contain TUNE_PKGARCH` or `Tuning 'core2-64' has no defined features`

**Solution**: This has been fixed in the latest version. The machine configuration now uses the standard `x86-64` tune instead of `core2-64`. If you still encounter this issue:

1. Check that you're using the latest version of the BSP
2. Verify that `conf/machine/steamdeck-oled.conf` contains `DEFAULTTUNE = "x86-64"`
3. Ensure the required tune include files are present: `require conf/machine/include/x86/arch-x86.inc` and `require conf/machine/include/x86/x86-base.inc`

#### 2. Ubuntu 24.04 Package Issues

**Error**: `Unable to locate package libegl1-mesa` or `pylint3`

**Solution**: Use the automated dependency installer or the updated package names:
```bash
# Use our automated installer (recommended)
curl -fsSL https://raw.githubusercontent.com/iZonex/meta-steamdeck-bsp/main/scripts/install-deps.sh | bash

# Or manually install with updated package names
sudo apt-get install libegl1 pylint  # Instead of libegl1-mesa pylint3
```

#### 3. BitBake User Namespaces Issue (Ubuntu 24.04)

**Error**: `User namespaces are not usable by BitBake, possibly due to AppArmor`

**Solutions** (choose one):

**Option A**: Use Ubuntu 22.04 (recommended)
```bash
# This BSP is tested primarily on Ubuntu 22.04 LTS
```

**Option B**: Configure sysctl (requires reboot)
```bash
echo 'kernel.apparmor_restrict_unprivileged_userns = 0' | sudo tee /etc/sysctl.d/60-bitbake.conf
sudo sysctl --system
# Reboot required
```

**Option C**: Use BitBake workarounds
```bash
# Add to conf/local.conf
PSEUDO_DISABLED = "1"
BB_NO_NETWORK = "1"
CONNECTIVITY_CHECK_URIS = ""
```

#### 4. Layer Dependencies

**Error**: `Layer 'steamdeck-bsp' depends on layer 'networking-layer', but this layer is not enabled`

**Solution**: Add the missing meta-networking layer:
```bash
bitbake-layers add-layer ../meta-openembedded/meta-networking
```

#### 5. Missing Source Files

**Error**: `Unable to get checksum for steamdeck-tools SRC_URI entry steamdeck-controller-config: file could not be found`

**Solution**: This has been fixed in the latest version. The missing tool scripts have been added:
- `steamdeck-controller-config` - Controller configuration and calibration
- `steamdeck-power-management` - Power profiles and TDP control  
- `steamdeck-display-config` - Display settings and external monitor support

If you still encounter this issue, ensure you're using the latest version of the BSP.

#### 6. Invalid IMAGE_FEATURE

**Error**: `'autologin' in IMAGE_FEATURES is not a valid image feature`

**Solution**: This has been fixed in the latest version. The invalid `autologin` IMAGE_FEATURE has been replaced with the correct `serial-autologin-root` feature. If you still encounter this issue:

1. Ensure you're using the latest version of the BSP
2. Check that your image recipes use valid IMAGE_FEATURES (see error message for list of valid features)
3. For automatic root login, use `serial-autologin-root` and `empty-root-password` features

#### 7. Virtual Kernel Provider Issues

**Error**: `Nothing PROVIDES 'virtual/kernel'` or `Required build target has no buildable providers`

**Solution**: This has been fixed in the latest version. The machine configuration now correctly sets `linux-steamdeck` as the preferred kernel provider. If you encounter this issue:

1. Ensure `PREFERRED_PROVIDER_virtual/kernel = "linux-steamdeck"` is set in your machine configuration
2. Verify that the `linux-steamdeck` recipe exists and is compatible with your machine
3. Check that all required kernel configuration files exist in the recipe's files directory

#### 8. Network Access Issues with Kernel Sources

**Error**: `Network access disabled through BB_NO_NETWORK` or `NetworkAccess: Network access disabled` when trying to fetch kernel sources

**Solution**: This has been fixed in the latest version. The `linux-steamdeck` kernel recipe now uses `linux-yocto` sources instead of direct access to kernel.org, which avoids network access issues while still providing Steam Deck optimizations. If you encounter this issue:

1. Ensure you're using the latest version of the BSP with the updated kernel recipe
2. The kernel recipe now inherits from `linux-yocto` and uses `git://git.yoctoproject.org/linux-yocto.git`
3. If you have `BB_NO_NETWORK = "1"` set for Ubuntu 24.04 compatibility, the new recipe will work with this setting

#### 9. Build Directory Issues

If BitBake can't find the build directory or configuration:
```bash
# Ensure you're in the correct directory and environment is sourced
cd ~/steamdeck-build/poky
source oe-init-build-env build-steamdeck
```

### Getting Help

If you encounter issues not covered here:

1. Check the [GitHub Issues](https://github.com/iZonex/meta-steamdeck-bsp/issues)
2. Review the Yocto Project documentation for your version
3. Ensure all dependencies are correctly installed
4. Verify you're using compatible versions of all components

## Performance Optimization

### Caching

```bash
# Shared sstate cache for all projects
export SSTATE_DIR="/opt/yocto-cache/sstate"
export DL_DIR="/opt/yocto-cache/downloads"
```

### Memory and CPU

```bash
# In local.conf - set to number of cores
BB_NUMBER_THREADS = "$(nproc)"
PARALLEL_MAKE = "-j $(nproc)"

# Limit memory usage if needed
BB_NUMBER_THREADS = "4"
PARALLEL_MAKE = "-j 4"
```

## Useful Commands

```bash
# Show recipe information
bitbake -s | grep steamdeck

# Show dependencies
bitbake -g steamdeck-image

# Show variables
bitbake -e steamdeck-image | grep IMAGE_

# Clean everything
bitbake -c cleanall steamdeck-image

# Check recipe syntax
bitbake -p
```

## Additional Resources

- [Yocto Project Manual](https://docs.yoctoproject.org/)
- [BitBake User Manual](https://docs.yoctoproject.org/bitbake/)
- [Steam Deck Hardware Documentation](https://www.steamdeck.com/en/tech)

## Getting Help

When encountering issues:

1. Check [GitHub Issues](https://github.com/iZonex/meta-steamdeck-bsp/issues)
2. Create new issue with detailed problem description
3. Include error logs and system configuration
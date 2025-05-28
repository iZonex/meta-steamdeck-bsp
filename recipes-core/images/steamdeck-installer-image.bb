SUMMARY = "Steam Deck Installer Image"
DESCRIPTION = "Bootable installer image for Steam Deck Linux with failsafe and dual boot support"

require steamdeck-minimal-image.bb

# Installer specific packages
IMAGE_INSTALL += " \
    steamdeck-installer \
    steamdeck-failsafe \
    auto-login-installer \
    parted \
    gptfdisk \
    dosfstools \
    e2fsprogs \
    rsync \
    dialog \
    pv \
    bmaptool \
    tar \
    gzip \
    bzip2 \
    xz \
"

# System utilities for installation
IMAGE_INSTALL += " \
    util-linux \
    coreutils \
    findutils \
    grep \
    sed \
    gawk \
    which \
    file \
    lsof \
    psmisc \
"

# Hardware detection and management
IMAGE_INSTALL += " \
    udev \
    usbutils \
    pciutils \
    dmidecode \
    lshw \
    hdparm \
    smartmontools \
"

# Network tools for OTA updates
IMAGE_INSTALL += " \
    wget \
    curl \
    ca-certificates \
    openssl \
"

# Hardware support
IMAGE_INSTALL += " \
    kernel-modules \
    linux-firmware \
    linux-firmware-amdgpu \
    steamdeck-firmware \
    mesa \
    mesa-drivers \
"

# Remove packages not needed for installer
IMAGE_INSTALL:remove = " \
    packagegroup-core-boot \
    alsa-utils \
    pulseaudio \
"

# Keep installer image smaller
IMAGE_ROOTFS_EXTRA_SPACE = "524288"

# Enable installer auto-login with valid features
IMAGE_FEATURES += "serial-autologin-root empty-root-password"

# Use WIC for bootable USB creation
WKS_FILE = "steamdeck-installer.wks"

COMPATIBLE_MACHINE = "steamdeck-oled" 
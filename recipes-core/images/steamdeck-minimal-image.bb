SUMMARY = "Minimal Steam Deck OLED image"
DESCRIPTION = "A minimal console-only image for Steam Deck OLED hardware"

IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_LINGUAS = " "

LICENSE = "MIT"

inherit core-image

# Steam Deck specific packages
IMAGE_INSTALL += " \
    kernel-modules \
    linux-firmware \
    linux-firmware-amdgpu \
    steamdeck-firmware \
    steamdeck-tools \
    steamdeck-failsafe \
    mesa \
    mesa-megadriver \
    alsa-utils \
    pulseaudio \
    systemd \
    networkmanager \
    openssh \
    htop \
    nano \
    curl \
    wget \
    git \
    jq \
    vim \
    bash-completion \
"

# Remove unnecessary packages to keep image small
IMAGE_INSTALL:remove = " \
    avahi-daemon \
    dropbear \
"

# Image features
IMAGE_FEATURES += "package-management ssh-server-openssh"

# Root filesystem extra space (in KB)
IMAGE_ROOTFS_EXTRA_SPACE = "1048576"

# Image output types
IMAGE_FSTYPES = "ext4 wic wic.bmap"

COMPATIBLE_MACHINE = "steamdeck-oled" 
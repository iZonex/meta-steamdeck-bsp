SUMMARY = "Steam Deck OLED work/control station image"
DESCRIPTION = "Professional image for Steam Deck OLED as work station with full hardware support"

require steamdeck-minimal-image.bb

# Failsafe and system management
IMAGE_INSTALL += " \
    steamdeck-failsafe \
    jq \
    curl \
    wget \
    rsync \
    git \
"

# Desktop environment and graphics
IMAGE_INSTALL += " \
    xserver-xorg \
    xf86-video-amdgpu \
    xf86-input-libinput \
    xf86-input-evdev \
    mesa-demos \
    vulkan-tools \
    vulkan-loader \
    vulkan-headers \
"

# Desktop environment (lightweight for productivity)
IMAGE_INSTALL += " \
    openbox \
    lxdm \
    xterm \
    pcmanfm \
    links \
    nano \
    vim \
"

# Joystick and controller support (basic packages only)
IMAGE_INSTALL += " \
    joystick \
"

# Multimedia support for streams and video (basic packages)
IMAGE_INSTALL += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
"

# Network and remote access tools (basic)
IMAGE_INSTALL += " \
    openssh \
    openssh-sftp-server \
    curl \
    wget \
"

# System monitoring and control (basic)
IMAGE_INSTALL += " \
    htop \
    tree \
"

# Hardware control and sensors (basic)
IMAGE_INSTALL += " \
    usbutils \
    pciutils \
"

# Programming and automation (basic Python)
IMAGE_INSTALL += " \
    python3 \
    python3-pip \
    python3-dev \
    nodejs \
    npm \
    bash \
    zsh \
"

# File transfer and backup
IMAGE_INSTALL += " \
    scp \
    rsync \
    tar \
    gzip \
    zip \
    unzip \
"

# Development tools
IMAGE_INSTALL += " \
    gcc \
    g++ \
    make \
    cmake \
    pkg-config \
    gdb \
"

# Image features for work station with multimedia
IMAGE_FEATURES += " \
    x11-base \
    hwcodecs \
    tools-profile \
    tools-debug \
    dev-pkgs \
    ssh-server-openssh \
"

# Larger root filesystem for multimedia and work applications
IMAGE_ROOTFS_EXTRA_SPACE = "4194304"

COMPATIBLE_MACHINE = "steamdeck-oled"

# Note: This image provides Steam Deck work station functionality
# with basic hardware support and commonly available packages.
# Advanced packages may require additional meta layers. 
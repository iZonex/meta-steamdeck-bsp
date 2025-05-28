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

# Joystick and controller support (ВАЖНО!)
IMAGE_INSTALL += " \
    joystick \
    jstest-gtk \
    antimicrox \
    steam-devices \
"

# Multimedia support for streams and video (ВАЖНО!)
IMAGE_INSTALL += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-vaapi \
    ffmpeg \
    vlc \
    mpv \
"

# Network and remote access tools
IMAGE_INSTALL += " \
    openssh \
    openssh-sftp-server \
    openvpn \
    wireguard-tools \
    nmap \
    tcpdump \
    wireshark \
    iperf3 \
"

# System monitoring and control
IMAGE_INSTALL += " \
    htop \
    iotop \
    lsof \
    strace \
    tmux \
    screen \
    tree \
"

# Hardware control and sensors (ВАЖНО!)
IMAGE_INSTALL += " \
    i2c-tools \
    spi-tools \
    gpio-utils \
    lm-sensors \
    dmidecode \
    usbutils \
    pciutils \
"

# Programming and automation (Python ОБЯЗАТЕЛЬНО!)
IMAGE_INSTALL += " \
    python3 \
    python3-pip \
    python3-dev \
    python3-numpy \
    python3-opencv \
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

# Note: This image provides FULL Steam Deck hardware functionality
# including joysticks, multimedia, streaming, and development tools.
# Gaming packages (Steam, Wine, games) are excluded for work use. 
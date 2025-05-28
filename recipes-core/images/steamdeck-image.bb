SUMMARY = "Full Steam Deck OLED gaming image"
DESCRIPTION = "Complete gaming image for Steam Deck OLED with Steam client and desktop environment"

require steamdeck-minimal-image.bb

# Failsafe and system management
IMAGE_INSTALL += " \
    steamdeck-failsafe \
    jq \
    curl \
    wget \
"

# Desktop environment and graphics
IMAGE_INSTALL += " \
    xserver-xorg \
    xf86-video-amdgpu \
    xf86-input-libinput \
    xf86-input-evdev \
    mesa-demos \
    glmark2 \
    vulkan-tools \
    vulkan-loader \
    vulkan-headers \
"

# Desktop environment (lightweight)
IMAGE_INSTALL += " \
    openbox \
    lxdm \
    xterm \
    pcmanfm \
    firefox \
    gparted \
"

# Steam and gaming
IMAGE_INSTALL += " \
    steam \
    wine \
    winetricks \
    gamemode \
    mangohud \
    lutris \
"

# Multimedia support
IMAGE_INSTALL += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-vaapi \
    ffmpeg \
    vlc \
"

# Additional gaming tools
IMAGE_INSTALL += " \
    joystick \
    jstest-gtk \
    antimicrox \
    steam-devices \
"

# Development tools (optional)
IMAGE_INSTALL += " \
    gcc \
    g++ \
    make \
    cmake \
    pkg-config \
    python3-dev \
    python3-pip \
"

# Image features for full desktop
IMAGE_FEATURES += " \
    x11-base \
    hwcodecs \
    tools-profile \
    tools-debug \
    dev-pkgs \
"

# Larger root filesystem for games
IMAGE_ROOTFS_EXTRA_SPACE = "4194304"

COMPATIBLE_MACHINE = "steamdeck-oled" 
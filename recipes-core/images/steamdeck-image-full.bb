SUMMARY = "Steam Deck OLED gaming image with all gaming packages"
DESCRIPTION = "Complete gaming image for Steam Deck OLED with Steam client, Wine, and all gaming tools. Use this for gaming purposes only."

# Note: This image is for GAMING use only!
# For work/control station use, build 'steamdeck-image' instead.

require steamdeck-image.bb

# Override work tools with gaming packages
IMAGE_INSTALL_remove = " \
    openssh \
    openssh-sftp-server \
    openvpn \
    wireguard-tools \
    nmap \
    tcpdump \
    wireshark \
    iperf3 \
    htop \
    iotop \
    lsof \
    strace \
    tmux \
    screen \
    tree \
    i2c-tools \
    spi-tools \
    gpio-utils \
    lm-sensors \
    dmidecode \
    nodejs \
    npm \
    bash \
    zsh \
    scp \
    tar \
    gzip \
    zip \
    unzip \
    gdb \
"

# Gaming packages (requires meta-games, meta-wine, etc.)
IMAGE_INSTALL += " \
    steam \
    wine \
    winetricks \
    gamemode \
    mangohud \
    lutris \
    jstest-gtk \
    antimicrox \
    steam-devices \
    joystick \
"

# Gaming multimedia packages
IMAGE_INSTALL += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-vaapi \
    vlc \
    glmark2 \
    ffmpeg \
"

# Gaming desktop tools
IMAGE_INSTALL += " \
    lxdm \
    gparted \
    vulkan-headers \
"

# Add back gaming image features
IMAGE_FEATURES += " \
    hwcodecs \
"

# Even larger root filesystem for gaming
IMAGE_ROOTFS_EXTRA_SPACE = "8388608"

# Note: This image requires additional Yocto layers for gaming:
# - meta-games (for Steam, gaming tools)
# - meta-wine (for Wine)
# - meta-multimedia (for VLC, additional codecs)
# - meta-gnome (for gparted)
#
# Add these to your bblayers.conf:
# BBLAYERS += " \
#   ${BSPDIR}/sources/meta-games \
#   ${BSPDIR}/sources/meta-wine \
#   ${BSPDIR}/sources/meta-multimedia \
#   ${BSPDIR}/sources/meta-gnome \
# " 
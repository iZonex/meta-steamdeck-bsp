SUMMARY = "Steam Deck OLED extended work station image"
DESCRIPTION = "Extended work station image with advanced packages (requires additional meta layers)"

require steamdeck-image.bb

# Advanced multimedia packages (requires meta-multimedia)
IMAGE_INSTALL += " \
    vlc \
    mpv \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-vaapi \
"

# Advanced Python packages (requires meta-python)
IMAGE_INSTALL += " \
    python3-numpy \
    python3-opencv \
"

# Advanced networking tools (requires meta-networking)
IMAGE_INSTALL += " \
    openvpn \
    wireguard-tools \
    nmap \
    tcpdump \
    wireshark \
    iperf3 \
"

# Advanced system tools
IMAGE_INSTALL += " \
    iotop \
    lsof \
    strace \
    tmux \
    screen \
"

# Advanced hardware tools
IMAGE_INSTALL += " \
    i2c-tools \
    spi-tools \
    gpio-utils \
    lm-sensors \
    dmidecode \
"

# Advanced controller tools (requires meta-games or custom recipes)
IMAGE_INSTALL += " \
    jstest-gtk \
    antimicrox \
    steam-devices \
"

# Larger root filesystem for extended functionality
IMAGE_ROOTFS_EXTRA_SPACE = "6291456"

# Note: This image requires additional Yocto layers:
# - meta-multimedia (for VLC, advanced codecs)
# - meta-python (for NumPy, OpenCV)
# - meta-networking (for advanced network tools)
# - meta-games (for controller tools)
#
# Add these to your bblayers.conf:
# BBLAYERS += " \
#   ${BSPDIR}/sources/meta-multimedia \
#   ${BSPDIR}/sources/meta-python \
#   ${BSPDIR}/sources/meta-networking \
#   ${BSPDIR}/sources/meta-games \
# " 
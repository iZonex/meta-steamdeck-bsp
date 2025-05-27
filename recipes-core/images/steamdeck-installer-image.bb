SUMMARY = "Steam Deck OLED installer image"
DESCRIPTION = "Installer image to deploy Steam Deck system to internal SSD"

require steamdeck-minimal-image.bb

# Installer specific packages
IMAGE_INSTALL += " \
    steamdeck-installer \
    parted \
    e2fsprogs \
    dosfstools \
    util-linux \
    rsync \
    pv \
    dialog \
    ncurses \
    bash \
"

# Remove some packages to keep installer small
IMAGE_INSTALL:remove = " \
    steamdeck-tools \
"

# Installer features
IMAGE_FEATURES += "splash"

# Smaller image for installer
IMAGE_ROOTFS_EXTRA_SPACE = "512000"

# Auto-login for installer
IMAGE_INSTALL += "auto-login-installer"

COMPATIBLE_MACHINE = "steamdeck-oled" 
SUMMARY = "Linux kernel optimized for Steam Deck OLED"
DESCRIPTION = "Linux kernel with Steam Deck specific patches and optimizations"

require recipes-kernel/linux/linux-yocto.inc

# Kernel version
PV = "6.12.0"
SRCREV = "v6.12"

# Kernel source
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;branch=linux-6.12.y;protocol=https \
           file://steamdeck-oled.scc \
           file://steamdeck-hardware.cfg \
           file://steamdeck-graphics.cfg \
           file://steamdeck-audio.cfg \
           file://steamdeck-input.cfg \
           file://steamdeck-power.cfg \
           file://0001-steamdeck-oled-display-support.patch \
           file://0002-steamdeck-audio-quirks.patch \
           file://0003-steamdeck-controller-support.patch \
           "

# Kernel config fragments
KERNEL_FEATURES:append = " \
    steamdeck-hardware.cfg \
    steamdeck-graphics.cfg \
    steamdeck-audio.cfg \
    steamdeck-input.cfg \
    steamdeck-power.cfg \
"

# Compatible machine
COMPATIBLE_MACHINE = "steamdeck-oled"

# Linux version for module builds
LINUX_VERSION_EXTENSION = "-steamdeck"

# Dependencies
DEPENDS += "bc-native bison-native flex-native openssl-native" 
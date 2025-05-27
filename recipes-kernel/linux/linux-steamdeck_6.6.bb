SUMMARY = "Linux kernel optimized for Steam Deck OLED"
DESCRIPTION = "Linux kernel with Steam Deck specific patches and optimizations"

require recipes-kernel/linux/linux-yocto.inc

# Use linux-yocto sources instead of direct kernel.org access
inherit kernel-yocto

# Kernel version - use a version that exists in linux-yocto
PV = "6.6+git${SRCPV}"

# Use linux-yocto compatible source configuration
SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;branch=${KBRANCH};name=machine;protocol=https \
           file://steamdeck-oled.scc \
           file://steamdeck-hardware.cfg \
           file://steamdeck-graphics.cfg \
           file://steamdeck-audio.cfg \
           file://steamdeck-input.cfg \
           file://steamdeck-power.cfg \
           "

# Branch and revision for linux-yocto
KBRANCH = "v6.6/standard/base"
SRCREV_machine = "${AUTOREV}"

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
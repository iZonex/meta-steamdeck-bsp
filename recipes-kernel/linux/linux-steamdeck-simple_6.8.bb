SUMMARY = "Steam Deck Linux kernel (simplified)"
DESCRIPTION = "Simplified Linux kernel for Steam Deck based on linux-yocto"

require recipes-kernel/linux/linux-yocto.inc

# Use stable linux-yocto kernel
KBRANCH = "v6.8/standard/base"
KMACHINE = "common-pc-64"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https"

# Use AUTOREV for now - will be set to stable commit later
SRCREV_machine = "${AUTOREV}"
SRCREV_meta = "${AUTOREV}"

# Let Yocto handle the version
LINUX_VERSION = "6.8"
PV = "${LINUX_VERSION}+git${SRCPV}"

# Steam Deck specific configuration fragments
SRC_URI += " \
    file://steamdeck-hardware.cfg \
    file://steamdeck-graphics.cfg \
    file://steamdeck-audio.cfg \
"

# Kernel modules to auto-load
KERNEL_MODULE_AUTOLOAD += "amdgpu snd_hda_intel"

COMPATIBLE_MACHINE = "steamdeck-oled"

# This should build without issues using standard Yocto infrastructure 
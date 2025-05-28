SUMMARY = "Steam Deck Neptune Linux kernel"
DESCRIPTION = "Linux kernel optimized for Steam Deck hardware based on Neptune kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel

# Your Neptune kernel repository
SRC_URI = "git://github.com/iZonex/neptune-linux-kernel.git;protocol=https;branch=frog/6.8"
SRCREV = "${AUTOREV}"

# For development, use latest commit
PV = "6.8+git${SRCPV}"

S = "${WORKDIR}/git"

# Steam Deck specific kernel configuration
KERNEL_FEATURES:append = " steamdeck"
KERNEL_CONFIG_COMMAND = "oe_runmake_call -C ${S} O=${B} olddefconfig"

# Use defconfig from the kernel source if available
do_configure:prepend() {
    if [ -f "${S}/arch/x86/configs/steamdeck_defconfig" ]; then
        cp ${S}/arch/x86/configs/steamdeck_defconfig ${B}/.config
    fi
}

# Kernel modules to auto-load
KERNEL_MODULE_AUTOLOAD += "amdgpu snd_hda_intel"

COMPATIBLE_MACHINE = "steamdeck-oled"

# Dependencies
DEPENDS += "bc-native bison-native"

# Prevent network access issues - use specific commit when building in CI
# SRCREV = "specific-commit-hash-here" 
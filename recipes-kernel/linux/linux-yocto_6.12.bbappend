SUMMARY = "Steam Deck OLED specific kernel configurations"
DESCRIPTION = "Additional kernel configurations for Steam Deck OLED hardware"

# Only apply to steamdeck-oled machine
COMPATIBLE_MACHINE:steamdeck-oled = "steamdeck-oled"

# Steam Deck specific kernel configurations
FILESEXTRAPATHS:prepend := "${THISDIR}/linux-steamdeck:"

# Add Steam Deck specific configuration files
SRC_URI:append:steamdeck-oled = " \
    file://steamdeck-oled.scc \
    file://steamdeck-hardware.cfg \
    file://steamdeck-graphics.cfg \
    file://steamdeck-audio.cfg \
    file://steamdeck-input.cfg \
    file://steamdeck-power.cfg \
"

# Kernel config fragments for Steam Deck
KERNEL_FEATURES:append:steamdeck-oled = " \
    steamdeck-hardware.cfg \
    steamdeck-graphics.cfg \
    steamdeck-audio.cfg \
    steamdeck-input.cfg \
    steamdeck-power.cfg \
"

# Linux version extension for Steam Deck
LINUX_VERSION_EXTENSION:steamdeck-oled = "-steamdeck" 
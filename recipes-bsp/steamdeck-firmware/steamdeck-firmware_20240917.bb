SUMMARY = "Steam Deck Neptune firmware files"
DESCRIPTION = "Firmware files for Steam Deck hardware from Neptune firmware collection"
LICENSE = "CLOSED"

inherit allarch

# Your Neptune firmware repository
SRC_URI = "git://github.com/iZonex/neptune-linux-firmware.git;protocol=https;branch=jupiter-20240917.1"
SRCREV = "${AUTOREV}"

PV = "20240917+git${SRCPV}"

S = "${WORKDIR}/git"

# No configure or compile needed
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware
    
    # Copy all firmware files
    cp -r ${S}/* ${D}${nonarch_base_libdir}/firmware/
    
    # Remove git files if any
    find ${D}${nonarch_base_libdir}/firmware -name ".git*" -exec rm -rf {} + || true
}

FILES:${PN} = "${nonarch_base_libdir}/firmware/*"

# This package supplements linux-firmware
RCONFLICTS:${PN} = "linux-firmware-rtw89"
RREPLACES:${PN} = "linux-firmware-rtw89"

COMPATIBLE_MACHINE = "steamdeck-oled"

# Allow package to be empty if no firmware files
ALLOW_EMPTY:${PN} = "1"

# Prevent network access issues - use specific commit when building in CI  
# SRCREV = "specific-commit-hash-here" 
SUMMARY = "Steam Deck specific firmware files"
DESCRIPTION = "Firmware files required for Steam Deck OLED hardware components"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7674976cfc656dc745739d998883a45"

SRC_URI = "file://LICENSE"

S = "${WORKDIR}"

inherit allarch

PACKAGES = "${PN}"
FILES:${PN} = "/lib/firmware/*"

do_install() {
    install -d ${D}/lib/firmware
    
    # Create placeholder firmware directory
    install -d ${D}/lib/firmware/steamdeck
    
    # AMD GPU firmware (optional)
    if [ -d ${S}/amdgpu ]; then
        cp -r ${S}/amdgpu ${D}/lib/firmware/
    fi
    
    # WiFi firmware (optional)
    if [ -d ${S}/rtw89 ]; then
        cp -r ${S}/rtw89 ${D}/lib/firmware/
    fi
    
    # Steam Deck specific hardware support (optional)
    if [ -d ${S}/jupiter-hw-support ]; then
        cp -r ${S}/jupiter-hw-support ${D}/lib/firmware/
    fi
}

COMPATIBLE_MACHINE = "steamdeck-oled"

# Allow empty package if no firmware files are present
ALLOW_EMPTY:${PN} = "1" 
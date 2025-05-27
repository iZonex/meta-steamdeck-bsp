SUMMARY = "Steam Deck specific firmware files"
DESCRIPTION = "Firmware files required for Steam Deck OLED hardware components"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"

SRC_URI = "file://LICENSE \
           file://amdgpu/ \
           file://rtw89/ \
           file://jupiter-hw-support/ \
           "

S = "${WORKDIR}"

inherit allarch

PACKAGES = "${PN}"
FILES:${PN} = "/lib/firmware/*"

do_install() {
    install -d ${D}/lib/firmware
    
    # AMD GPU firmware
    if [ -d ${S}/amdgpu ]; then
        cp -r ${S}/amdgpu ${D}/lib/firmware/
    fi
    
    # WiFi firmware (RTW89)
    if [ -d ${S}/rtw89 ]; then
        cp -r ${S}/rtw89 ${D}/lib/firmware/
    fi
    
    # Steam Deck specific hardware support
    if [ -d ${S}/jupiter-hw-support ]; then
        cp -r ${S}/jupiter-hw-support ${D}/lib/firmware/
    fi
}

COMPATIBLE_MACHINE = "steamdeck-oled" 
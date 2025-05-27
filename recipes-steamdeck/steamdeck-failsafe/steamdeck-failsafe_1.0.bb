SUMMARY = "Steam Deck Failsafe A/B System Management"
DESCRIPTION = "Tools for managing A/B root partitions and failsafe updates"
HOMEPAGE = "https://github.com/iZonex/meta-steamdeck-bsp"
BUGTRACKER = "https://github.com/iZonex/meta-steamdeck-bsp/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI = " \
    file://steamdeck-ab-manager \
    file://steamdeck-ota-update \
    file://steamdeck-recovery \
    file://ab-manager.service \
    file://ota-update.service \
    file://99-steamdeck-ab.rules \
    file://LICENSE \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    bash \
    util-linux \
    e2fsprogs \
    rsync \
    dialog \
    wget \
    curl \
    bzip2 \
    gzip \
    tar \
"

inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    ab-manager.service \
    ota-update.service \
"

do_install() {
    # Install executables
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/steamdeck-ab-manager ${D}${bindir}/
    install -m 0755 ${WORKDIR}/steamdeck-ota-update ${D}${bindir}/
    install -m 0755 ${WORKDIR}/steamdeck-recovery ${D}${bindir}/

    # Install systemd services
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ab-manager.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/ota-update.service ${D}${systemd_system_unitdir}/

    # Install udev rules
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-steamdeck-ab.rules ${D}${sysconfdir}/udev/rules.d/

    # Create directories for A/B system state
    install -d ${D}${sysconfdir}/steamdeck-ab
    install -d ${D}${localstatedir}/lib/steamdeck-ab

    # Create configuration files
    echo "CURRENT_SLOT=a" > ${D}${sysconfdir}/steamdeck-ab/current-slot
    echo "BOOT_COUNT=0" > ${D}${sysconfdir}/steamdeck-ab/boot-count
    echo "MAX_BOOT_ATTEMPTS=3" > ${D}${sysconfdir}/steamdeck-ab/config
}

FILES:${PN} += " \
    ${bindir}/steamdeck-ab-manager \
    ${bindir}/steamdeck-ota-update \
    ${bindir}/steamdeck-recovery \
    ${systemd_system_unitdir}/ab-manager.service \
    ${systemd_system_unitdir}/ota-update.service \
    ${sysconfdir}/udev/rules.d/99-steamdeck-ab.rules \
    ${sysconfdir}/steamdeck-ab/* \
    ${localstatedir}/lib/steamdeck-ab \
" 
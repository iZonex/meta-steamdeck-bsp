SUMMARY = "Steam Deck System Installer"
DESCRIPTION = "Interactive installer for deploying Steam Deck Linux to internal storage with failsafe and dual boot support"
HOMEPAGE = "https://github.com/iZonex/meta-steamdeck-bsp"
BUGTRACKER = "https://github.com/iZonex/meta-steamdeck-bsp/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI = " \
    file://steamdeck-installer \
    file://steamdeck-install.sh \
    file://steamdeck-install-advanced.sh \
    file://installer-welcome.txt \
    file://LICENSE \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    bash \
    dialog \
    parted \
    util-linux \
    e2fsprogs \
    dosfstools \
    rsync \
    systemd \
    steamdeck-failsafe \
"

inherit systemd

do_install() {
    # Install main installer script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/steamdeck-installer ${D}${bindir}/
    install -m 0755 ${WORKDIR}/steamdeck-install.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/steamdeck-install-advanced.sh ${D}${bindir}/

    # Install installer data
    install -d ${D}${datadir}/steamdeck-installer
    install -m 0644 ${WORKDIR}/installer-welcome.txt ${D}${datadir}/steamdeck-installer/

    # Create systemd service for auto-start
    install -d ${D}${systemd_system_unitdir}
    cat > ${D}${systemd_system_unitdir}/steamdeck-installer.service << EOF
[Unit]
Description=Steam Deck System Installer
After=multi-user.target
ConditionPathExists=!/etc/steamdeck-installed

[Service]
Type=oneshot
ExecStart=/usr/bin/steamdeck-installer
StandardInput=tty
StandardOutput=tty
TTYPath=/dev/tty1
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

    # Create completion marker script
    cat > ${D}${bindir}/steamdeck-installer-complete << 'EOF'
#!/bin/bash
# Mark installation as complete
touch /etc/steamdeck-installed
systemctl disable steamdeck-installer.service
echo "Steam Deck installer disabled. System ready for normal use."
EOF
    chmod +x ${D}${bindir}/steamdeck-installer-complete
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "steamdeck-installer.service"

FILES:${PN} += " \
    ${bindir}/steamdeck-installer \
    ${bindir}/steamdeck-install.sh \
    ${bindir}/steamdeck-install-advanced.sh \
    ${bindir}/steamdeck-installer-complete \
    ${datadir}/steamdeck-installer/installer-welcome.txt \
    ${systemd_system_unitdir}/steamdeck-installer.service \
"

COMPATIBLE_MACHINE = "steamdeck-oled" 
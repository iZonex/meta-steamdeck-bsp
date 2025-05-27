SUMMARY = "Steam Deck system installer"
DESCRIPTION = "Interactive installer to deploy Steam Deck system to internal SSD"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"

SRC_URI = "file://steamdeck-installer \
           file://steamdeck-install.sh \
           file://installer-welcome.txt \
           file://LICENSE \
           "

S = "${WORKDIR}"

RDEPENDS:${PN} = "bash dialog parted e2fsprogs dosfstools util-linux rsync pv"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/steamdeck-installer
    install -d ${D}${systemd_system_unitdir}
    
    # Install installer scripts
    install -m 0755 ${S}/steamdeck-installer ${D}${bindir}/
    install -m 0755 ${S}/steamdeck-install.sh ${D}${bindir}/
    
    # Install configuration files
    install -m 0644 ${S}/installer-welcome.txt ${D}${sysconfdir}/steamdeck-installer/
    
    # Install systemd service for auto-start
    cat > ${D}${systemd_system_unitdir}/steamdeck-installer.service << EOF
[Unit]
Description=Steam Deck System Installer
After=multi-user.target
ConditionPathExists=!/etc/steamdeck-installed

[Service]
Type=oneshot
ExecStart=${bindir}/steamdeck-installer
StandardInput=tty
StandardOutput=tty
TTYPath=/dev/tty1
Environment=TERM=linux
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

    # Install completion marker service
    cat > ${D}${systemd_system_unitdir}/steamdeck-installer-completed.service << EOF
[Unit]
Description=Mark Steam Deck installation as completed
After=steamdeck-installer.service

[Service]
Type=oneshot
ExecStart=/bin/touch /etc/steamdeck-installed
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF
}

inherit systemd

SYSTEMD_SERVICE:${PN} = "steamdeck-installer.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} = "${bindir}/* ${sysconfdir}/steamdeck-installer/* ${systemd_system_unitdir}/*"

COMPATIBLE_MACHINE = "steamdeck-oled" 
SUMMARY = "Steam Deck system tools and utilities"
DESCRIPTION = "Collection of tools for managing Steam Deck hardware features"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"

SRC_URI = "file://steamdeck-controller-config \
           file://steamdeck-fan-control \
           file://steamdeck-power-management \
           file://steamdeck-display-config \
           file://LICENSE \
           "

S = "${WORKDIR}"

RDEPENDS:${PN} = "python3 python3-pip"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/steamdeck
    install -d ${D}${systemd_system_unitdir}
    
    # Install tools
    install -m 0755 ${S}/steamdeck-controller-config ${D}${bindir}/
    install -m 0755 ${S}/steamdeck-fan-control ${D}${bindir}/
    install -m 0755 ${S}/steamdeck-power-management ${D}${bindir}/
    install -m 0755 ${S}/steamdeck-display-config ${D}${bindir}/
    
    # Install systemd services
    cat > ${D}${systemd_system_unitdir}/steamdeck-fan-control.service << EOF
[Unit]
Description=Steam Deck Fan Control Service
After=multi-user.target

[Service]
Type=simple
ExecStart=${bindir}/steamdeck-fan-control
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

    cat > ${D}${systemd_system_unitdir}/steamdeck-power-management.service << EOF
[Unit]
Description=Steam Deck Power Management Service
After=multi-user.target

[Service]
Type=simple
ExecStart=${bindir}/steamdeck-power-management
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
}

inherit systemd

SYSTEMD_SERVICE:${PN} = "steamdeck-fan-control.service steamdeck-power-management.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} = "${bindir}/* ${sysconfdir}/steamdeck/* ${systemd_system_unitdir}/*"

COMPATIBLE_MACHINE = "steamdeck-oled" 
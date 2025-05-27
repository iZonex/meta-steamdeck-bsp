SUMMARY = "Auto-login configuration for Steam Deck installer"
DESCRIPTION = "Configures automatic login to root for installer image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"

SRC_URI = "file://LICENSE"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir}/getty@tty1.service.d
    
    # Override getty service for auto-login
    cat > ${D}${systemd_system_unitdir}/getty@tty1.service.d/override.conf << EOF
[Service]
ExecStart=
ExecStart=-/sbin/agetty --autologin root --noclear %I \$TERM
EOF

    # Create installer startup script
    install -d ${D}${sysconfdir}/profile.d
    cat > ${D}${sysconfdir}/profile.d/installer-autostart.sh << EOF
#!/bin/bash
# Auto-start installer on first login
if [ "\$USER" = "root" ] && [ "\$PWD" = "/root" ] && [ -z "\$SSH_CONNECTION" ]; then
    if [ ! -f /etc/steamdeck-installed ]; then
        clear
        echo "Starting Steam Deck System Installer..."
        sleep 2
        exec /usr/bin/steamdeck-installer
    fi
fi
EOF
    chmod +x ${D}${sysconfdir}/profile.d/installer-autostart.sh
}

FILES:${PN} = "${systemd_system_unitdir}/getty@tty1.service.d/* ${sysconfdir}/profile.d/*"

COMPATIBLE_MACHINE = "steamdeck-oled" 
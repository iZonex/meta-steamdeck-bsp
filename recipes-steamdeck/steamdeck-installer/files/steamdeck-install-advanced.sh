#!/bin/bash
#
# Steam Deck Advanced Installation Script
# Supports A/B partitions, dual boot, and failsafe
#

set -e

TARGET_DISK="$1"
INSTALL_MODE="${2:-failsafe}"  # failsafe, dualboot, simple

# Configuration
EFI_SIZE="1024M"  # Larger for dual boot
SWAP_SIZE="4G"
ROOT_A_SIZE="8G"
ROOT_B_SIZE="8G"
RECOVERY_SIZE="2G"
DATA_SIZE="16G"
ROOT_SOURCE="/dev/sda2"
MOUNT_BASE="/mnt/target"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[INSTALL]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Check parameters
if [ -z "$TARGET_DISK" ]; then
    error "Usage: $0 <target_disk> [install_mode]"
    error "Install modes: failsafe, dualboot, simple"
    error "Example: $0 /dev/nvme0n1 failsafe"
    exit 1
fi

if [ ! -b "$TARGET_DISK" ]; then
    error "Target disk $TARGET_DISK does not exist!"
    exit 1
fi

log "Installing Steam Deck system to $TARGET_DISK"
log "Installation mode: $INSTALL_MODE"

# Detect existing SteamOS
detect_steamos() {
    info "Detecting existing SteamOS installation..."
    
    for part in $(lsblk -nlo NAME "$TARGET_DISK" | tail -n +2); do
        local mount_point="/mnt/steamos-check"
        mkdir -p "$mount_point"
        
        if mount "/dev/$part" "$mount_point" 2>/dev/null; then
            if [ -f "$mount_point/etc/os-release" ]; then
                if grep -q "SteamOS" "$mount_point/etc/os-release"; then
                    local version=$(grep VERSION_ID "$mount_point/etc/os-release" | cut -d'"' -f2)
                    info "Found SteamOS $version on /dev/$part"
                    umount "$mount_point"
                    rmdir "$mount_point"
                    echo "/dev/$part"
                    return 0
                fi
            fi
            umount "$mount_point"
        fi
        rmdir "$mount_point" 2>/dev/null || true
    done
    
    return 1
}

# Backup existing SteamOS
backup_steamos() {
    local steamos_part="$1"
    local backup_dir="/tmp/steamos-backup"
    
    info "Creating SteamOS backup..."
    mkdir -p "$backup_dir"
    
    local mount_point="/mnt/steamos-backup"
    mkdir -p "$mount_point"
    mount "$steamos_part" "$mount_point"
    
    # Create compressed backup
    tar -czf "$backup_dir/steamos-backup.tar.gz" -C "$mount_point" . &
    local tar_pid=$!
    
    while kill -0 $tar_pid 2>/dev/null; do
        printf "."
        sleep 1
    done
    wait $tar_pid
    
    umount "$mount_point"
    rmdir "$mount_point"
    
    info "SteamOS backup created at $backup_dir/steamos-backup.tar.gz"
}

# Create partition layout based on mode
create_partitions() {
    log "Creating partition layout for $INSTALL_MODE mode..."
    
    # Unmount any existing partitions
    umount ${TARGET_DISK}* 2>/dev/null || true
    
    # Create new partition table
    parted -s "$TARGET_DISK" mklabel gpt
    
    case "$INSTALL_MODE" in
        "failsafe")
            create_failsafe_partitions
            ;;
        "dualboot")
            create_dualboot_partitions
            ;;
        "simple")
            create_simple_partitions
            ;;
        *)
            error "Unknown install mode: $INSTALL_MODE"
            exit 1
            ;;
    esac
    
    # Wait for kernel to detect partitions
    sleep 2
    partprobe "$TARGET_DISK"
    sleep 2
}

# Create failsafe A/B partition layout
create_failsafe_partitions() {
    info "Creating failsafe A/B partition layout..."
    
    # EFI partition
    parted -s "$TARGET_DISK" mkpart primary fat32 1MiB "$EFI_SIZE"
    parted -s "$TARGET_DISK" set 1 esp on
    
    # Root A partition
    local start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "+${ROOT_A_SIZE}"
    
    # Root B partition
    start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "+${ROOT_B_SIZE}"
    
    # Swap partition
    start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary linux-swap "$start" "+${SWAP_SIZE}"
    
    # Recovery partition
    start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "+${RECOVERY_SIZE}"
    
    # Data partition (use remaining space)
    start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "100%"
}

# Create dual boot partition layout
create_dualboot_partitions() {
    info "Creating dual boot partition layout..."
    
    # Check if SteamOS exists and ask user
    local steamos_part
    steamos_part=$(detect_steamos)
    
    if [ -n "$steamos_part" ]; then
        warning "Existing SteamOS installation detected!"
        read -p "Do you want to preserve SteamOS for dual boot? (Y/n): " -r
        if [[ ! $REPLY =~ ^[Nn]$ ]]; then
            backup_steamos "$steamos_part"
            PRESERVE_STEAMOS=1
        fi
    fi
    
    # Create failsafe layout + optional SteamOS space
    create_failsafe_partitions
    
    if [ "$PRESERVE_STEAMOS" = "1" ]; then
        # Add SteamOS partition
        local start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
        parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "+8G"
    fi
}

# Create simple single partition layout
create_simple_partitions() {
    info "Creating simple partition layout..."
    
    # EFI partition
    parted -s "$TARGET_DISK" mkpart primary fat32 1MiB "$EFI_SIZE"
    parted -s "$TARGET_DISK" set 1 esp on
    
    # Swap partition
    local start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary linux-swap "$start" "+${SWAP_SIZE}"
    
    # Root partition (use remaining space)
    start=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
    parted -s "$TARGET_DISK" mkpart primary ext4 "$start" "100%"
}

# Format partitions
format_partitions() {
    log "Formatting partitions..."
    
    # Determine partition names
    if [[ "$TARGET_DISK" =~ "nvme" ]]; then
        PART_PREFIX="${TARGET_DISK}p"
    else
        PART_PREFIX="${TARGET_DISK}"
    fi
    
    case "$INSTALL_MODE" in
        "failsafe"|"dualboot")
            format_failsafe_partitions
            ;;
        "simple")
            format_simple_partitions
            ;;
    esac
}

# Format failsafe partitions
format_failsafe_partitions() {
    EFI_PART="${PART_PREFIX}1"
    ROOT_A_PART="${PART_PREFIX}2"
    ROOT_B_PART="${PART_PREFIX}3"
    SWAP_PART="${PART_PREFIX}4"
    RECOVERY_PART="${PART_PREFIX}5"
    DATA_PART="${PART_PREFIX}6"
    
    info "Formatting EFI partition..."
    mkfs.fat -F32 -n "shared-efi" "$EFI_PART"
    
    info "Formatting root A partition..."
    mkfs.ext4 -F -L "rootfs-a" "$ROOT_A_PART"
    
    info "Formatting root B partition..."
    mkfs.ext4 -F -L "rootfs-b" "$ROOT_B_PART"
    
    info "Setting up swap..."
    mkswap -L "shared-swap" "$SWAP_PART"
    
    info "Formatting recovery partition..."
    mkfs.ext4 -F -L "recovery" "$RECOVERY_PART"
    
    info "Formatting data partition..."
    mkfs.ext4 -F -L "shared-data" "$DATA_PART"
    
    if [ "$INSTALL_MODE" = "dualboot" ] && [ "$PRESERVE_STEAMOS" = "1" ]; then
        STEAMOS_PART="${PART_PREFIX}7"
        info "Formatting SteamOS partition..."
        mkfs.ext4 -F -L "steamos-root" "$STEAMOS_PART"
    fi
}

# Format simple partitions
format_simple_partitions() {
    EFI_PART="${PART_PREFIX}1"
    SWAP_PART="${PART_PREFIX}2"
    ROOT_PART="${PART_PREFIX}3"
    
    info "Formatting EFI partition..."
    mkfs.fat -F32 -n "EFI" "$EFI_PART"
    
    info "Setting up swap..."
    mkswap -L "swap" "$SWAP_PART"
    
    info "Formatting root partition..."
    mkfs.ext4 -F -L "root" "$ROOT_PART"
}

# Install system
install_system() {
    log "Installing system..."
    
    case "$INSTALL_MODE" in
        "failsafe"|"dualboot")
            install_failsafe_system
            ;;
        "simple")
            install_simple_system
            ;;
    esac
}

# Install failsafe system
install_failsafe_system() {
    # Mount root A partition (primary)
    mkdir -p "$MOUNT_BASE"
    mount "$ROOT_A_PART" "$MOUNT_BASE"
    
    # Mount EFI partition
    mkdir -p "$MOUNT_BASE/boot"
    mount "$EFI_PART" "$MOUNT_BASE/boot"
    
    # Copy system files
    copy_system_files "$MOUNT_BASE"
    
    # Generate fstab for A/B system
    generate_failsafe_fstab "$MOUNT_BASE"
    
    # Install bootloader
    install_failsafe_bootloader "$MOUNT_BASE"
    
    # Install failsafe tools
    install_failsafe_tools "$MOUNT_BASE"
    
    # Setup recovery partition
    setup_recovery_partition
    
    # Setup data partition
    setup_data_partition
    
    # Unmount
    umount "$MOUNT_BASE/boot"
    umount "$MOUNT_BASE"
}

# Install simple system
install_simple_system() {
    # Mount root partition
    mkdir -p "$MOUNT_BASE"
    mount "$ROOT_PART" "$MOUNT_BASE"
    
    # Mount EFI partition
    mkdir -p "$MOUNT_BASE/boot"
    mount "$EFI_PART" "$MOUNT_BASE/boot"
    
    # Copy system files
    copy_system_files "$MOUNT_BASE"
    
    # Generate simple fstab
    generate_simple_fstab "$MOUNT_BASE"
    
    # Install simple bootloader
    install_simple_bootloader "$MOUNT_BASE"
    
    # Unmount
    umount "$MOUNT_BASE/boot"
    umount "$MOUNT_BASE"
}

# Copy system files
copy_system_files() {
    local target_dir="$1"
    
    info "Copying system files..."
    
    if [ -b "$ROOT_SOURCE" ]; then
        mkdir -p /mnt/source
        mount "$ROOT_SOURCE" /mnt/source
        
        rsync -av --progress \
              --exclude='/proc/*' --exclude='/sys/*' --exclude='/dev/*' \
              --exclude='/mnt/*' --exclude='/tmp/*' --exclude='/run/*' \
              /mnt/source/ "$target_dir/"
        
        umount /mnt/source
    else
        error "Source root partition $ROOT_SOURCE not found!"
        exit 1
    fi
}

# Generate failsafe fstab
generate_failsafe_fstab() {
    local target_dir="$1"
    
    info "Generating A/B system fstab..."
    cat > "$target_dir/etc/fstab" << EOF
# Steam Deck A/B system fstab
LABEL=rootfs-a / ext4 defaults,noatime 0 1
LABEL=shared-efi /boot vfat defaults,noatime 0 2
LABEL=shared-swap none swap sw 0 0
LABEL=shared-data /home/deck ext4 defaults,noatime 0 2
LABEL=recovery /recovery ext4 defaults,noatime 0 2
EOF
}

# Generate simple fstab
generate_simple_fstab() {
    local target_dir="$1"
    
    info "Generating fstab..."
    cat > "$target_dir/etc/fstab" << EOF
# Steam Deck system fstab
UUID=$(blkid -s UUID -o value "$ROOT_PART") / ext4 defaults,noatime 0 1
UUID=$(blkid -s UUID -o value "$EFI_PART") /boot vfat defaults,noatime 0 2
UUID=$(blkid -s UUID -o value "$SWAP_PART") none swap sw 0 0
EOF
}

# Install failsafe bootloader
install_failsafe_bootloader() {
    local target_dir="$1"
    
    info "Installing failsafe bootloader..."
    
    # Create loader configuration
    mkdir -p "$target_dir/boot/loader/entries"
    
    cat > "$target_dir/boot/loader/loader.conf" << EOF
default steamdeck-a.conf
timeout 5
console-mode max
editor no
EOF
    
    # Find kernel and initrd
    local kernel=$(ls "$target_dir/boot"/vmlinuz-* | head -1 | xargs basename)
    local initrd=$(ls "$target_dir/boot"/initramfs-* | head -1 | xargs basename)
    
    # Create boot entries for A/B slots
    cat > "$target_dir/boot/loader/entries/steamdeck-a.conf" << EOF
title Steam Deck Linux (Slot A)
linux /$kernel
initrd /$initrd
options root=LABEL=rootfs-a rw quiet splash plymouth.ignore-serial-consoles
EOF
    
    cat > "$target_dir/boot/loader/entries/steamdeck-b.conf" << EOF
title Steam Deck Linux (Slot B)
linux /$kernel
initrd /$initrd
options root=LABEL=rootfs-b rw quiet splash plymouth.ignore-serial-consoles
EOF
    
    # Install systemd-boot
    chroot "$target_dir" bootctl install --path=/boot
    
    # Setup dual boot if needed
    if [ "$INSTALL_MODE" = "dualboot" ] && [ "$PRESERVE_STEAMOS" = "1" ]; then
        setup_dual_boot_entries "$target_dir"
    fi
}

# Install simple bootloader
install_simple_bootloader() {
    local target_dir="$1"
    
    info "Installing bootloader..."
    
    mkdir -p "$target_dir/boot/loader/entries"
    
    cat > "$target_dir/boot/loader/loader.conf" << EOF
default steamdeck.conf
timeout 3
console-mode max
editor no
EOF
    
    local kernel=$(ls "$target_dir/boot"/vmlinuz-* | head -1 | xargs basename)
    local initrd=$(ls "$target_dir/boot"/initramfs-* | head -1 | xargs basename)
    
    cat > "$target_dir/boot/loader/entries/steamdeck.conf" << EOF
title Steam Deck Linux
linux /$kernel
initrd /$initrd
options root=UUID=$(blkid -s UUID -o value "$ROOT_PART") rw quiet splash plymouth.ignore-serial-consoles
EOF
    
    chroot "$target_dir" bootctl install --path=/boot
}

# Setup dual boot entries
setup_dual_boot_entries() {
    local target_dir="$1"
    
    info "Setting up dual boot entries..."
    
    # Update timeout for dual boot
    sed -i 's/timeout 5/timeout 10/' "$target_dir/boot/loader/loader.conf"
    
    # Create SteamOS entry
    cat > "$target_dir/boot/loader/entries/steamos.conf" << EOF
title SteamOS
linux /vmlinuz-steamos
initrd /initramfs-steamos
options root=LABEL=steamos-root rw quiet loglevel=3 systemd.unified_cgroup_hierarchy=0 steamos
EOF
    
    # Restore SteamOS if backup exists
    if [ -f "/tmp/steamos-backup/steamos-backup.tar.gz" ]; then
        info "Restoring SteamOS backup..."
        local steamos_mount="/mnt/steamos-restore"
        mkdir -p "$steamos_mount"
        mount "$STEAMOS_PART" "$steamos_mount"
        
        tar -xzf "/tmp/steamos-backup/steamos-backup.tar.gz" -C "$steamos_mount"
        
        umount "$steamos_mount"
        rmdir "$steamos_mount"
        rm -rf "/tmp/steamos-backup"
    fi
}

# Install failsafe tools
install_failsafe_tools() {
    local target_dir="$1"
    
    info "Installing failsafe management tools..."
    
    # Create A/B configuration directory
    mkdir -p "$target_dir/etc/steamdeck-ab"
    echo "a" > "$target_dir/etc/steamdeck-ab/current-slot"
    echo "0" > "$target_dir/etc/steamdeck-ab/boot-count"
    echo "MAX_BOOT_ATTEMPTS=3" > "$target_dir/etc/steamdeck-ab/config"
    
    # Enable failsafe services
    chroot "$target_dir" systemctl enable ab-manager.service
    chroot "$target_dir" systemctl enable ota-update.service
}

# Setup recovery partition
setup_recovery_partition() {
    info "Setting up recovery partition..."
    
    local recovery_mount="/mnt/recovery-setup"
    mkdir -p "$recovery_mount"
    mount "$RECOVERY_PART" "$recovery_mount"
    
    # Create recovery directory structure
    mkdir -p "$recovery_mount/backups"
    mkdir -p "$recovery_mount/tools"
    mkdir -p "$recovery_mount/logs"
    
    # Create recovery info
    cat > "$recovery_mount/recovery-info.txt" << EOF
Steam Deck Recovery Partition
============================

This partition contains:
- System backups in /backups/
- Recovery tools in /tools/
- System logs in /logs/

To access recovery tools, boot from USB and run:
  steamdeck-recovery menu

For emergency repair:
  steamdeck-recovery repair
EOF
    
    umount "$recovery_mount"
    rmdir "$recovery_mount"
}

# Setup data partition
setup_data_partition() {
    info "Setting up shared data partition..."
    
    local data_mount="/mnt/data-setup"
    mkdir -p "$data_mount"
    mount "$DATA_PART" "$data_mount"
    
    # Create user directory structure
    mkdir -p "$data_mount/deck"
    mkdir -p "$data_mount/deck/.steam"
    mkdir -p "$data_mount/deck/Games"
    mkdir -p "$data_mount/deck/Documents"
    
    # Set permissions
    chown -R 1000:1000 "$data_mount/deck"
    
    umount "$data_mount"
    rmdir "$data_mount"
}

# Final system configuration
configure_system() {
    log "Configuring system..."
    
    # Mount for final configuration
    mount "$ROOT_A_PART" "$MOUNT_BASE"
    
    # Set hostname
    echo "steamdeck" > "$MOUNT_BASE/etc/hostname"
    
    # Create default user if not exists
    if ! chroot "$MOUNT_BASE" id deck >/dev/null 2>&1; then
        info "Creating default user 'deck'..."
        chroot "$MOUNT_BASE" useradd -m -G wheel,audio,video,input deck
        echo "deck:deck" | chroot "$MOUNT_BASE" chpasswd
    fi
    
    # Enable sudo for wheel group
    echo "%wheel ALL=(ALL) NOPASSWD: ALL" >> "$MOUNT_BASE/etc/sudoers"
    
    # Enable Steam Deck services
    chroot "$MOUNT_BASE" systemctl enable steamdeck-fan-control || true
    chroot "$MOUNT_BASE" systemctl enable steamdeck-power-management || true
    
    # Disable installer service
    chroot "$MOUNT_BASE" systemctl disable steamdeck-installer || true
    
    # Create version file
    echo "1.0.0-$(date +%Y%m%d)" > "$MOUNT_BASE/etc/steamdeck-version"
    
    umount "$MOUNT_BASE"
}

# Main installation process
main() {
    log "Starting Steam Deck installation..."
    log "Target: $TARGET_DISK"
    log "Mode: $INSTALL_MODE"
    
    create_partitions
    format_partitions
    install_system
    configure_system
    
    log "Installation completed successfully!"
    
    case "$INSTALL_MODE" in
        "failsafe")
            info "Failsafe A/B system installed"
            info "- Automatic rollback on boot failure"
            info "- OTA updates with safety checks"
            info "- Recovery partition available"
            ;;
        "dualboot")
            info "Dual boot system installed"
            info "- Steam Deck Linux with A/B failsafe"
            info "- SteamOS preserved (if found)"
            info "- Boot menu timeout: 10 seconds"
            ;;
        "simple")
            info "Simple system installed"
            info "- Single root partition"
            info "- Standard boot configuration"
            ;;
    esac
    
    info "You can now remove the USB installer and reboot."
    
    if [ "$INSTALL_MODE" != "simple" ]; then
        info ""
        info "Failsafe commands:"
        info "  steamdeck-ab-manager status    - Show A/B status"
        info "  steamdeck-ota-update          - Check for updates"
        info "  steamdeck-recovery menu       - Recovery tools"
    fi
}

# Run main installation
main

exit 0 
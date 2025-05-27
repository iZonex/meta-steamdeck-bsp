#!/bin/bash
#
# Steam Deck System Installation Script
# Installs system to internal SSD
#

set -e

TARGET_DISK="$1"
INSTALL_TYPE="${2:-minimal}"

# Configuration
EFI_SIZE="512M"
SWAP_SIZE="4G"  # For hibernation support
ROOT_SOURCE="/dev/sda2"  # Assume USB installer root is on sda2
MOUNT_BASE="/mnt/target"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
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

# Check parameters
if [ -z "$TARGET_DISK" ]; then
    error "Usage: $0 <target_disk> [install_type]"
    error "Example: $0 /dev/nvme0n1 gaming"
    exit 1
fi

if [ ! -b "$TARGET_DISK" ]; then
    error "Target disk $TARGET_DISK does not exist!"
    exit 1
fi

log "Installing Steam Deck system to $TARGET_DISK"
log "Installation type: $INSTALL_TYPE"

# Unmount any existing partitions
log "Unmounting existing partitions..."
umount ${TARGET_DISK}* 2>/dev/null || true

# Create partition table
log "Creating partition table..."
parted -s "$TARGET_DISK" mklabel gpt

# Calculate sizes based on disk size
DISK_SIZE=$(lsblk -bndo SIZE "$TARGET_DISK")
DISK_SIZE_GB=$((DISK_SIZE / 1024 / 1024 / 1024))

case "$INSTALL_TYPE" in
    "minimal")
        ROOT_SIZE="8G"
        ;;
    "gaming")
        ROOT_SIZE="16G"
        ;;
    "custom")
        # Use most of the disk for root
        ROOT_SIZE=$((DISK_SIZE_GB - 8))G  # Leave 8GB for EFI, swap, etc.
        ;;
    *)
        ROOT_SIZE="8G"
        ;;
esac

log "Disk size: ${DISK_SIZE_GB}GB"
log "Root partition size: $ROOT_SIZE"

# Create partitions
log "Creating EFI partition (${EFI_SIZE})..."
parted -s "$TARGET_DISK" mkpart primary fat32 1MiB "$EFI_SIZE"
parted -s "$TARGET_DISK" set 1 esp on

log "Creating swap partition (${SWAP_SIZE})..."
SWAP_START=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
parted -s "$TARGET_DISK" mkpart primary linux-swap "$SWAP_START" "+${SWAP_SIZE}"

log "Creating root partition (${ROOT_SIZE})..."
ROOT_START=$(parted -s "$TARGET_DISK" print | grep "primary" | tail -1 | awk '{print $3}')
parted -s "$TARGET_DISK" mkpart primary ext4 "$ROOT_START" "100%"

# Wait for kernel to detect new partitions
sleep 2
partprobe "$TARGET_DISK"
sleep 2

# Determine partition names
if [[ "$TARGET_DISK" =~ "nvme" ]]; then
    EFI_PART="${TARGET_DISK}p1"
    SWAP_PART="${TARGET_DISK}p2"
    ROOT_PART="${TARGET_DISK}p3"
else
    EFI_PART="${TARGET_DISK}1"
    SWAP_PART="${TARGET_DISK}2"
    ROOT_PART="${TARGET_DISK}3"
fi

log "Partitions created:"
log "  EFI:  $EFI_PART"
log "  Swap: $SWAP_PART"
log "  Root: $ROOT_PART"

# Format partitions
log "Formatting EFI partition..."
mkfs.fat -F32 -n "EFI" "$EFI_PART"

log "Setting up swap..."
mkswap -L "swap" "$SWAP_PART"

log "Formatting root partition..."
mkfs.ext4 -F -L "root" "$ROOT_PART"

# Mount partitions
log "Mounting partitions..."
mkdir -p "$MOUNT_BASE"
mount "$ROOT_PART" "$MOUNT_BASE"
mkdir -p "$MOUNT_BASE/boot"
mount "$EFI_PART" "$MOUNT_BASE/boot"

# Copy system
log "Copying system files..."
if [ -b "$ROOT_SOURCE" ]; then
    # Mount source
    mkdir -p /mnt/source
    mount "$ROOT_SOURCE" /mnt/source
    
    # Copy with progress
    log "This may take several minutes..."
    rsync -av --progress --exclude='/proc/*' --exclude='/sys/*' --exclude='/dev/*' \
          --exclude='/mnt/*' --exclude='/tmp/*' --exclude='/run/*' \
          /mnt/source/ "$MOUNT_BASE/"
    
    umount /mnt/source
else
    error "Source root partition $ROOT_SOURCE not found!"
    exit 1
fi

# Generate fstab
log "Generating fstab..."
cat > "$MOUNT_BASE/etc/fstab" << EOF
# Steam Deck system fstab
UUID=$(blkid -s UUID -o value "$ROOT_PART") / ext4 defaults,noatime 0 1
UUID=$(blkid -s UUID -o value "$EFI_PART") /boot vfat defaults,noatime 0 2
UUID=$(blkid -s UUID -o value "$SWAP_PART") none swap sw 0 0
EOF

# Install bootloader
log "Installing systemd-boot..."
mkdir -p "$MOUNT_BASE/boot/loader/entries"

# Create loader configuration
cat > "$MOUNT_BASE/boot/loader/loader.conf" << EOF
default steamdeck.conf
timeout 3
console-mode max
editor no
EOF

# Find kernel and initrd
KERNEL=$(ls "$MOUNT_BASE/boot"/vmlinuz-* | head -1 | xargs basename)
INITRD=$(ls "$MOUNT_BASE/boot"/initramfs-* | head -1 | xargs basename)

# Create boot entry
cat > "$MOUNT_BASE/boot/loader/entries/steamdeck.conf" << EOF
title Steam Deck Linux
linux /$KERNEL
initrd /$INITRD
options root=UUID=$(blkid -s UUID -o value "$ROOT_PART") rw quiet splash plymouth.ignore-serial-consoles
EOF

# Install systemd-boot to EFI
chroot "$MOUNT_BASE" bootctl install --path=/boot

# Update Steam Deck specific configuration
log "Configuring Steam Deck settings..."

# Enable steamdeck services
chroot "$MOUNT_BASE" systemctl enable steamdeck-fan-control
chroot "$MOUNT_BASE" systemctl enable steamdeck-power-management

# Set hostname
echo "steamdeck" > "$MOUNT_BASE/etc/hostname"

# Create default user
log "Creating default user 'deck'..."
chroot "$MOUNT_BASE" useradd -m -G wheel,audio,video,input deck
echo "deck:deck" | chroot "$MOUNT_BASE" chpasswd

# Enable sudo for wheel group
echo "%wheel ALL=(ALL) NOPASSWD: ALL" >> "$MOUNT_BASE/etc/sudoers"

# Disable installer service (if exists)
chroot "$MOUNT_BASE" systemctl disable steamdeck-installer || true

# Final touches
log "Finalizing installation..."
sync

# Unmount
log "Unmounting partitions..."
umount "$MOUNT_BASE/boot"
umount "$MOUNT_BASE"

log "Installation completed successfully!"
log "Target disk: $TARGET_DISK"
log "You can now remove the USB installer and reboot."

exit 0 
#!/bin/bash
#
# Steam Deck BSP Dependencies Installer
# Automatically detects Ubuntu version and installs correct packages
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}Steam Deck BSP Dependencies Installer${NC}"
echo "====================================="

# Detect OS
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
    VER=$VERSION_ID
else
    echo -e "${RED}Cannot detect OS version${NC}"
    exit 1
fi

echo "Detected OS: $OS $VER"

# Update package lists
echo -e "${YELLOW}Updating package lists...${NC}"
sudo apt-get update

# Base packages for all Ubuntu versions
BASE_PACKAGES="gawk wget git diffstat unzip texinfo gcc build-essential \
chrpath socat cpio python3 python3-pip python3-pexpect \
xz-utils debianutils iputils-ping python3-git python3-jinja2 \
libsdl1.2-dev python3-subunit mesa-common-dev \
zstd liblz4-tool file locales libacl1"

# Version-specific packages
if [[ "$OS" == *"Ubuntu"* ]]; then
    case "$VER" in
        "24.04"|"24.10")
            echo -e "${GREEN}Installing dependencies for Ubuntu 24.04+...${NC}"
            EGL_PACKAGE="libegl1-mesa-dev"
            PYLINT_PACKAGE="pylint"
            ;;
        "22.04"|"23.04"|"23.10")
            echo -e "${GREEN}Installing dependencies for Ubuntu 22.04/23.x...${NC}"
            EGL_PACKAGE="libegl1-mesa"
            PYLINT_PACKAGE="pylint3"
            ;;
        *)
            echo -e "${YELLOW}Unknown Ubuntu version, using modern package names...${NC}"
            EGL_PACKAGE="libegl1-mesa-dev"
            PYLINT_PACKAGE="pylint"
            ;;
    esac
elif [[ "$OS" == *"Debian"* ]]; then
    echo -e "${GREEN}Installing dependencies for Debian...${NC}"
    EGL_PACKAGE="libegl1-mesa-dev"
    PYLINT_PACKAGE="pylint"
else
    echo -e "${YELLOW}Non-Ubuntu/Debian system detected, using standard package names...${NC}"
    EGL_PACKAGE="libegl1-mesa-dev"
    PYLINT_PACKAGE="pylint"
fi

# Install packages
echo -e "${YELLOW}Installing packages...${NC}"
if sudo apt-get install -y $BASE_PACKAGES $EGL_PACKAGE $PYLINT_PACKAGE; then
    echo -e "${GREEN}✅ All dependencies installed successfully!${NC}"
else
    echo -e "${RED}❌ Failed to install some packages${NC}"
    echo ""
    echo "You can try manual installation:"
    echo "sudo apt-get install -y $BASE_PACKAGES $EGL_PACKAGE $PYLINT_PACKAGE"
    exit 1
fi

# Verify installation
echo -e "${YELLOW}Verifying installation...${NC}"
MISSING_PACKAGES=""

# Check critical packages
for pkg in python3 git gcc make; do
    if ! command -v $pkg >/dev/null 2>&1; then
        MISSING_PACKAGES="$MISSING_PACKAGES $pkg"
    fi
done

if [ -n "$MISSING_PACKAGES" ]; then
    echo -e "${RED}❌ Missing critical packages:$MISSING_PACKAGES${NC}"
    exit 1
else
    echo -e "${GREEN}✅ All critical packages verified!${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Steam Deck BSP dependencies installed successfully!${NC}"
echo ""

# Check for Ubuntu 24.04 user namespaces issue
if [[ "$OS" == *"Ubuntu"* ]] && [[ "$VER" == "24.04" || "$VER" == "24.10" ]]; then
    echo -e "${YELLOW}⚠️  Ubuntu 24.04 Notice:${NC}"
    echo "Ubuntu 24.04 has restricted user namespaces that may cause BitBake errors."
    echo "If you encounter 'User namespaces are not usable' errors, run:"
    echo ""
    echo -e "${BLUE}  export PSEUDO_DISABLED=1${NC}"
    echo -e "${BLUE}  echo 'BB_NO_NETWORK = \"1\"' >> conf/local.conf${NC}"
    echo ""
    echo "See BUILD.md for more solutions."
    echo ""
fi

echo "Next steps:"
echo "1. Clone Yocto: git clone -b scarthgap git://git.yoctoproject.org/poky"
echo "2. Clone BSP: git clone https://github.com/iZonex/meta-steamdeck-bsp.git"
echo "3. Follow BUILD.md for complete instructions" 
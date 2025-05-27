# Contributing to meta-steamdeck-bsp

Thank you for your interest in contributing to the Steam Deck BSP project! 🎮

## 🔗 Repository
**GitHub:** [https://github.com/iZonex/meta-steamdeck-bsp](https://github.com/iZonex/meta-steamdeck-bsp)

## 🚀 Quick Start for Contributors

### 1. Fork and Clone
```bash
# Fork the repository on GitHub, then clone your fork
git clone https://github.com/YOUR_USERNAME/meta-steamdeck-bsp.git
cd meta-steamdeck-bsp

# Add upstream remote
git remote add upstream https://github.com/iZonex/meta-steamdeck-bsp.git
```

### 2. Development Setup
```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Set up Yocto build environment (see BUILD.md for details)
mkdir ../steamdeck-build && cd ../steamdeck-build
git clone -b scarthgap git://git.yoctoproject.org/poky
cd poky
ln -s ../../meta-steamdeck-bsp .
source oe-init-build-env build-steamdeck
```

### 3. Making Changes
```bash
# Make your changes in meta-steamdeck-bsp/
# Test your changes by building
bitbake steamdeck-minimal-image

# Commit with descriptive messages
git add .
git commit -m "Add: New feature for Steam Deck OLED display optimization"
git push origin feature/your-feature-name
```

### 4. Submit Pull Request
1. Go to GitHub and create a Pull Request
2. Describe your changes thoroughly
3. Reference any related issues

## 📝 Contribution Guidelines

### What We Accept
- **Bug fixes** - Hardware support improvements
- **New hardware support** - Additional Steam Deck variants
- **Performance optimizations** - Gaming/power management improvements  
- **Documentation** - Improvements to guides and README
- **Testing** - Hardware validation on different Steam Deck models
- **Installer improvements** - Better user experience

### Coding Standards
- **BitBake recipes** - Follow Yocto Project conventions
- **Shell scripts** - Use bash with proper error handling
- **Configuration files** - Clear comments explaining purpose
- **Documentation** - Update relevant .md files with changes

### Testing Requirements
- Test on actual Steam Deck hardware when possible
- Verify installer functionality
- Ensure all images build successfully
- Document tested configurations

## 🐛 Reporting Issues

### Before Reporting
1. Check existing issues on GitHub
2. Test with latest main branch
3. Try with clean build environment

### Issue Template
```markdown
**Hardware:** Steam Deck OLED/LCD
**Image:** steamdeck-minimal-image / steamdeck-image / steamdeck-installer-image
**Host OS:** Ubuntu 22.04 / Fedora 38 / etc.
**Yocto Version:** Scarthgap (5.0)

**Description:**
[Clear description of the issue]

**Steps to Reproduce:**
1. Step one
2. Step two
3. Step three

**Expected Behavior:**
[What should happen]

**Actual Behavior:**
[What actually happens]

**Build Logs:**
[Relevant error messages or logs]
```

## 🔧 Development Areas

### Priority Areas
- **Hardware support** - WiFi, Bluetooth, sensors
- **Performance tuning** - Gaming optimizations
- **Power management** - Battery life improvements
- **Display optimization** - OLED-specific features
- **Audio enhancements** - Spatial audio, EQ presets

### Architecture
```
meta-steamdeck-bsp/
├── conf/                    # Layer and machine configuration
├── recipes-kernel/          # Custom kernel with Steam Deck patches
├── recipes-steamdeck/       # Steam Deck specific packages
├── recipes-core/           # System images
└── wic/                    # Disk layout configuration
```

## 📚 Resources

### Documentation
- [Yocto Project Manual](https://docs.yoctoproject.org/)
- [BitBake User Manual](https://docs.yoctoproject.org/bitbake/)
- [Steam Deck Developer Documentation](https://partner.steamgames.com/doc/steamdeck)

### Steam Deck Hardware
- **CPU:** AMD Zen 2 4-core/8-thread (Van Gogh APU)
- **GPU:** AMD RDNA 2 (8 CUs, 1.0-1.6 GHz)
- **RAM:** 16 GB LPDDR5
- **Storage:** NVMe SSD (64/256/512 GB models)
- **Display:** 7" OLED (1280x800, 90Hz for OLED model)

## 🤝 Community

### Communication
- **Issues:** GitHub Issues for bug reports and feature requests
- **Discussions:** GitHub Discussions for general questions
- **Pull Requests:** For code contributions

### Code of Conduct
- Be respectful and inclusive
- Focus on constructive feedback
- Help newcomers to the project
- Keep discussions on-topic

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

---

**Happy hacking!** 🚀 
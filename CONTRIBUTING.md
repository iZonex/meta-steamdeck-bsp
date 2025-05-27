# Contributing to Steam Deck OLED BSP

Thank you for your interest in contributing to the Steam Deck OLED BSP project! This document provides guidelines for contributing to the project.

## How to Contribute

### 1. Creating Issues

Before starting work, create an issue for:
- Bugs and problems
- New features
- Code improvements
- Documentation questions

### 2. Fork and Clone

```bash
# Fork the repository through GitHub UI, then clone
git clone https://github.com/YOUR_USERNAME/meta-steamdeck-bsp.git
cd meta-steamdeck-bsp

# Add upstream remote
git remote add upstream https://github.com/iZonex/meta-steamdeck-bsp.git
```

### 3. Create Branch

```bash
# Update main branch
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/bug-description
```

### 4. Code Standards

#### BitBake Recipes

- Use proper header format:
  ```bitbake
  SUMMARY = "Brief package description"
  DESCRIPTION = "Detailed functionality description"
  HOMEPAGE = "https://github.com/iZonex/meta-steamdeck-bsp"
  BUGTRACKER = "https://github.com/iZonex/meta-steamdeck-bsp/issues"
  LICENSE = "MIT"
  LIC_FILES_CHKSUM = "file://LICENSE;md5=..."
  ```

- Use proper dependencies:
  ```bitbake
  RDEPENDS:${PN} = "dependency1 dependency2"
  DEPENDS = "build-dependency1 build-dependency2"
  ```

#### Shell Scripts

- Use `#!/bin/bash` as shebang
- Add `set -e` for error handling
- Quote variables: `"$variable"`
- Add comments for complex logic

#### Python Scripts

- Follow PEP 8 style
- Use type hints where possible
- Add docstrings to functions

### 5. Testing

#### Local Testing

```bash
# Test shell script syntax
find . -name "*.sh" -type f | xargs shellcheck

# Test recipe parsing
bitbake -p  # Parse recipes
bitbake steamdeck-minimal-image  # Test build
```

#### CI/CD Testing

All pull requests are automatically tested through GitHub Actions:
- Shell script syntax checking
- BitBake recipe validation
- Documentation structure verification
- Test image builds

### 6. Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: new feature
- `fix`: bug fix
- `docs`: documentation changes
- `style`: code formatting (no logic changes)
- `refactor`: code refactoring
- `test`: adding tests
- `chore`: build process changes

Examples:
```
feat(failsafe): add automatic rollback on boot failure

fix(installer): resolve partition detection issue on some devices

docs(readme): update installation instructions

chore(ci): update GitHub Actions to use Node 18
```

### 7. Pull Request Process

1. Ensure your branch is up to date:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. Push your changes:
   ```bash
   git push origin feature/your-feature-name
   ```

3. Create Pull Request through GitHub UI

4. Fill out PR template with:
   - Description of changes
   - Related issues
   - Testing results
   - Screenshots (if applicable)

### 8. Code Review

- Respond to comments in a timely manner
- Make requested changes
- Update PR after rebase if needed

### 9. Specific Areas

#### Failsafe System

When working with A/B system:
- Test rollback functionality
- Verify both slot operations
- Ensure user data preservation

#### Installer

When modifying installer:
- Test all installation modes
- Verify hardware detection
- Validate partition creation

#### Kernel/Hardware

When changing kernel configuration:
- Test on real hardware if possible
- Document device tree changes
- Check driver compatibility

### 10. Documentation

When adding new features:
- Update README.md
- Add usage examples
- Update CHANGELOG.md
- Document new commands

### 11. Release Process

Maintainers follow this process for releases:

1. Update CHANGELOG.md
2. Create version tag
3. Automatic build through GitHub Actions
4. Create GitHub Release with artifacts

## Project Structure

```
meta-steamdeck-bsp/
├── conf/                          # Layer configuration
├── recipes-bsp/                   # BSP-specific packages
├── recipes-core/                  # System images
├── recipes-kernel/                # Kernel and modules
├── recipes-steamdeck/             # Steam Deck utilities
│   ├── steamdeck-failsafe/       # A/B system
│   ├── steamdeck-installer/      # Installer
│   └── steamdeck-tools/          # Hardware utilities
├── wic/                          # WIC configurations
├── .github/workflows/            # CI/CD
└── docs/                         # Documentation
```

## Getting Help

- Create an issue for questions
- Refer to existing code as examples
- Study [Yocto Project documentation](https://docs.yoctoproject.org/)

Thank you for contributing to the Steam Deck Linux ecosystem!

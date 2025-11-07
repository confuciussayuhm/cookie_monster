# GitHub Actions Workflows

This project uses GitHub Actions to automatically build and release the Cookie Monster extension.

## Available Workflows

### 1. Build and Test (`build.yml`)

**Triggers:** Automatically on push and pull requests to main/master/develop branches

**Purpose:** Validates that the code compiles successfully and creates a build artifact.

**What it does:**
- Sets up Java 11
- Downloads Burp Suite to extract Montoya API
- Compiles the extension
- Creates the JAR file
- Uploads the JAR as a workflow artifact (retained for 30 days)

**Viewing artifacts:**
1. Go to Actions tab in GitHub
2. Click on a completed workflow run
3. Scroll to "Artifacts" section
4. Download `cookie-monster-jar`

### 2. Create Release (`release.yml`)

**Triggers:** Automatically when you push a version tag (e.g., `v1.1.0`, `v2.0.0`)

**Purpose:** Creates a GitHub Release with the JAR file attached.

**How to use:**
```bash
# Create and push a version tag
git tag v1.1.0
git push origin v1.1.0
```

**What it does:**
- Extracts version from tag
- Builds the extension
- Generates release notes
- Creates a GitHub Release
- Attaches the JAR file to the release

**Release naming:** `cookie-monster-{version}.jar` (e.g., `cookie-monster-1.1.0.jar`)

### 3. Manual Release (`manual-release.yml`)

**Triggers:** Manually via GitHub Actions UI

**Purpose:** Create a release without needing to push a tag first.

**How to use:**
1. Go to Actions tab in GitHub
2. Select "Manual Release" workflow
3. Click "Run workflow"
4. Enter the version number (e.g., `1.1.0`)
5. Optionally mark as pre-release
6. Click "Run workflow"

**What it does:**
- Builds with specified version
- Creates a git tag (if it doesn't exist)
- Creates a GitHub Release
- Attaches the JAR file

## Quick Start Guide

### First Release

**Option A: Using Git Tags (Recommended)**
```bash
# Make sure all changes are committed
git add .
git commit -m "Release v1.1.0"
git push

# Create and push a version tag
git tag v1.1.0
git push origin v1.1.0

# GitHub Actions will automatically create the release
```

**Option B: Manual Trigger**
1. Push your changes to GitHub
2. Go to Actions → Manual Release
3. Click "Run workflow"
4. Enter version: `1.1.0`
5. Click "Run workflow"

### Subsequent Releases

```bash
# Update version in pom.xml if needed
# Commit your changes
git add .
git commit -m "Release v1.2.0"
git push

# Tag and release
git tag v1.2.0
git push origin v1.2.0
```

## Version Numbering

Follow semantic versioning: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

Examples:
- `v1.0.0` - Initial release
- `v1.1.0` - Added domain filtering
- `v1.1.1` - Bug fixes
- `v2.0.0` - Breaking API changes

## Troubleshooting

### Build fails with "Montoya API not found"

The workflows download Burp Suite to extract the Montoya API. If this fails:

1. **Option 1:** Add Montoya API to your repository
   - Create `lib/` directory
   - Add Montoya API JAR to `lib/montoya-api.jar`
   - Update workflows to use this JAR

2. **Option 2:** Use a direct download link
   - Find a stable Burp Suite download URL
   - Update the `wget` command in workflows

### Release not created

Check:
1. Repository has "Actions" enabled (Settings → Actions → General)
2. Workflow has `contents: write` permission
3. Tag follows the pattern `v*.*.*`
4. No existing release with the same tag

### JAR file not attached to release

Check the workflow logs:
1. Go to Actions tab
2. Click on the failed workflow
3. Review the "Create GitHub Release" step
4. Ensure the JAR file was created in the "Build extension" step

## Testing Workflows

### Test build without creating a release:

```bash
# Push to a feature branch
git checkout -b test-build
git push origin test-build

# Create a pull request
# The build workflow will run automatically
```

### Test release locally before pushing:

```bash
# Build locally
javac -cp target/classes -d target/classes src/main/java/burp/cookiemonster/*.java
cd target/classes && jar cvf ../cookie-monster-test.jar burp/cookiemonster/*.class

# Test the JAR in Burp Suite
# If successful, create the release
```

## Advanced Configuration

### Customize release notes

Edit the `Generate release notes` step in `release.yml` or `manual-release.yml`

### Change Java version

Update the `Set up JDK` step:
```yaml
- name: Set up JDK 11
  uses: actions/setup-java@v4
  with:
    java-version: '17'  # Change to desired version
    distribution: 'temurin'
```

### Add build notifications

Add a notification step at the end of workflows:
```yaml
- name: Notify on completion
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    text: 'Build completed!'
```

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Creating Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
- [Semantic Versioning](https://semver.org/)

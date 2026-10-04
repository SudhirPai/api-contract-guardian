# ==============================================================================
# 🛠️ IDE & Editors
# ==============================================================================
# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr
out/

# Visual Studio Code
.vscode/
!.vscode/settings.json
!.vscode/tasks.json
!.vscode/launch.json
!.vscode/extensions.json
.history/
*.code-workspace

# General Editors & OS Files
.DS_Store
Thumbs.db
*.swp
*.bak
*~


# ==============================================================================
# ☕ Backend (Spring Boot + Maven)
# ==============================================================================
# Build outputs
target/
*.jar
*.war
*.ear
*.zip
*.tar.gz

# Maven logs and local repo overrides
pom.xml.tag
pom.xml.releaseBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/.maven-wrapper.jar

# Crash logs
hs_err_pid*
replay_pid*


# ==============================================================================
# ⚛️ Frontend (React / Node.js)
# ==============================================================================
# Dependencies
node_modules/
jspm_packages/

# Build outputs
build/
dist/
.next/
out/

# Frontend testing & logs
npm-debug.log*
yarn-debug.log*
yarn-error.log*
.pnpm-debug.log*
.eslintcache
.stylelintcache
.cache/
coverage/


# ==============================================================================
# 🐳 Docker & DevOps
# ==============================================================================
# Docker volumes / local data persistence
.docker/
docker-volumes/


# ==============================================================================
# 🗄️ Database & Local Data
# ==============================================================================
# Local DB storage (adjust if your folder name differs)
.db-data/
postgres-data/
mysql-data/
*.db
*.sqlite

# =============================================================================
# OWASP Dependency Check Configuration
# =============================================================================
# Purpose: Suppression file and config for known false positives in SCA.
# Integration: Runs as part of Maven build in Jenkins pipeline.
# =============================================================================

# This directory contains configuration for OWASP Dependency Check.
# The main suppressions file is at the project root:
#   dependency-check-suppressions.xml
#
# Additional configuration:
#   - failBuildOnCVSS: 7 (configured in pom.xml)
#   - Output formats: XML, HTML (configured in pom.xml)
#   - NVD API key: stored as Jenkins credential 'nvd-api-key'
#
# Pipeline integration:
#   mvn org.owasp:dependency-check-maven:check \
#     -DfailOnError=true \
#     -DfailBuildOnCVSS=7 \
#     -Dformat=ALL \
#     -DsuppressionFiles=dependency-check-suppressions.xml \
#     -DnvdApiKey=${NVD_API_KEY}

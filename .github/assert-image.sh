#!/bin/sh
# =============================================================================
# Runs INSIDE the built image, fed over stdin:
#
#   docker run --rm -i --entrypoint sh <image> -s < .github/assert-image.sh
#
# A published image that contains a credential cannot be un-published —
# deleting the file in a later layer does not remove it from the earlier one.
# So this fails the build rather than letting it ship.
#
# .dockerignore is the guard; this is the check that the guard still works.
# They are deliberately separate: a typo in .dockerignore is invisible, a
# failing assertion is not.
#
# It lives in a file rather than inline in the workflow because the quoting
# defeated it otherwise, and because it can then be run by hand against a
# local build:
#
#   docker build -t kdc:test .
#   docker run --rm -i --entrypoint sh kdc:test -s < .github/assert-image.sh
# =============================================================================
fail=0

# --- /app holds the artefact and nothing else --------------------------------
stray=$(find /app -mindepth 1 ! -name app.jar)
if [ -n "$stray" ]; then
  echo "FAIL: unexpected files in /app:"
  echo "$stray"
  fail=1
fi

# --- no .env anywhere --------------------------------------------------------
# This one stays filesystem-wide: a .env has no legitimate place in any layer.
if find / -xdev -name ".env" -not -path "/proc/*" 2>/dev/null | grep -q .; then
  echo "FAIL: a .env file is present in the image"
  fail=1
fi

# --- not root ----------------------------------------------------------------
if [ "$(id -u)" = "0" ]; then
  echo "FAIL: the image runs as root"
  fail=1
fi

# --- key material, scoped to our own paths -----------------------------------
# Scope matters. The base image ships the system CA trust store as about 130
# *.pem files under /etc/ssl, which must be there — a filesystem-wide *.pem
# search therefore fails on every single build, and a check that always fails
# is a check everybody learns to ignore. So look where our files actually go:
# /app, and inside the artefact.
#
# BOOT-INF/classes/keys/ is where src/main/resources/keys/ lands. That is the
# directory the committed server_private.pem came out of. It is NOT the same
# as BOOT-INF/classes/.../cryptography/keys/, which is a package of compiled
# Java classes and legitimately present.
inside=$(unzip -Z1 /app/app.jar 2>/dev/null | grep -Ei \
  "^BOOT-INF/classes/keys/|\.pem$|\.jks$|\.p12$|\.keystore$|(^|/)\.env$")
if [ -n "$inside" ]; then
  echo "FAIL: key material or .env inside the jar:"
  echo "$inside"
  fail=1
fi

host_side=$(find /app -type f \( -name "*.pem" -o -name "*.jks" -o -name "*.p12" \
  -o -name "*.keystore" \) 2>/dev/null)
if [ -n "$host_side" ]; then
  echo "FAIL: key material alongside the jar:"
  echo "$host_side"
  fail=1
fi

# --- no literal database password in the packaged configuration --------------
# The published default used to sit in application.properties, which is
# packaged into the jar. Assert it is gone from the artefact, not merely from
# the working tree.
#
# The wanted value is a bare placeholder with an empty fallback. Anything else
# after the "=" is a literal and fails.
line=$(unzip -p /app/app.jar BOOT-INF/classes/application.properties 2>/dev/null |
  grep -E "^spring\.datasource\.password=" | tr -d "\r")
expected="spring.datasource.password=\${SPRING_DATASOURCE_PASSWORD:}"
if [ -n "$line" ] && [ "$line" != "$expected" ]; then
  echo "FAIL: application.properties does not read the password from the environment"
  echo "      found: spring.datasource.password=<redacted, ${#line} chars on the line>"
  fail=1
fi

if [ "$fail" = "0" ]; then
  echo "OK: no secrets in the image, runs as uid $(id -u)"
fi
exit "$fail"

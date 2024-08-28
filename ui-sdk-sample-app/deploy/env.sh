#!/bin/sh
SSHPASS=/proj/PDU_OSS_CI_TAF/tools/sshpass-1.05/sshpass

case "$ENV" in
prod )
  HOST=atvts2839.athtem.eei.ericsson.se
  USER=root
  PASSWORD=shroot
  WEBAPP_DIR=/tmp/sample-app
  ;;
*)
  echo "Invalid environment: '$ENV'"
  exit 1
esac

echo "
Deployment environment:
HOST=${HOST}
USER=${USER}
WEBAPP_DIR=${WEBAPP_DIR}
"

#!/bin/sh
source `dirname $0`/env.sh
SERVER_ARTIFACT=ui-sdk-sample-app-server
SSH_FLAGS="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"

echo "Preparing to upload artifacts..."
${SSHPASS} -p "$PASSWORD" ssh ${USER}@${HOST} ${SSH_FLAGS} <<EOF
rm -rf ${WEBAPP_DIR}/responses
rm -rf ${WEBAPP_DIR}/${SERVER_ARTIFACT}.jar

mkdir -p ${WEBAPP_DIR}/responses
EOF

echo "Uploading artifacts..."
${SSHPASS} -p "$PASSWORD" scp ${SSH_FLAGS} ui-sdk-sample-app/server/target/${SERVER_ARTIFACT}.jar ${USER}@${HOST}:${WEBAPP_DIR}/${SERVER_ARTIFACT}.jar
${SSHPASS} -p "$PASSWORD" scp ${SSH_FLAGS} ui-sdk-sample-app/server/target/classes/responses/* ${USER}@${HOST}:${WEBAPP_DIR}/responses


echo "Running application..."
${SSHPASS} -p "$PASSWORD" ssh ${SSH_FLAGS} -t -t ${USER}@${HOST} <<EOF
cd ${WEBAPP_DIR}

kill \$(ps aux | grep '[u]i-sdk-sample-app-server' | awk '{print \$2}')

java -jar -Dyaml=responses/response-mocks.yaml ui-sdk-sample-app-server.jar & disown

exit
EOF

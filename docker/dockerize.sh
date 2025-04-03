#!/usr/bin/env bash

HUB='hub.docker.com'  # docker.io
HUB_USER='lamistage'
HUB_PASSWORD="ch=Y,E+JKt)g7Q6"
PROJECT='pick-me-backend'

DIR="`dirname "$(readlink -f "$0")"`"
#PARENT_DIR="`dirname "$DIR"`"

if [[ -z "${hub}" ]]; then
  hub=$HUB
fi

if [[ -z "${project}" ]]; then
  project=$PROJECT
fi

print() {
  echo ${DIR}
}

build() {
  docker build \
  -t ${hub}/${project}:`cat ${DIR}/version` \
  -f ${DIR}/Dockerfile ${DIR}/..
}

clean() {
  docker rmi ${hub}/${project}:`cat ${DIR}/version`
}

push() {
  docker login $HUB -u $HUB_USER -p $HUB_PASSWORD

  docker push ${hub}/${project}:`cat ${DIR}/version`
}

$1
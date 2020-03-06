#!/usr/bin/env bash

readonly SCDF_BASE_URL="http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch"
readonly DOCKER_REGISTRY="docker-registry.default.svc:5000/jobroom-dev"

function usage() {
    printf "
    This script registeres a SCDF task.\n
    Following parameters must be provided:
            --name    | -n Application name
            --version | -v Application version
            --cron    | -c Cron expression
    Example execution
            ./create-task.sh --name test-name --version 1.1.0 --cron '0_2_*_*_*'\n"
    exit 1
}

opts=$(getopt \
    --options "n:v:c:" \
    --longoptions "name:,version:,cron:" \
    --name "$(basename "$0")" \
    -- "$@"
)

eval set --${opts}

while [[ $# -gt 0 ]]; do
    case "$1" in
        -n|--name)
            NAME=$2
            shift 2
            ;;

        -v|--version)
            VERSION=$2
            shift 2
            ;;

        -c|--cron)
            CRON="$2"
            shift 2
            ;;
        *)
            break
            ;;
    esac
done

if [[ -z ${NAME} || -z ${VERSION} || -z ${CRON} ]]; then
    echo 'Required argument is missing'
    usage
fi

# Delete existing application
curl -k -f -X DELETE "${SCDF_BASE_URL}/apps/task/${NAME}"

# Register new application
curl -k -f -X POST "${SCDF_BASE_URL}/apps/task/${NAME}" \
 --data-urlencode "force=true" \
 --data-urlencode "uri=docker://${DOCKER_REGISTRY}/${NAME}:${VERSION}"

# Create the task associated to the application
curl -k -f -X POST "${SCDF_BASE_URL}/tasks/definitions" \
 --data-urlencode "force=true" \
 --data-urlencode "name=${NAME}-task" \
 --data-urlencode "definition=${NAME}"

# Delete the existing schedule
curl -k -f -X DELETE "${SCDF_BASE_URL}/tasks/schedules/schedule-scdf-${NAME}-task"

# Create new schedule
curl -k -f -X POST "${SCDF_BASE_URL}/tasks/schedules" \
    --data-urlencode "force=true" \
	--data-urlencode "scheduleName=schedule" \
	--data-urlencode "taskDefinitionName=${NAME}-task"  \
	--data-urlencode "properties=scheduler.cron.expression=${CRON//_/ }"

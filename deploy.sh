#!/bin/bash

set -e
if [[ ! -d "$HOME/config" || ! -f "$HOME/.env" ]]; then
  echo "deployment failed"
  echo "there are no required config files"
  exit 1
fi

if ! command -v docker &> /dev/null; then
  echo "installing docker..."
  sudo dnf update -y
  sudo dnf install -y docker

  sudo systemctl start docker
  sudo systemctl enable docker

  echo "installed docker successfully"
fi

PROJECT_DIR="$HOME/ohmygoods"
BACKEND_DIR="$PROJECT_DIR/backend"
BACKEND_DOCKER_COMPOSE_FILE="$BACKEND_DIR/docker-compose-ci.yml"
BACKEND_SERVICE="backend"

if [ ! -d "$PROJECT_DIR" ]; then
  echo "first deployment detected"
  mkdir -p "$BACKEND_DIR"
  FIRST_DEPLOY=true
fi

if [ -f "$BACKEND_DOCKER_COMPOSE_FILE" ]; then
  echo "checking if docker-compose-ci.yml has changed"
  if cmp -s $DOCKER_COMPOSE_FILE $HOME/config/backend/docker-compose-ci.yml; then
    echo "no changes in docker-compose.yml"
    COMPOSE_CHANGED=false
  else
    echo "detected docker-compose.yml changes"
    COMPOSE_CHANGED=true
  fi
fi

echo "updating environment files..."
cp ~/.env $BACKEND_DIR/.env
cp ~/config/backend/docker-compose-ci.yml $BACKEND_DOCKER_COMPOSE_FILE

echo "pulling latest backend docker image..."
cd $BACKEND_DIR
echo $GHCR_TOKEN | docker login ghcr.io -u $GITHUB_ACTOR --password-stdin
docker pull ghcr.io/$GITHUB_REPOSITORY/backend-app:latest

if [ "$FIRST_DEPLOY" ]; then
  echo "starting all services..."
  docker compose up -d
  else
    if [ "$COMPOSE_CHANGED" = true ]; then
      echo "restarting all services..."
      docker compose down
      docker compose up -d
    else
      echo "restarting backend service only..."
      docker compose stop backend
      docker compose rm -f backend
      docker compose up -d backend
    fi
fi

echo "checking all services health..."

for i in {1..3}; do
  sleep 10
  unhealthy_services=$(docker ps --format '{{.Names}} {{.Status}}' | grep -E 'unhealthy|Exited' | awk '{print $1}')
  if [ -z "$unhealthy_services" ]; then
    echo "all services are running successfully"
    exit 0
  fi
  echo "unhealthy services detected: $unhealthy_services"
  echo "restarting failed services..."
  docker-compose restart $unhealthy_services
done

echo "deployment failed after multiple retries!" >&2
exit 1
# Tear down
docker compose down

# Build everything
mvn clean install
docker compose build

# Start Docker containers
docker compose up

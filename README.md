# Azalea

## Operation
Azalea requires a running instance of Redis to perform service discovery.

Redis: `docker run --rm -p 6379:6379 redis`

Docker Compose:
```
docker-compose rm
docker-compose pull
docker-compose --compatibility --abort-on-container-exit up
```

## Build
```
docker login
./gradlew jib --image epelesis/azalea
```

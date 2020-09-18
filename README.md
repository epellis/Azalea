# Azalea

## Operation
Azalea requires a running instance of Redis to perform service discovery.

Redis: `docker run --rm -p 6379:6379 redis`

Docker Compose:
```
docker-compose rm
docker-compose pull
docker-compose --compatibility up
```

View Ports:
```
docker-compose ps
[~/IdeaProjects/Azalea] curl http://0.0.0.0:32769
     Name                    Command               State            Ports         
----------------------------------------------------------------------------------
azalea_azalea_1   java -cp /app/resources:/a ...   Up      0.0.0.0:32769->8000/tcp
azalea_azalea_2   java -cp /app/resources:/a ...   Up      0.0.0.0:32771->8000/tcp
azalea_azalea_3   java -cp /app/resources:/a ...   Up      0.0.0.0:32768->8000/tcp
azalea_azalea_4   java -cp /app/resources:/a ...   Up      0.0.0.0:32770->8000/tcp
azalea_azalea_5   java -cp /app/resources:/a ...   Up      0.0.0.0:32772->8000/tcp
redis             docker-entrypoint.sh redis ...   Up      6379/tcp    

[~/IdeaProjects/Azalea] curl http://0.0.0.0:32769/table
entries {
  key: "gproto+http://172.18.0.3:8000/"
  value: 132
}
entries {
  key: "gproto+http://172.18.0.4:8000/"
  value: 132
}
entries {
  key: "gproto+http://172.18.0.5:8000/"
  value: 133
}
entries {
  key: "gproto+http://172.18.0.6:8000/"
  value: 132
}
entries {
  key: "gproto+http://172.18.0.7:8000/"
  value: 132
}
```

## Build
```
docker login
./gradlew jib --image epelesis/azalea
./gradlew jibDockerBuild --image epelesis/azalea
```

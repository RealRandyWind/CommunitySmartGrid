# Community Smart Grid
IoT sinks and sources

## Working Enviorment
using docker to set up the extenal services, start docker, pull and run images, start stop and remove images, stop docker. (warning, carfule if you have other machines running)
```
# if docker need start
docker-machine start

# if network need creation
docker network create devnet

# if images need pull before run
docker run --name dev.rabbitmq -p 5672:5672 -d rabbitmq:latest
docker run --name dev.mongo -p 27017:27017 -d mongo:latest
docker run --name dev.node -p 5674:5674 -d node:latest

# if images need start
docker start $(docker ps -a -q)

# if images need stop
docker stop $(docker ps -a -q)

# if images need remove
docker rm $(docker ps -a -q)

# id network need remove
docker network rm devnet

# if docker need stop
docker-machine stop

# to check or clean MongoDB
docker exec dev.mongo mongo "8b9deeb8-ea9a-4a4e-93f3-a819b96c5620" --eval 'printjson(db["results"].find({}).toArray());'
docker exec dev.mongo mongo "8b9deeb8-ea9a-4a4e-93f3-a819b96c5620" --eval 'db.dropDatabase();'
```

## References
[1] docker nodejs image, https://hub.docker.com/_/node/<br>
[2] docker mongodb image, https://hub.docker.com/_/mongo/<br>
[3] docker rabbitmq image, https://hub.docker.com/_/rabbitmq/<br>
[4] gentelella dashboard nodejs, https://github.com/puikinsh/gentelella

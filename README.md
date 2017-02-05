# Community Smart Grid
IoT sinks and sources

## Working Enviorment
using docker to set up the extenal services, start docker, pull and run images, start stop and remove images, stop docker. (warning, carfule if you have other machines running)
```
# if docker need start
docker-machine start

# if images need pull before run
docker run --name rabbitmq -d rabbitmq:latest
docker run --name mongo -d mongo:latest
docker run --name node -d node:latest

# if images need start
docker start $(docker ps -a -q)

# if images need stop
docker stop $(docker ps -a -q)

# if images need remove
docker rm $(docker ps -a -q)

# if docker need stop
docker-machine stop
```

## References
[1] docker nodejs image, https://hub.docker.com/_/node/<br>
[2] docker mongodb image, https://hub.docker.com/_/mongo/<br>
[3] docker rabbitmq image, https://hub.docker.com/_/rabbitmq/<br>
[4] gentelella dashboard nodejs, https://github.com/puikinsh/gentelella

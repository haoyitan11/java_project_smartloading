docker run --rm -it `
  -p 8089:8089 `
  -v "${PWD}:/mnt/locust" `
  -e USERNAME=bob `
  -e PASSWORD=password123 `
  locustio/locust:2.32.0 `
  -f /mnt/locust/locustfile.py `
  --web-host 0.0.0.0 `
  --host http://host.docker.internal:8080

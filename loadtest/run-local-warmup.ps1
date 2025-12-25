docker run --rm -it `
  -v "${PWD}:/mnt/locust" `
  locustio/locust:2.32.0 `
  -f /mnt/locust/locustfile.py `
  --headless `
  --host http://host.docker.internal:8080 `
  --users 200 --spawn-rate 20 `
  --run-time 30s

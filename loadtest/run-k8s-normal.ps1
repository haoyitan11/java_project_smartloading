docker run --rm -it `
  -p 8089:8089 `
  -v "${PWD}:/mnt/locust" `
  locustio/locust:2.32.0 `
  -f /mnt/locust/locustfile.py `
  --web-host 0.0.0.0 --web-port 8089 `
  --host http://host.docker.internal:30090 `
  --users 50 --spawn-rate 5 `
  --run-time 30s

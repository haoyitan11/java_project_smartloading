from locust import HttpUser, task, between

class DashboardUser(HttpUser):
    wait_time = between(0.1, 0.5)

    @task
    def dashboard(self):
        #call to dashboard API
        r = self.client.get("/dashboard", name="/dashboard", allow_redirects=True)

        #if it is not success then pop error
        if r.status_code != 200:
            raise Exception(f"Dashboard failed: {r.status_code} url={r.url}")

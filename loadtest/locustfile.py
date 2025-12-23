from locust import HttpUser, task, between

class DashboardUser(HttpUser):
    wait_time = between(0.1, 0.5)

    def on_start(self):
        # 1) Get login page (optional but good for session cookies)
        self.client.get("/login", name="/login [GET]")

        # 2) Post login form (Spring MVC form)
        r = self.client.post(
            "/login",
            name="/login [POST]",
            data={"username": "bob", "password": "password123"},
            allow_redirects=True,
        )

        # If it cannot connect -> status_code is 0 (Locust shows as failure)
        # If wrong creds -> likely ends up back at /login?error=true
        if r.status_code not in (200, 302):
            raise Exception(f"Login failed: {r.status_code} {r.text[:200]}")

    @task
    def dashboard(self):
        self.client.get("/dashboard")

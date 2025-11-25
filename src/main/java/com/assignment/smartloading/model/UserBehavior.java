package com.assignment.smartloading.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_behavior")
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String category;
    private int clicks;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public UserBehavior() {}

    public UserBehavior(String userId, String category, int clicks) {
        this.userId = userId;
        this.category = category;
        this.clicks = clicks;
        this.lastUpdated = new Date(); // initialize timestamp
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date();
    }

    @PrePersist
    protected void onCreate() {
        lastUpdated = new Date();
    }

    // getters and setters
    public Long getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getClicks() { return clicks; }
    public void setClicks(int clicks) { this.clicks = clicks; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}

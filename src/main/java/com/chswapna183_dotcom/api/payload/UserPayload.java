package com.chswapna183_dotcom.api.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPayload {
    private String id;
    private String name;
    private String job;
    private String createdAt;
    private String updatedAt;

    public UserPayload() {
    }

    public UserPayload(String id, String name, String job, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserPayload that = (UserPayload) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(job, that.job)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, job, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "UserPayload{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", job='" + job + '\''
                + ", createdAt='" + createdAt + '\''
                + ", updatedAt='" + updatedAt + '\''
                + '}';
    }

    public static final class Builder {
        private String id;
        private String name;
        private String job;
        private String createdAt;
        private String updatedAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder job(String job) {
            this.job = job;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserPayload build() {
            return new UserPayload(id, name, job, createdAt, updatedAt);
        }
    }
}


package com.example.community.global;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@MappedSuperclass
public abstract class BaseEntity{

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;


        protected void onCreate() {
            LocalDateTime now = LocalDateTime.now();

            this.createdAt = now;
            this.updatedAt = now;
        }


        protected void onUpdate() {
            LocalDateTime now = LocalDateTime.now();
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }


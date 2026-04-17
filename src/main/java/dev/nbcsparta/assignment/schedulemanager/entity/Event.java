package dev.nbcsparta.assignment.schedulemanager.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@EntityListeners(EnableJpaAuditing.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @ManyToOne
    private Author author;

    // JPA Empty constructor
    protected Event() {
    }

    public Event(String title, String description, Author author) {
        this.title = title;
        this.description = description;
        this.author = author;
    }



}

package dev.nbcsparta.assignment.schedulemanager.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@EntityListeners(AuditingEntityListener.class)
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
    @JoinColumn(name = "author_id", nullable = false)
    private Client author;

    // JPA Empty constructor
    protected Event() {
    }

    public Event(String title, String description, Client client) {
        this.title = title;
        this.description = description;
        this.author = client;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDate() {
        return this.updatedDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public String getAuthorName() {
        return this.author.getUserName();
    }

    public Client getAuthor() {
        return this.author;
    }

    public void updateEvent(String title, String description) {
        this.title = title;
        this.description = description;
    }
}

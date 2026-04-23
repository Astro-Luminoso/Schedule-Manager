package dev.nbcsparta.assignment.schedulemanager.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Client author;

    @LastModifiedDate
    private String updatedDate;

     // JPA Empty constructor
    protected Comment() {
    }

    public Comment(String content, Event event, Client author) {
        this.content = content;
        this.event = event;
        this.author = author;
    }

    public long getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public Event getEvent() {
        return this.event;
    }

    public Client getAuthor() {
        return this.author;
    }
}

package dev.nbcsparta.assignment.schedulemanager.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@EntityListeners(EnableJpaAuditing.class)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    // JPA Empty constructor
    protected Author() {
    }

    public Author(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

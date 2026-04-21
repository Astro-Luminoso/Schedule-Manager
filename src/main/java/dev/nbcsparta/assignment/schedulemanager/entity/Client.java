package dev.nbcsparta.assignment.schedulemanager.entity;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    // JPA Empty constructor
    protected Client() {
    }

    public Client(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean isPasswordMatch(PasswordEncoder encoder, String rawPassword) {
        return encoder.matches(rawPassword, this.password);
    }
}

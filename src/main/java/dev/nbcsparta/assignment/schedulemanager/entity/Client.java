package dev.nbcsparta.assignment.schedulemanager.entity;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private boolean isDeleted;

    // JPA Empty constructor
    protected Client() {
    }

    public Client(String userName, String email, String password, boolean isDeleted) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isDeleted = isDeleted;
    }

    public Client(String userName, String email, String password) {
        this(userName, email, password, false);
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

    public String getDate() {
        return this.updatedDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public boolean passwordNotMatch(PasswordEncoder encoder, String rawPassword) {
        return !encoder.matches(rawPassword, this.password);
    }

    public void declareDeletedUser() {
        this.userName = "Deleted_User_" + this.id;
        this.email = null;
        this.password = null;
        this.isDeleted = true;
    }

    public void updateClientDetail(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }
}

package dev.nbcsparta.assignment.schedulemanager.repository;

import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByEmail(String email);

    boolean existsByEmail(@Email String email);
}

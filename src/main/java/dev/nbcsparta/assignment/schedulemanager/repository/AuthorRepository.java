package dev.nbcsparta.assignment.schedulemanager.repository;

import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByEmail(String email);
}

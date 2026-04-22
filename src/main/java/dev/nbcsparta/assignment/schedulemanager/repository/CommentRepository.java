package dev.nbcsparta.assignment.schedulemanager.repository;

import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}

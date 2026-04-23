package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Comment;

public record CommentDetail(
        long id,
        String content,
        SimpleClientResponse client
) {

    public static CommentDetail from(Comment comment) {
        return new CommentDetail(
                comment.getId(),
                comment.getContent(),
                SimpleClientResponse.from(comment.getAuthor())
        );
    }
}

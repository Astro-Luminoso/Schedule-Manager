package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Comment;

public record CommonCommentDetail(String content, long eventId, long authorId) {

    public static CommonCommentDetail from(Comment comment) {
        return new CommonCommentDetail(
                comment.getContent(),
                comment.getEvent().getId(),
                comment.getAuthor().getId()
        );
    }
}

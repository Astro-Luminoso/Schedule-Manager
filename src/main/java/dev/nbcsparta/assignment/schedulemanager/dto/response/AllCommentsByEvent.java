package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Comment;

import java.util.List;

public record AllCommentsByEvent(List<CommentDetail> comments, int total) {

    public static AllCommentsByEvent from(List<Comment> comments) {
        List<CommentDetail> commentDetails = comments.stream()
                .map(CommentDetail::from)
                .toList();
        return new AllCommentsByEvent(commentDetails, commentDetails.size());
    }
}

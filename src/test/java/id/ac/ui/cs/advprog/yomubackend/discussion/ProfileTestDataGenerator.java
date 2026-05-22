package id.ac.ui.cs.advprog.yomubackend.discussion;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ProfileTestDataGenerator {
    
    private final CommentRepository commentRepository;
    
    public ProfileTestDataGenerator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    public UUID generateTestData(int topLevelComments, int repliesPerComment) {
        UUID readingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<Comment> allComments = new ArrayList<>();
        
        for (int i = 0; i < topLevelComments; i++) {
            Comment parent = createComment(readingId, userId, "Top level " + i, null);
            allComments.add(parent);
            
            for (int j = 0; j < repliesPerComment; j++) {
                Comment reply1 = createComment(readingId, userId, "Reply " + j, parent);
                allComments.add(reply1);
                
                // Add nested reply
                Comment reply2 = createComment(readingId, userId, "Nested " + j, reply1);
                allComments.add(reply2);
            }
        }
        
        commentRepository.saveAll(allComments);
        return readingId;
    }
    
    private Comment createComment(UUID readingId, UUID userId, String content, Comment parent) {
        Comment c = new Comment();
        c.setReadingId(readingId);
        c.setAuthorId(userId);
        c.setContent(content);
        c.setParent(parent);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        c.setDeleted(false);
        return c;
    }
}
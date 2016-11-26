package vn.edu.hust.medium.repository;

import vn.edu.hust.medium.domain.Comment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
public interface CommentRepository extends JpaRepository<Comment,Long> {
  Comment findOneByStoryIDAndStoryOrder(int storyid, int commentOrder);
}

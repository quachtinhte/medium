package vn.edu.hust.medium.repository;

import vn.edu.hust.medium.domain.Story;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Story entity.
 */
@SuppressWarnings("unused")
public interface StoryRepository extends JpaRepository<Story,Long> {
  public List<Story> findAllByAuthorID(int id);
  public List<Story> findAllByCategory(String category);
  //@Query()
  public List<Story> findAllByCategoryOrderByNumberOfLoveDesc(int storyid);
  public List<Story> findAllByCategoryOrderByNumberOfCommentDesc(int storyid);
  //
  public List<Story> findAllByCategoryOrderByTimeCreatedDesc(int storyid);
  //
  public List<Story> findAllByOrderByNumberOfCommentDesc();
  public List<Story> findAllByOrderByNumberOfLoveDesc();
}

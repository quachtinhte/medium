package vn.edu.hust.medium.repository;

import vn.edu.hust.medium.domain.Love;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Love entity.
 */
@SuppressWarnings("unused")
public interface LoveRepository extends JpaRepository<Love,Long> {
  // public List<Love> findAllByStoryID(int storyID);
  public Love findOneByStoryIDAndStoryOrder(int storyID, int storyOrder);
  //@Query("Select id from Love where ")
  public void deleteLoveByStoryIDAndStoryOrder(int storyID,int storyOrder);
  public Love findOneByStoryIDOrderByStoryOrderDesc(int storyID);
}

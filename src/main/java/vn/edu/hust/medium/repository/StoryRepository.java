package vn.edu.hust.medium.repository;

import vn.edu.hust.medium.domain.Story;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Story entity.
 */
@SuppressWarnings("unused")
public interface StoryRepository extends JpaRepository<Story,Long> {

}

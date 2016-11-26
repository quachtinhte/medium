package vn.edu.hust.medium.repository;

import vn.edu.hust.medium.domain.Love;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Love entity.
 */
@SuppressWarnings("unused")
public interface LoveRepository extends JpaRepository<Love,Long> {

}

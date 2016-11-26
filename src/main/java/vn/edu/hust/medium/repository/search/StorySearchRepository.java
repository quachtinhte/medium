package vn.edu.hust.medium.repository.search;

import vn.edu.hust.medium.domain.Story;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Story entity.
 */
public interface StorySearchRepository extends ElasticsearchRepository<Story, Long> {
}

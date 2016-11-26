package vn.edu.hust.medium.repository.search;

import vn.edu.hust.medium.domain.Love;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Love entity.
 */
public interface LoveSearchRepository extends ElasticsearchRepository<Love, Long> {
}

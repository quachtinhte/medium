package vn.edu.hust.medium.web.rest;

import com.codahale.metrics.annotation.Timed;
import vn.edu.hust.medium.domain.Love;

import vn.edu.hust.medium.repository.LoveRepository;
import vn.edu.hust.medium.repository.search.LoveSearchRepository;
import vn.edu.hust.medium.web.rest.util.HeaderUtil;
import vn.edu.hust.medium.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Love.
 */
@RestController
@RequestMapping("/api")
public class LoveResource {

    private final Logger log = LoggerFactory.getLogger(LoveResource.class);
        
    @Inject
    private LoveRepository loveRepository;

    @Inject
    private LoveSearchRepository loveSearchRepository;

    /**
     * POST  /loves : Create a new love.
     *
     * @param love the love to create
     * @return the ResponseEntity with status 201 (Created) and with body the new love, or with status 400 (Bad Request) if the love has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/loves")
    @Timed
    public ResponseEntity<Love> createLove(@RequestBody Love love) throws URISyntaxException {
        log.debug("REST request to save Love : {}", love);
        if (love.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("love", "idexists", "A new love cannot already have an ID")).body(null);
        }
        Love result = loveRepository.save(love);
        loveSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/loves/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("love", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /loves : Updates an existing love.
     *
     * @param love the love to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated love,
     * or with status 400 (Bad Request) if the love is not valid,
     * or with status 500 (Internal Server Error) if the love couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/loves")
    @Timed
    public ResponseEntity<Love> updateLove(@RequestBody Love love) throws URISyntaxException {
        log.debug("REST request to update Love : {}", love);
        if (love.getId() == null) {
            return createLove(love);
        }
        Love result = loveRepository.save(love);
        loveSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("love", love.getId().toString()))
            .body(result);
    }

    /**
     * GET  /loves : get all the loves.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of loves in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/loves")
    @Timed
    public ResponseEntity<List<Love>> getAllLoves(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Loves");
        Page<Love> page = loveRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/loves");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /loves/:id : get the "id" love.
     *
     * @param id the id of the love to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the love, or with status 404 (Not Found)
     */
    @GetMapping("/loves/{id}")
    @Timed
    public ResponseEntity<Love> getLove(@PathVariable Long id) {
        log.debug("REST request to get Love : {}", id);
        Love love = loveRepository.findOne(id);
        return Optional.ofNullable(love)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /loves/:id : delete the "id" love.
     *
     * @param id the id of the love to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/loves/{id}")
    @Timed
    public ResponseEntity<Void> deleteLove(@PathVariable Long id) {
        log.debug("REST request to delete Love : {}", id);
        loveRepository.delete(id);
        loveSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("love", id.toString())).build();
    }

    /**
     * SEARCH  /_search/loves?query=:query : search for the love corresponding
     * to the query.
     *
     * @param query the query of the love search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/loves")
    @Timed
    public ResponseEntity<List<Love>> searchLoves(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Loves for query {}", query);
        Page<Love> page = loveSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/loves");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}

package vn.edu.hust.medium.web.rest;

import vn.edu.hust.medium.MediumApp;

import vn.edu.hust.medium.domain.Love;
import vn.edu.hust.medium.repository.LoveRepository;
import vn.edu.hust.medium.repository.search.LoveSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LoveResource REST controller.
 *
 * @see LoveResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediumApp.class)
public class LoveResourceIntTest {

    private static final Integer DEFAULT_USER_ID = 1;
    private static final Integer UPDATED_USER_ID = 2;

    private static final Integer DEFAULT_STORY_ID = 1;
    private static final Integer UPDATED_STORY_ID = 2;

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    @Inject
    private LoveRepository loveRepository;

    @Inject
    private LoveSearchRepository loveSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLoveMockMvc;

    private Love love;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LoveResource loveResource = new LoveResource();
        ReflectionTestUtils.setField(loveResource, "loveSearchRepository", loveSearchRepository);
        ReflectionTestUtils.setField(loveResource, "loveRepository", loveRepository);
        this.restLoveMockMvc = MockMvcBuilders.standaloneSetup(loveResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Love createEntity(EntityManager em) {
        Love love = new Love()
                .userID(DEFAULT_USER_ID)
                .storyID(DEFAULT_STORY_ID)
                .userName(DEFAULT_USER_NAME);
        return love;
    }

    @Before
    public void initTest() {
        loveSearchRepository.deleteAll();
        love = createEntity(em);
    }

    @Test
    @Transactional
    public void createLove() throws Exception {
        int databaseSizeBeforeCreate = loveRepository.findAll().size();

        // Create the Love

        restLoveMockMvc.perform(post("/api/loves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(love)))
                .andExpect(status().isCreated());

        // Validate the Love in the database
        List<Love> loves = loveRepository.findAll();
        assertThat(loves).hasSize(databaseSizeBeforeCreate + 1);
        Love testLove = loves.get(loves.size() - 1);
        assertThat(testLove.getUserID()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testLove.getStoryID()).isEqualTo(DEFAULT_STORY_ID);
        assertThat(testLove.getUserName()).isEqualTo(DEFAULT_USER_NAME);

        // Validate the Love in ElasticSearch
        Love loveEs = loveSearchRepository.findOne(testLove.getId());
        assertThat(loveEs).isEqualToComparingFieldByField(testLove);
    }

    @Test
    @Transactional
    public void getAllLoves() throws Exception {
        // Initialize the database
        loveRepository.saveAndFlush(love);

        // Get all the loves
        restLoveMockMvc.perform(get("/api/loves?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(love.getId().intValue())))
                .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID)))
                .andExpect(jsonPath("$.[*].storyID").value(hasItem(DEFAULT_STORY_ID)))
                .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())));
    }

    @Test
    @Transactional
    public void getLove() throws Exception {
        // Initialize the database
        loveRepository.saveAndFlush(love);

        // Get the love
        restLoveMockMvc.perform(get("/api/loves/{id}", love.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(love.getId().intValue()))
            .andExpect(jsonPath("$.userID").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.storyID").value(DEFAULT_STORY_ID))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLove() throws Exception {
        // Get the love
        restLoveMockMvc.perform(get("/api/loves/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLove() throws Exception {
        // Initialize the database
        loveRepository.saveAndFlush(love);
        loveSearchRepository.save(love);
        int databaseSizeBeforeUpdate = loveRepository.findAll().size();

        // Update the love
        Love updatedLove = loveRepository.findOne(love.getId());
        updatedLove
                .userID(UPDATED_USER_ID)
                .storyID(UPDATED_STORY_ID)
                .userName(UPDATED_USER_NAME);

        restLoveMockMvc.perform(put("/api/loves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLove)))
                .andExpect(status().isOk());

        // Validate the Love in the database
        List<Love> loves = loveRepository.findAll();
        assertThat(loves).hasSize(databaseSizeBeforeUpdate);
        Love testLove = loves.get(loves.size() - 1);
        assertThat(testLove.getUserID()).isEqualTo(UPDATED_USER_ID);
        assertThat(testLove.getStoryID()).isEqualTo(UPDATED_STORY_ID);
        assertThat(testLove.getUserName()).isEqualTo(UPDATED_USER_NAME);

        // Validate the Love in ElasticSearch
        Love loveEs = loveSearchRepository.findOne(testLove.getId());
        assertThat(loveEs).isEqualToComparingFieldByField(testLove);
    }

    @Test
    @Transactional
    public void deleteLove() throws Exception {
        // Initialize the database
        loveRepository.saveAndFlush(love);
        loveSearchRepository.save(love);
        int databaseSizeBeforeDelete = loveRepository.findAll().size();

        // Get the love
        restLoveMockMvc.perform(delete("/api/loves/{id}", love.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean loveExistsInEs = loveSearchRepository.exists(love.getId());
        assertThat(loveExistsInEs).isFalse();

        // Validate the database is empty
        List<Love> loves = loveRepository.findAll();
        assertThat(loves).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLove() throws Exception {
        // Initialize the database
        loveRepository.saveAndFlush(love);
        loveSearchRepository.save(love);

        // Search the love
        restLoveMockMvc.perform(get("/api/_search/loves?query=id:" + love.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(love.getId().intValue())))
            .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].storyID").value(hasItem(DEFAULT_STORY_ID)))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())));
    }
}

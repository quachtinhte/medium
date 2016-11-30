package vn.edu.hust.medium.web.rest;

import vn.edu.hust.medium.MediumApp;

import vn.edu.hust.medium.domain.Story;
import vn.edu.hust.medium.repository.StoryRepository;
import vn.edu.hust.medium.repository.search.StorySearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StoryResource REST controller.
 *
 * @see StoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediumApp.class)
public class StoryResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_AUTHOR_ID = 1;
    private static final Integer UPDATED_AUTHOR_ID = 2;

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TIME_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PLACE_CREATED = "AAAAAAAAAA";
    private static final String UPDATED_PLACE_CREATED = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_LOVE = 1;
    private static final Integer UPDATED_NUMBER_OF_LOVE = 2;

    private static final Integer DEFAULT_NUMBER_OF_COMMENT = 1;
    private static final Integer UPDATED_NUMBER_OF_COMMENT = 2;

    private static final String DEFAULT_URL_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_URL_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    @Inject
    private StoryRepository storyRepository;

    @Inject
    private StorySearchRepository storySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restStoryMockMvc;

    private Story story;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StoryResource storyResource = new StoryResource();
        ReflectionTestUtils.setField(storyResource, "storySearchRepository", storySearchRepository);
        ReflectionTestUtils.setField(storyResource, "storyRepository", storyRepository);
        this.restStoryMockMvc = MockMvcBuilders.standaloneSetup(storyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Story createEntity(EntityManager em) {
        Story story = new Story()
                .title(DEFAULT_TITLE)
                .content(DEFAULT_CONTENT)
                .authorID(DEFAULT_AUTHOR_ID)
                .authorName(DEFAULT_AUTHOR_NAME)
                .category(DEFAULT_CATEGORY)
                .timeCreated(DEFAULT_TIME_CREATED)
                .placeCreated(DEFAULT_PLACE_CREATED)
                .numberOfLove(DEFAULT_NUMBER_OF_LOVE)
                .numberOfComment(DEFAULT_NUMBER_OF_COMMENT)
                .urlImage(DEFAULT_URL_IMAGE)
                .summary(DEFAULT_SUMMARY);
        return story;
    }

    @Before
    public void initTest() {
        storySearchRepository.deleteAll();
        story = createEntity(em);
    }

    @Test
    @Transactional
    public void createStory() throws Exception {
        int databaseSizeBeforeCreate = storyRepository.findAll().size();

        // Create the Story

        restStoryMockMvc.perform(post("/api/stories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(story)))
                .andExpect(status().isCreated());

        // Validate the Story in the database
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeCreate + 1);
        Story testStory = stories.get(stories.size() - 1);
        assertThat(testStory.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testStory.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testStory.getAuthorID()).isEqualTo(DEFAULT_AUTHOR_ID);
        assertThat(testStory.getAuthorName()).isEqualTo(DEFAULT_AUTHOR_NAME);
        assertThat(testStory.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testStory.getTimeCreated()).isEqualTo(DEFAULT_TIME_CREATED);
        assertThat(testStory.getPlaceCreated()).isEqualTo(DEFAULT_PLACE_CREATED);
        assertThat(testStory.getNumberOfLove()).isEqualTo(DEFAULT_NUMBER_OF_LOVE);
        assertThat(testStory.getNumberOfComment()).isEqualTo(DEFAULT_NUMBER_OF_COMMENT);
        assertThat(testStory.getUrlImage()).isEqualTo(DEFAULT_URL_IMAGE);
        assertThat(testStory.getSummary()).isEqualTo(DEFAULT_SUMMARY);

        // Validate the Story in ElasticSearch
        Story storyEs = storySearchRepository.findOne(testStory.getId());
        assertThat(storyEs).isEqualToComparingFieldByField(testStory);
    }

    @Test
    @Transactional
    public void getAllStories() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get all the stories
        restStoryMockMvc.perform(get("/api/stories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].authorID").value(hasItem(DEFAULT_AUTHOR_ID)))
                .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME.toString())))
                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
                .andExpect(jsonPath("$.[*].timeCreated").value(hasItem(DEFAULT_TIME_CREATED.toString())))
                .andExpect(jsonPath("$.[*].placeCreated").value(hasItem(DEFAULT_PLACE_CREATED.toString())))
                .andExpect(jsonPath("$.[*].numberOfLove").value(hasItem(DEFAULT_NUMBER_OF_LOVE)))
                .andExpect(jsonPath("$.[*].numberOfComment").value(hasItem(DEFAULT_NUMBER_OF_COMMENT)))
                .andExpect(jsonPath("$.[*].urlImage").value(hasItem(DEFAULT_URL_IMAGE.toString())))
                .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())));
    }

    @Test
    @Transactional
    public void getStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(story.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.authorID").value(DEFAULT_AUTHOR_ID))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.timeCreated").value(DEFAULT_TIME_CREATED.toString()))
            .andExpect(jsonPath("$.placeCreated").value(DEFAULT_PLACE_CREATED.toString()))
            .andExpect(jsonPath("$.numberOfLove").value(DEFAULT_NUMBER_OF_LOVE))
            .andExpect(jsonPath("$.numberOfComment").value(DEFAULT_NUMBER_OF_COMMENT))
            .andExpect(jsonPath("$.urlImage").value(DEFAULT_URL_IMAGE.toString()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStory() throws Exception {
        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);
        int databaseSizeBeforeUpdate = storyRepository.findAll().size();

        // Update the story
        Story updatedStory = storyRepository.findOne(story.getId());
        updatedStory
                .title(UPDATED_TITLE)
                .content(UPDATED_CONTENT)
                .authorID(UPDATED_AUTHOR_ID)
                .authorName(UPDATED_AUTHOR_NAME)
                .category(UPDATED_CATEGORY)
                .timeCreated(UPDATED_TIME_CREATED)
                .placeCreated(UPDATED_PLACE_CREATED)
                .numberOfLove(UPDATED_NUMBER_OF_LOVE)
                .numberOfComment(UPDATED_NUMBER_OF_COMMENT)
                .urlImage(UPDATED_URL_IMAGE)
                .summary(UPDATED_SUMMARY);

        restStoryMockMvc.perform(put("/api/stories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStory)))
                .andExpect(status().isOk());

        // Validate the Story in the database
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeUpdate);
        Story testStory = stories.get(stories.size() - 1);
        assertThat(testStory.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testStory.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testStory.getAuthorID()).isEqualTo(UPDATED_AUTHOR_ID);
        assertThat(testStory.getAuthorName()).isEqualTo(UPDATED_AUTHOR_NAME);
        assertThat(testStory.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testStory.getTimeCreated()).isEqualTo(UPDATED_TIME_CREATED);
        assertThat(testStory.getPlaceCreated()).isEqualTo(UPDATED_PLACE_CREATED);
        assertThat(testStory.getNumberOfLove()).isEqualTo(UPDATED_NUMBER_OF_LOVE);
        assertThat(testStory.getNumberOfComment()).isEqualTo(UPDATED_NUMBER_OF_COMMENT);
        assertThat(testStory.getUrlImage()).isEqualTo(UPDATED_URL_IMAGE);
        assertThat(testStory.getSummary()).isEqualTo(UPDATED_SUMMARY);

        // Validate the Story in ElasticSearch
        Story storyEs = storySearchRepository.findOne(testStory.getId());
        assertThat(storyEs).isEqualToComparingFieldByField(testStory);
    }

    @Test
    @Transactional
    public void deleteStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);
        int databaseSizeBeforeDelete = storyRepository.findAll().size();

        // Get the story
        restStoryMockMvc.perform(delete("/api/stories/{id}", story.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean storyExistsInEs = storySearchRepository.exists(story.getId());
        assertThat(storyExistsInEs).isFalse();

        // Validate the database is empty
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);

        // Search the story
        restStoryMockMvc.perform(get("/api/_search/stories?query=id:" + story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].authorID").value(hasItem(DEFAULT_AUTHOR_ID)))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].timeCreated").value(hasItem(DEFAULT_TIME_CREATED.toString())))
            .andExpect(jsonPath("$.[*].placeCreated").value(hasItem(DEFAULT_PLACE_CREATED.toString())))
            .andExpect(jsonPath("$.[*].numberOfLove").value(hasItem(DEFAULT_NUMBER_OF_LOVE)))
            .andExpect(jsonPath("$.[*].numberOfComment").value(hasItem(DEFAULT_NUMBER_OF_COMMENT)))
            .andExpect(jsonPath("$.[*].urlImage").value(hasItem(DEFAULT_URL_IMAGE.toString())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())));
    }
}

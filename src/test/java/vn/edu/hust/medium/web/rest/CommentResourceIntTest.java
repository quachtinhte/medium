package vn.edu.hust.medium.web.rest;

import vn.edu.hust.medium.MediumApp;

import vn.edu.hust.medium.domain.Comment;
import vn.edu.hust.medium.repository.CommentRepository;
import vn.edu.hust.medium.repository.search.CommentSearchRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see CommentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediumApp.class)
public class CommentResourceIntTest {

    private static final Integer DEFAULT_USER_ID = 1;
    private static final Integer UPDATED_USER_ID = 2;

    private static final Integer DEFAULT_STORY_ID = 1;
    private static final Integer UPDATED_STORY_ID = 2;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_TIME_COMMENTED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME_COMMENTED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TIME_COMMENTED_STR = DateTimeFormatter.ISO_INSTANT.format(DEFAULT_TIME_COMMENTED);

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_STORY_ORDER = 1;
    private static final Integer UPDATED_STORY_ORDER = 2;

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private CommentSearchRepository commentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCommentMockMvc;

    private Comment comment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CommentResource commentResource = new CommentResource();
        ReflectionTestUtils.setField(commentResource, "commentSearchRepository", commentSearchRepository);
        ReflectionTestUtils.setField(commentResource, "commentRepository", commentRepository);
        this.restCommentMockMvc = MockMvcBuilders.standaloneSetup(commentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity(EntityManager em) {
        Comment comment = new Comment()
                .userID(DEFAULT_USER_ID)
                .storyID(DEFAULT_STORY_ID)
                .content(DEFAULT_CONTENT)
                .timeCommented(DEFAULT_TIME_COMMENTED)
                .userName(DEFAULT_USER_NAME)
                .storyOrder(DEFAULT_STORY_ORDER);
        return comment;
    }

    @Before
    public void initTest() {
        commentSearchRepository.deleteAll();
        comment = createEntity(em);
    }

    @Test
    @Transactional
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getUserID()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testComment.getStoryID()).isEqualTo(DEFAULT_STORY_ID);
        assertThat(testComment.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testComment.getTimeCommented()).isEqualTo(DEFAULT_TIME_COMMENTED);
        assertThat(testComment.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testComment.getStoryOrder()).isEqualTo(DEFAULT_STORY_ORDER);

        // Validate the Comment in ElasticSearch
        Comment commentEs = commentSearchRepository.findOne(testComment.getId());
        assertThat(commentEs).isEqualToComparingFieldByField(testComment);
    }

    @Test
    @Transactional
    public void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the comments
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
                .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID)))
                .andExpect(jsonPath("$.[*].storyID").value(hasItem(DEFAULT_STORY_ID)))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].timeCommented").value(hasItem(DEFAULT_TIME_COMMENTED_STR)))
                .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
                .andExpect(jsonPath("$.[*].storyOrder").value(hasItem(DEFAULT_STORY_ORDER)));
    }

    @Test
    @Transactional
    public void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
            .andExpect(jsonPath("$.userID").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.storyID").value(DEFAULT_STORY_ID))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.timeCommented").value(DEFAULT_TIME_COMMENTED_STR))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
            .andExpect(jsonPath("$.storyOrder").value(DEFAULT_STORY_ORDER));
    }

    @Test
    @Transactional
    public void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        commentSearchRepository.save(comment);
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment
        Comment updatedComment = commentRepository.findOne(comment.getId());
        updatedComment
                .userID(UPDATED_USER_ID)
                .storyID(UPDATED_STORY_ID)
                .content(UPDATED_CONTENT)
                .timeCommented(UPDATED_TIME_COMMENTED)
                .userName(UPDATED_USER_NAME)
                .storyOrder(UPDATED_STORY_ORDER);

        restCommentMockMvc.perform(put("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
                .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getUserID()).isEqualTo(UPDATED_USER_ID);
        assertThat(testComment.getStoryID()).isEqualTo(UPDATED_STORY_ID);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testComment.getTimeCommented()).isEqualTo(UPDATED_TIME_COMMENTED);
        assertThat(testComment.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testComment.getStoryOrder()).isEqualTo(UPDATED_STORY_ORDER);

        // Validate the Comment in ElasticSearch
        Comment commentEs = commentSearchRepository.findOne(testComment.getId());
        assertThat(commentEs).isEqualToComparingFieldByField(testComment);
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        commentSearchRepository.save(comment);
        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // Get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean commentExistsInEs = commentSearchRepository.exists(comment.getId());
        assertThat(commentExistsInEs).isFalse();

        // Validate the database is empty
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        commentSearchRepository.save(comment);

        // Search the comment
        restCommentMockMvc.perform(get("/api/_search/comments?query=id:" + comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].userID").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].storyID").value(hasItem(DEFAULT_STORY_ID)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].timeCommented").value(hasItem(DEFAULT_TIME_COMMENTED_STR)))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].storyOrder").value(hasItem(DEFAULT_STORY_ORDER)));
    }
}

package vn.edu.hust.medium.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "story_id")
    private Integer storyID;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "time_commented")
    private ZonedDateTime timeCommented;

    @Column(name = "user_name")
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public Comment userID(Integer userID) {
        this.userID = userID;
        return this;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getStoryID() {
        return storyID;
    }

    public Comment storyID(Integer storyID) {
        this.storyID = storyID;
        return this;
    }

    public void setStoryID(Integer storyID) {
        this.storyID = storyID;
    }

    public String getContent() {
        return content;
    }

    public Comment content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getTimeCommented() {
        return timeCommented;
    }

    public Comment timeCommented(ZonedDateTime timeCommented) {
        this.timeCommented = timeCommented;
        return this;
    }

    public void setTimeCommented(ZonedDateTime timeCommented) {
        this.timeCommented = timeCommented;
    }

    public String getUserName() {
        return userName;
    }

    public Comment userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        if(comment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", userID='" + userID + "'" +
            ", storyID='" + storyID + "'" +
            ", content='" + content + "'" +
            ", timeCommented='" + timeCommented + "'" +
            ", userName='" + userName + "'" +
            '}';
    }
}

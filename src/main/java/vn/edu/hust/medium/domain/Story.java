package vn.edu.hust.medium.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Story.
 */
@Entity
@Table(name = "story")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "story")
public class Story implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "author_id")
    private Integer authorID;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "category")
    private String category;

    @Column(name = "time_created")
    private LocalDate timeCreated;

    @Column(name = "place_created")
    private String placeCreated;

    @Column(name = "number_of_love")
    private Integer numberOfLove;

    @Column(name = "number_of_comment")
    private Integer numberOfComment;

    @Column(name = "url_image")
    private String urlImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Story title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Story content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public Story authorID(Integer authorID) {
        this.authorID = authorID;
        return this;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Story authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategory() {
        return category;
    }

    public Story category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getTimeCreated() {
        return timeCreated;
    }

    public Story timeCreated(LocalDate timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }

    public void setTimeCreated(LocalDate timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getPlaceCreated() {
        return placeCreated;
    }

    public Story placeCreated(String placeCreated) {
        this.placeCreated = placeCreated;
        return this;
    }

    public void setPlaceCreated(String placeCreated) {
        this.placeCreated = placeCreated;
    }

    public Integer getNumberOfLove() {
        return numberOfLove;
    }

    public Story numberOfLove(Integer numberOfLove) {
        this.numberOfLove = numberOfLove;
        return this;
    }

    public void setNumberOfLove(Integer numberOfLove) {
        this.numberOfLove = numberOfLove;
    }

    public Integer getNumberOfComment() {
        return numberOfComment;
    }

    public Story numberOfComment(Integer numberOfComment) {
        this.numberOfComment = numberOfComment;
        return this;
    }

    public void setNumberOfComment(Integer numberOfComment) {
        this.numberOfComment = numberOfComment;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public Story urlImage(String urlImage) {
        this.urlImage = urlImage;
        return this;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Story story = (Story) o;
        if(story.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, story.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Story{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", content='" + content + "'" +
            ", authorID='" + authorID + "'" +
            ", authorName='" + authorName + "'" +
            ", category='" + category + "'" +
            ", timeCreated='" + timeCreated + "'" +
            ", placeCreated='" + placeCreated + "'" +
            ", numberOfLove='" + numberOfLove + "'" +
            ", numberOfComment='" + numberOfComment + "'" +
            ", urlImage='" + urlImage + "'" +
            '}';
    }
}

package com.example.mp_finalproject.model;

import java.util.Date;
import java.util.List;

public class Post {
    private String postId;
    private String title;
    private String description;
    private String authorId;
    private String image;
    private int upvote;
    private Date date;
    private List<String> tags;

    public Post(String postId, String title, String description, String authorId, String image, int upvote, Date date, List<String> tags) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.image = image;
        this.upvote = upvote;
        this.date = date;
        this.tags = tags;
    }
    public Post() {}
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

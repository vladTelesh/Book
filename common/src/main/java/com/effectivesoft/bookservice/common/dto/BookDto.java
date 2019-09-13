package com.effectivesoft.bookservice.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BookDto {
    private String id;
    @NotEmpty(message = "Title is required")
    private String title;
    @NotEmpty(message = "Author info is required")
    private String authorId;
    private String authorName;
    @JsonProperty("additional_authors")
    private String additionalAuthors;
    @NotEmpty(message = "ISBN is required")
    private String ISBN;
    @NotEmpty(message = "ISBN13 is required")
    private String ISBN13;
    private String publisher;
    private String binding;
    @JsonProperty("average_rating")
    private Double averageRating;
    @NotNull(message = "Pages number is required")
    @JsonProperty("pages_number")
    private Integer pagesNumber;
    @JsonProperty("publication_year")
    private Integer publicationYear;
    @JsonProperty("original_publication_year")
    private Integer originalPublicationYear;
    @NotEmpty(message = "Book description is required")
    private String description;
    @JsonProperty("image_link")
    private String imageLink;

    public BookDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdditionalAuthors() {
        return additionalAuthors;
    }

    public void setAdditionalAuthors(String additionalAuthors) {
        this.additionalAuthors = additionalAuthors;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public void setISBN13(String ISBN13) {
        this.ISBN13 = ISBN13;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getPagesNumber() {
        return pagesNumber;
    }

    public void setPagesNumber(Integer pagesNumber) {
        this.pagesNumber = pagesNumber;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getOriginalPublicationYear() {
        return originalPublicationYear;
    }

    public void setOriginalPublicationYear(Integer originalPublicationYear) {
        this.originalPublicationYear = originalPublicationYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}

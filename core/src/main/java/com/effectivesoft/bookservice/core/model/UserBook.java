package com.effectivesoft.bookservice.core.model;

import com.effectivesoft.bookservice.core.converter.LocalDatePersistenceConverter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_book")
public class UserBook {
    @Column(name = "id")
    private Long id;
    @Id
    @Column(name = "book_id")
    private String bookId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "my_rating")
    private Double myRating;
    @Column(name = "date_read")
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate dateRead;
    @Column(name = "date_added")
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate dateAdded;
    @Column(name = "read_count")
    private Integer readCount;
    @Column(name = "user_comment")
    private String comment;
    @OneToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;

    public UserBook() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getMyRating() {
        return myRating;
    }

    public void setMyRating(Double myRating) {
        this.myRating = myRating;
    }

    public LocalDate getDateRead() {
        return dateRead;
    }

    public void setDateRead(LocalDate dateRead) {
        this.dateRead = dateRead;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

package com.effectivesoft.bookservice.ui.component.dialog;

import com.vaadin.flow.component.dialog.Dialog;

import java.time.LocalDate;

public class BookDialog extends Dialog {
    private Long userBookId;
    private String bookId;
    private LocalDate dateAdded;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String userBookId) {
        this.bookId = userBookId;
    }

    public Long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(Long userBookId) {
        this.userBookId = userBookId;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }
}

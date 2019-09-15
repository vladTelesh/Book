package com.effectivesoft.bookservice.ui.component.grid;

import com.effectivesoft.bookservice.common.dto.UserBookDto;
import com.vaadin.flow.component.html.Anchor;

public class UserBooksGridComponent extends PaginatedGridComponent<UserBookDto> {

    public UserBooksGridComponent() {
        super.setSelectionMode(SelectionMode.NONE);
        super.setMultiSort(true);

        super.addComponentColumn(userBookDto ->
                new Anchor("book/" + userBookDto.getBookDto().getId(), userBookDto.getBookDto().getTitle()))
                .setHeader("Title")
                .setSortProperty("title").setWidth("180px");
        super.addComponentColumn(userBookDto -> new Anchor("author/" + userBookDto.getBookDto().getAuthorId(),
                userBookDto.getBookDto().getAuthorName())).setHeader("Author").setSortProperty("author")
                .setWidth("150px");
        super.addColumn(userBookDto -> userBookDto.getBookDto().getAverageRating())
                .setHeader("Average rating")
                .setSortProperty("rating");
        super.addColumn(UserBookDto::getMyRating)
                .setHeader("My rating")
                .setSortProperty("rating");
        super.addColumn(UserBookDto::getDateRead)
                .setHeader("Date read")
                .setSortProperty("read")
                .setWidth("100px");
        super.addColumn(UserBookDto::getDateAdded)
                .setHeader("Date added")
                .setSortProperty("added")
                .setWidth("100px");
    }
}

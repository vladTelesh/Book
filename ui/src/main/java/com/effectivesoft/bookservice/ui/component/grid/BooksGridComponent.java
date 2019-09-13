package com.effectivesoft.bookservice.ui.component.grid;


import com.vaadin.flow.component.grid.Grid;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.vaadin.flow.component.html.Anchor;

public class BooksGridComponent extends PaginatedGridComponent<BookDto> {

    public BooksGridComponent() {
        super.setSelectionMode(Grid.SelectionMode.NONE);
        super.setMultiSort(true);


        super.addComponentColumn(bookDto -> new Anchor("book/" + bookDto.getId(), bookDto.getTitle()))
                .setHeader("Title")
                .setSortProperty("title").setWidth("170px");
        super.addComponentColumn(bookDto -> new Anchor("author/" + bookDto.getAuthorId(),
                bookDto.getAuthorName())).setHeader("Author").setSortProperty("author").setWidth("170px");
        super.addColumn(BookDto::getAdditionalAuthors).setHeader("Additional authors").setSortProperty("authors");
        super.addColumn(BookDto::getAverageRating).setHeader("Average rating").setSortProperty("rating");
        super.addColumn(BookDto::getPublisher).setHeader("Publisher").setSortProperty("publisher");
        super.addColumn(BookDto::getBinding).setHeader("Binding").setSortProperty("binding");
        super.addColumn(BookDto::getPublicationYear).setHeader("Publication year").setSortProperty("year");
    }
}
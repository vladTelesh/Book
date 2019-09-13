package com.effectivesoft.bookservice.ui.component.grid;

import com.effectivesoft.bookservice.common.dto.AuthorDto;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;

public class AuthorsGridComponent extends PaginatedGridComponent<AuthorDto> {

    public AuthorsGridComponent(){
        super.setSelectionMode(Grid.SelectionMode.NONE);
        super.setMultiSort(true);

        super.addComponentColumn(authorDto -> new Anchor("author/" + authorDto.getId(), authorDto.getName()))
                .setHeader("Name")
                .setSortProperty("name");
    }
}

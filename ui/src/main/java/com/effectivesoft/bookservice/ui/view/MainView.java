package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.ui.client.BookRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.grid.BooksGridComponent;
import com.effectivesoft.bookservice.ui.component.Header;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "books")
@PageTitle("EffectiveSoft • Book-service")
public class MainView extends HorizontalLayout {

    private final BookRestClient bookRestClient;
    private final UserRestClient userRestClient;

    private final Integer BOOKS_ON_PAGE = 10;

    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    public MainView(@Autowired BookRestClient bookRestClient, UserRestClient userRestClient) throws IOException {
        this.bookRestClient = bookRestClient;
        this.userRestClient = userRestClient;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Header header = new Header(this.userRestClient);
        HorizontalLayout gridLayout = new HorizontalLayout();
        BooksGridComponent grid = new BooksGridComponent();
        gridLayout.setWidthFull();
        gridLayout.setAlignItems(Alignment.CENTER);
        gridLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        grid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    try {
                        int offset = query.getOffset();
                        int limit = query.getLimit();

                        List<String> sort = new ArrayList<>();
                        for (SortOrder<String> queryOrder : query.getSortOrders()) {
                            sort.add(queryOrder.getSorted() + "+" + queryOrder.getDirection().toString().toLowerCase());
                        }
                        return this.bookRestClient.readBooks(BOOKS_ON_PAGE, (grid.getCurrentPage() - 1) * BOOKS_ON_PAGE, sort).stream();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                },
                query -> {
                    try {
                        int count = this.bookRestClient.readBooksCount();
                        if (count - (grid.getCurrentPage() - 1) * BOOKS_ON_PAGE >= BOOKS_ON_PAGE) {
                            return 10;
                        } else {
                            return count - ((grid.getCurrentPage() - 1) * BOOKS_ON_PAGE);
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return 0;
                    }
                }

        ));
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setHeight("340px");
        gridLayout.add(grid);
        verticalLayout.add(header, new Hr(), gridLayout, grid.createPaginationButtons(getPagesCount()));
        add(verticalLayout);
    }

    private int getPagesCount() throws IOException {
        int count = this.bookRestClient.readBooksCount();
        if (count % BOOKS_ON_PAGE == 0) {
            return count / BOOKS_ON_PAGE;
        } else {
            return (count / BOOKS_ON_PAGE) + 1;
        }
    }

}



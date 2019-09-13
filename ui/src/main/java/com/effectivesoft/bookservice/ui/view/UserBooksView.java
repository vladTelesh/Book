package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.ui.client.BookRestClient;
import com.effectivesoft.bookservice.ui.client.UserBookRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.dialog.BookDialog;
import com.effectivesoft.bookservice.ui.component.Header;
import com.effectivesoft.bookservice.ui.component.grid.UserBooksGridComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.effectivesoft.bookservice.common.dto.BookDto;
import com.effectivesoft.bookservice.common.dto.UserBookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "my_books")
public class UserBooksView extends HorizontalLayout implements HasDynamicTitle {
    private String title;

    private final UserBookRestClient userBookRestClient;
    private final BookRestClient bookRestClient;
    private final UserRestClient userRestClient;

    private final Integer BOOKS_ON_PAGE = 10;

    private static final Logger logger = LoggerFactory.getLogger(UserBooksView.class);

    public UserBooksView(@Autowired UserBookRestClient userBookRestClient, BookRestClient bookRestClient, UserRestClient userRestClient) throws IOException {
        this.userBookRestClient = userBookRestClient;
        this.bookRestClient = bookRestClient;
        this.userRestClient = userRestClient;

        title = "User's books • " + ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() +
                " • Book-service";
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();

        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setWidthFull();
        gridLayout.setAlignItems(Alignment.CENTER);
        gridLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        UserBooksGridComponent grid = new UserBooksGridComponent();
        grid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    try {
                        query.getLimit();
                        query.getOffset();

                        List<String> sort = new ArrayList<>();
                        for (SortOrder<String> queryOrder : query.getSortOrders()) {
                            sort.add(queryOrder.getSorted() + "+" + queryOrder.getDirection().toString().toLowerCase());
                        }
                        return this.userBookRestClient.readUserBooks(BOOKS_ON_PAGE, (grid.getCurrentPage() - 1) * BOOKS_ON_PAGE, sort).stream();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                },
                query -> {
                    try {
                        int count = this.userBookRestClient.readUserBooksCount();
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

        BookDialog confirmDialog = new BookDialog();
        confirmDialog.setWidth("400px");
        confirmDialog.setHeight("60px");

        BookDialog editDialog = new BookDialog();
        editDialog.setWidth("230px");
        editDialog.setHeight("500px");

        HorizontalLayout confirmDialogLabelLayout = new HorizontalLayout();
        confirmDialogLabelLayout.setWidthFull();
        confirmDialogLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        confirmDialogLabelLayout.setAlignItems(Alignment.START);

        HorizontalLayout editDialogLabelLayout = new HorizontalLayout();
        editDialogLabelLayout.setWidthFull();
        editDialogLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        editDialogLabelLayout.setAlignItems(Alignment.START);

        HorizontalLayout editDialogButtonLayout = new HorizontalLayout();
        editDialogButtonLayout.setWidthFull();
        editDialogButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        editDialogButtonLayout.setAlignItems(Alignment.CENTER);

        HorizontalLayout confirmDialogButtonsLayout = new HorizontalLayout();
        confirmDialogButtonsLayout.setWidthFull();
        confirmDialogButtonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        confirmDialogButtonsLayout.setAlignItems(Alignment.END);
        confirmDialogButtonsLayout.add(new Button("Yes", onClick -> {
            if (this.userBookRestClient.deleteUserBook(confirmDialog.getBookId())) {
                confirmDialog.close();
                grid.getDataProvider().refreshAll();
            } else {
                confirmDialog.close();
            }
        }), new Button("No", onClick -> confirmDialog.close()));

        VerticalLayout userBookTextFieldsLayout = new VerticalLayout();
        userBookTextFieldsLayout.setWidthFull();
        userBookTextFieldsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        userBookTextFieldsLayout.setAlignItems(Alignment.START);

        Binder<UserBookDto> binder = new Binder<>(UserBookDto.class);

        NumberField myRating = new NumberField("Rating");
        myRating.setValueChangeMode(ValueChangeMode.ON_BLUR);
        myRating.setSizeFull();
        myRating.setStep(0.1);
        myRating.setMin(0);
        myRating.setMax(10);
        myRating.setHasControls(true);
        binder.forField(myRating)
                .withValidator(rating -> rating >= 0 && rating <= 10, "Rating should be greater than 0 and less than 10")
                .bind(UserBookDto::getMyRating, UserBookDto::setMyRating);

        DatePicker dateRead = new DatePicker("Date read");
        binder.forField(dateRead)
                .bind(UserBookDto::getDateRead, UserBookDto::setDateRead);

        NumberField readCount = new NumberField("Read count");
        readCount.setSizeFull();
        readCount.setStep(1);
        readCount.setMin(0);
        readCount.setMax(10);
        readCount.setHasControls(true);

        TextArea comment = new TextArea("CommentComponent");
        comment.setHeight("100px");
        binder.forField(comment)
                .bind(UserBookDto::getComment, UserBookDto::setComment);

        userBookTextFieldsLayout.add(myRating, dateRead, readCount, comment);

        editDialogButtonLayout.add(new Button("Save", onClick -> {
            UserBookDto userBookDto = new UserBookDto();
            binder.writeBeanIfValid(userBookDto);
            userBookDto.setId(editDialog.getUserBookId());
            userBookDto.setBookDto(new BookDto());
            userBookDto.setDateAdded(editDialog.getDateAdded());
            userBookDto.getBookDto().setId(editDialog.getBookId());
            userBookDto.setReadCount(readCount.getValue().intValue());
            try {
                if (userBookRestClient.updateUserBook(userBookDto)) {
                    grid.getDataProvider().refreshAll();
                    editDialog.close();
                }
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        }));

        editDialog.add(editDialogLabelLayout, userBookTextFieldsLayout, editDialogButtonLayout);

        confirmDialog.add(confirmDialogLabelLayout, confirmDialogButtonsLayout);

        grid.addComponentColumn(userBookDto -> {
            Icon icon = new Icon(VaadinIcon.PENCIL);
            icon.getElement().setAttribute("style", "cursor: pointer; width: 20px; height: 20px;");
            icon.addClickListener(onClick -> {
                editDialogLabelLayout.removeAll();
                editDialogLabelLayout.add(new Label(userBookDto.getBookDto().getTitle()));

                editDialog.setBookId(userBookDto.getBookDto().getId());
                editDialog.setUserBookId(userBookDto.getId());
                editDialog.setDateAdded(userBookDto.getDateAdded());

                myRating.setValue(userBookDto.getMyRating());
                readCount.setValue(Double.parseDouble(userBookDto.getReadCount().toString()));
                comment.setValue(userBookDto.getComment());
                dateRead.setValue(userBookDto.getDateRead());

                editDialog.open();
            });
            return icon;
        }).setWidth("25px");
        grid.addComponentColumn(userBookDto -> {
            Icon icon = new Icon(VaadinIcon.CLOSE_CIRCLE_O);
            icon.getElement().setAttribute("style", "cursor: pointer; width: 20px; height: 20px;");
            icon.addClickListener(onClick -> {
                confirmDialogLabelLayout.removeAll();
                confirmDialogLabelLayout.add(new Label("Are you sure you want to delete \"" + userBookDto.getBookDto().getTitle() + "\"?"));
                confirmDialog.setBookId(userBookDto.getBookDto().getId());
                confirmDialog.open();
            });
            return icon;
        }).setWidth("25px");

        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        gridLayout.add(grid);


        Dialog addBookDialog = new Dialog();
        addBookDialog.setWidth("300px");

        Binder<UserBookDto> addBinder = new Binder<>(UserBookDto.class);

        ComboBox<BookDto> comboBox = new ComboBox<>("Book selection");
        comboBox.setDataProvider(
                (filter, offset, limit) -> {
                    try {
                        if (filter.length() < 3) {
                            return null;
                        } else {
                            return this.bookRestClient.readBooksByTitle(filter).stream();
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return null;
                    }
                },
                filter -> {
                    try {
                        if (filter.length() < 3) {
                            return 0;
                        } else {
                            return this.bookRestClient.readBooksCount(filter);
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return 0;
                    }
                }
        );
        comboBox.setItemLabelGenerator(item -> item.getTitle() + ", " + item.getAuthorName());

        NumberField addMyRating = new NumberField("Rating");
        addMyRating.setValueChangeMode(ValueChangeMode.ON_BLUR);
        addMyRating.setSizeFull();
        addMyRating.setStep(0.5);
        addMyRating.setMin(0);
        addMyRating.setMax(5);
        addMyRating.setHasControls(true);

        DatePicker addDateRead = new DatePicker("Date read");
        addDateRead.setWidthFull();

        NumberField addReadCount = new NumberField("Read count");
        addReadCount.setSizeFull();
        addReadCount.setStep(1);
        addReadCount.setMin(0);
        addReadCount.setMax(10);
        addReadCount.setHasControls(true);

        TextArea addComment = new TextArea("CommentComponent");
        addComment.setHeight("100px");
        addComment.setWidthFull();

        HorizontalLayout addButtonLayout = new HorizontalLayout();
        addButtonLayout.setAlignItems(Alignment.CENTER);
        addButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        addButtonLayout.add(new Button("Add", onClick -> {
            if (!comboBox.isEmpty()) {
                UserBookDto userBookDto = new UserBookDto();
                userBookDto.setBookDto(new BookDto());
                userBookDto.getBookDto().setId(comboBox.getValue().getId());
                userBookDto.setMyRating(addMyRating.getValue());
                addMyRating.setValue(null);
                if (!addDateRead.isEmpty()) {
                    userBookDto.setDateRead(addDateRead.getValue());

                }
                if (!addReadCount.isEmpty()) {
                    userBookDto.setReadCount(addReadCount.getValue().intValue());
                }
                userBookDto.setComment(addComment.getValue());

                try {
                    if (userBookRestClient.createUserBook(userBookDto)) {
                        grid.getDataProvider().refreshAll();
                        addBookDialog.close();
                    } else {
                        comboBox.setInvalid(true);
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            } else {
                comboBox.setInvalid(true);
            }

        }));

        comboBox.setWidthFull();
        addBookDialog.add(comboBox, addMyRating, addDateRead, addReadCount, addComment, addButtonLayout);

        Button addBookButton = new Button("Add book", onClick -> {
            comboBox.setValue(null);
            addReadCount.setValue(null);
            addDateRead.setValue(null);
            addComment.setValue("");
            addBookDialog.open();
        });

        verticalLayout.add(new Header(userRestClient), gridLayout, grid.createPaginationButtons(getPagesCount()), addBookButton);

        add(verticalLayout);
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    private int getPagesCount() throws IOException {
        int count = this.userBookRestClient.readUserBooksCount();
        if (count % BOOKS_ON_PAGE == 0) {
            return count / BOOKS_ON_PAGE;
        } else {
            return (count / BOOKS_ON_PAGE) + 1;
        }
    }

}

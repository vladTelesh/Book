package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.common.dto.UserGoalDto;
import com.effectivesoft.bookservice.ui.client.UserGoalRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.Header;
import com.effectivesoft.bookservice.ui.config.security.SecurityContextParser;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

@Route("settings")
@StyleSheet("styles/userSettingsViewStyle.css")
public class UserSettingsView extends HorizontalLayout implements HasDynamicTitle {

    private String title;

    private final UserRestClient userRestClient;
    private final UserGoalRestClient userGoalRestClient;

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsView.class);

    public UserSettingsView(@Autowired UserRestClient userRestClient,
                            @Autowired UserGoalRestClient userGoalRestClient) throws IOException {
        this.userRestClient = userRestClient;
        this.userGoalRestClient = userGoalRestClient;
        this.load();
    }

    private void load() throws IOException {
        title = "Settings • " + SecurityContextParser.getEmail() + " • Book-service";

        Optional<UserGoalDto> userGoal = userGoalRestClient.readUserGoal();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout settingsLabelLayout = new HorizontalLayout();
        settingsLabelLayout.setWidthFull();
        settingsLabelLayout.setAlignItems(Alignment.CENTER);
        settingsLabelLayout.setClassName("settings-label-layout");
        settingsLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Label settingsLabel = new Label("Settings");
        settingsLabel.setClassName("settings-label");

        settingsLabelLayout.add(settingsLabel);

        HorizontalLayout goalLayout = new HorizontalLayout();
        goalLayout.setWidth("40%");

        HorizontalLayout goalLabelLayout = new HorizontalLayout();
        goalLabelLayout.setWidth("50%");
        goalLabelLayout.setAlignItems(Alignment.CENTER);
        goalLabelLayout.setJustifyContentMode(JustifyContentMode.START);

        Label goalLabel = new Label("Goal:");
        goalLabel.setClassName("goal-label");

        goalLabelLayout.add(goalLabel);

        HorizontalLayout numberFieldLayout = new HorizontalLayout();
        numberFieldLayout.setWidth("50%");
        numberFieldLayout.setAlignItems(Alignment.CENTER);
        numberFieldLayout.setJustifyContentMode(JustifyContentMode.END);


        NumberField goalCount = new NumberField();
        goalCount.setHasControls(true);
        goalCount.setStep(1);
        userGoal.ifPresent(userGoalDto -> goalCount.setValue(userGoalDto.getBookCount().doubleValue()));

        Icon infoIcon = new Icon(VaadinIcon.INFO_CIRCLE_O);
        infoIcon.setColor("#3498db");
        infoIcon.setClassName("info-icon");
        infoIcon.setSize("18px");

        Div tooltip = new Div();
        tooltip.getElement().setAttribute("data-tooltip", "You can find the progress\n" +
                "on the statistics page.");
        tooltip.setClassName("tooltip");
        tooltip.add(infoIcon);

        HorizontalLayout saveButtonLayout = new HorizontalLayout();
        saveButtonLayout.setWidthFull();
        saveButtonLayout.setAlignItems(Alignment.CENTER);
        saveButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Div saveButton = new Div();
        saveButton.setClassName("save-button");
        saveButton.add("Save");
        saveButton.addClickListener(onClick -> {
            if (userGoal.isPresent() && userGoal.get().getBookCount() != goalCount.getValue().intValue()) {
                UserGoalDto updatedUserGoal = userGoal.get();
                updatedUserGoal.setBookCount(goalCount.getValue().intValue());
                try {
                    userGoalRestClient.updateUserGoal(updatedUserGoal);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            } else {
                if (userGoal.isEmpty()) {
                    UserGoalDto userGoalDto = new UserGoalDto();
                    userGoalDto.setBookCount(goalCount.getValue().intValue());

                    try {
                        userGoalRestClient.createUserGoal(userGoalDto);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        });

        saveButtonLayout.add(saveButton);

        numberFieldLayout.add(goalCount, tooltip);

        goalLayout.add(goalLabelLayout, numberFieldLayout);

        verticalLayout.add(new Header(userRestClient), new Hr(), settingsLabelLayout, new Hr(),
                goalLayout, saveButtonLayout);

        add(verticalLayout);
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}

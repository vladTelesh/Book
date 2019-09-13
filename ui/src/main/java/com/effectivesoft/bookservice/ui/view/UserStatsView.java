package com.effectivesoft.bookservice.ui.view;

import com.effectivesoft.bookservice.common.dto.AnnualUserStatsDto;
import com.effectivesoft.bookservice.common.dto.MonthlyUserStatsDto;
import com.effectivesoft.bookservice.ui.client.UserBookRestClient;
import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.effectivesoft.bookservice.ui.component.Header;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("stats")
@StyleSheet("styles/userStatsViewStyle.css")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js")
public class UserStatsView extends HorizontalLayout implements HasDynamicTitle {
    private String title;

    private final UserRestClient userRestClient;
    private final UserBookRestClient userBookRestClient;

    private static final Logger logger = LoggerFactory.getLogger(UserStatsView.class);

    public UserStatsView(@Autowired UserRestClient userRestClient,
                         @Autowired UserBookRestClient userBookRestClient) throws IOException {
        this.userRestClient = userRestClient;
        this.userBookRestClient = userBookRestClient;
        this.load();
    }

    private void load() throws IOException {
        title = "Stats • " + ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() +
                " • Book-service";

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("70%");
        verticalLayout.setHeightFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        verticalLayout.add(new Header(userRestClient), new Hr());

        List<Integer> selectItems = new ArrayList<>();
        for (int i = LocalDate.now().getYear(); i > LocalDate.now().getYear() - 30; i--) {
            selectItems.add(i);
        }

        HorizontalLayout annualLabelLayout = new HorizontalLayout();
        annualLabelLayout.setWidthFull();
        annualLabelLayout.setAlignItems(Alignment.CENTER);
        annualLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        annualLabelLayout.getElement().setAttribute("style", "margin-top: 0;");

        Label annualLabel = new Label("Annual statistics");
        annualLabel.setClassName("annual-label");

        annualLabelLayout.add(annualLabel);
        verticalLayout.add(annualLabelLayout, new Hr());


        HorizontalLayout durationLayout = new HorizontalLayout();
        durationLayout.setWidthFull();
        durationLayout.setClassName("duration-layout");
        durationLayout.setAlignItems(Alignment.CENTER);
        durationLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Select<Integer> from = new Select<>();
        from.setLabel("From");
        from.setClassName("from");
        from.setItems(selectItems);
        from.setValue(2009);

        Select<Integer> to = new Select<>();
        to.setLabel("To");
        to.setItems(selectItems);
        to.setValue(2019);

        from.addValueChangeListener(value -> {
            if (value.getValue() < to.getValue() &&
                    value.getOldValue().intValue() != value.getValue().intValue()) {
                from.setInvalid(false);
                try {
                    updateAnnualStatsChart(from.getValue(), to.getValue());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            } else {
                from.setInvalid(true);
            }
        });
        to.addValueChangeListener(value -> {
            if (value.getValue() > from.getValue() &&
                    value.getOldValue().intValue() != value.getValue().intValue()) {
                to.setInvalid(false);
                try {
                    updateAnnualStatsChart(from.getValue(), to.getValue());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            } else {
                to.setInvalid(true);
            }
        });

        Label delimiter = new Label("-");
        delimiter.setClassName("delimiter");

        durationLayout.add(from, delimiter, to);

        Div annualChartDiv = new Div();
        annualChartDiv.setClassName("chart-container");
        annualChartDiv.setId("annual-chart-div");

        verticalLayout.add(durationLayout, annualChartDiv, new Hr());

        addAnnualStatsChart(from.getValue(), to.getValue());

        HorizontalLayout monthlyLabelLayout = new HorizontalLayout();
        monthlyLabelLayout.setWidthFull();
        monthlyLabelLayout.setAlignItems(Alignment.CENTER);
        monthlyLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        monthlyLabelLayout.getElement().setAttribute("style", "margin-top: 0;");

        Label monthlyLabel = new Label("Monthly statistics");
        monthlyLabel.setClassName("monthly-label");

        monthlyLabelLayout.add(monthlyLabel);

        HorizontalLayout yearLayout = new HorizontalLayout();
        yearLayout.setWidthFull();
        yearLayout.setAlignItems(Alignment.CENTER);
        yearLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Select<Integer> year = new Select<>();
        year.setLabel("Year");
        year.setItems(selectItems);
        year.setValue(2019);
        year.addValueChangeListener(value -> {
            try {
                updateMonthlyStatsChart(value.getValue());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        Div monthlyChartDiv = new Div();
        monthlyChartDiv.setClassName("chart-container");
        monthlyChartDiv.setId("monthly-chart-div");

        yearLayout.add(year);

        verticalLayout.add(monthlyLabelLayout, new Hr(), yearLayout, monthlyChartDiv);

        addMonthlyStatsChart(year.getValue());

        verticalLayout.add(new Hr(), new Hr(), new Hr());

        add(verticalLayout);
    }

    private void addAnnualStatsChart(int from, int to) throws IOException {
        List<AnnualUserStatsDto> userStats = userBookRestClient.readUserStats(from, to);

        StringBuilder years = new StringBuilder();
        StringBuilder count = new StringBuilder();

        for (AnnualUserStatsDto userStat : userStats) {
            years.append(userStat.getYear()).append(", ");
            count.append(userStat.getCount()).append(", ");
        }

        UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                "annual-chart-div",
                years.toString().substring(0, years.length() - 2),
                count.toString().substring(0, count.length() - 2),
                "#3498db");
    }

    private void addMonthlyStatsChart(int year) throws IOException {
        List<MonthlyUserStatsDto> monthlyUserStats = userBookRestClient.readUserStats(year);

        StringBuilder count = new StringBuilder();

        for (MonthlyUserStatsDto userStat : monthlyUserStats) {
            count.append(userStat.getCount()).append(", ");
        }

        UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                "monthly-chart-div",
                "January, February, March, April, May, June, July, August, September, October, November, December",
                count.toString().substring(0, count.length() - 2),
                "#e5bd00");
    }

    private void updateAnnualStatsChart(int from, int to) throws IOException {
        List<AnnualUserStatsDto> userStats = userBookRestClient.readUserStats(from, to);

        StringBuilder years = new StringBuilder();
        StringBuilder count = new StringBuilder();

        if (userStats != null && userStats.size() != 0) {
            for (AnnualUserStatsDto userStat : userStats) {
                years.append(userStat.getYear()).append(", ");
                count.append(userStat.getCount()).append(", ");
            }
            UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                    "annual-chart-div",
                    years.toString().substring(0, years.length() - 2),
                    count.toString().substring(0, count.length() - 2),
                    "#3498db");
        } else {
            UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                    "annual-chart-div",
                    "0",
                    "0",
                    "#3498db");
        }
    }

    private void updateMonthlyStatsChart(int year) throws IOException {
        List<MonthlyUserStatsDto> userStats = userBookRestClient.readUserStats(year);

        StringBuilder count = new StringBuilder();

        if (userStats != null && userStats.size() != 0) {
            for (MonthlyUserStatsDto userStat : userStats) {
                count.append(userStat.getCount()).append(", ");
            }
            UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                    "monthly-chart-div",
                    "January, February, March, April, May, June, July, August, September, October, November, December",
                    count.toString().substring(0, count.length() - 2),
                    "#e5bd00");
        } else {
            UI.getCurrent().getPage().executeJavaScript(readJavaScript("createLineChart.js"),
                    "monthly-chart-div",
                    "January, February, March, April, May, June, July, August, September, October, November, December",
                    "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
                    "#e5bd00");
        }
    }

    private String readJavaScript(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(
                "src\\main\\resources\\META-INF\\resources\\js\\" + fileName))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return contentBuilder.toString();
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}
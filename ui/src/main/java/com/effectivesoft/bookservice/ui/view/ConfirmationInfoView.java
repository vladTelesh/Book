package com.effectivesoft.bookservice.ui.view;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Info")
@Route(value = "confirmation")
public class ConfirmationInfoView extends VerticalLayout {

    public ConfirmationInfoView() {
        setHeightFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        Text text = new Text("Check your mail!");
        add(text);
        add(new Button("Main page", onClick -> {
            UI.getCurrent().navigate("books");
        }));
    }
}

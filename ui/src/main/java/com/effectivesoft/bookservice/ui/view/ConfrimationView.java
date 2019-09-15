package com.effectivesoft.bookservice.ui.view;


import com.effectivesoft.bookservice.ui.client.UserRestClient;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Confirm")
@Route(value = "confirm")
public class ConfrimationView extends VerticalLayout implements HasUrlParameter<String> {
    private final UserRestClient userRestClient;

    private static final Logger logger = LoggerFactory.getLogger(ConfrimationView.class);

    public ConfrimationView(@Autowired UserRestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String code) {
        setHeightFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        if (code == null) {
            add(new Text("BAD CONFIRMATION CODE!"));
        } else {
            if (userRestClient.confirmUser(code)) {
                add(new Text("SUCCESS!"));
            } else {
                add(new Text("No such unconfirmed user!"));
            }
            add(new Button("Sign in", onClick -> {
                UI.getCurrent().navigate("sign_in");
            }));
        }
    }
}

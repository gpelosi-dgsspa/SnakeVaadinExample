package com.dgsspa.snake.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("control")
@AnonymousAllowed
public class ControlPage extends VerticalLayout {

    public ControlPage() {
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        // Aggiungi gli eventuali gestori degli eventi per i pulsanti qui...

        add(startButton, stopButton);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }
}

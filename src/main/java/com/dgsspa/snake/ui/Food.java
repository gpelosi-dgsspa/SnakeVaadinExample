package com.dgsspa.snake.ui;

import com.vaadin.flow.component.html.Div;

public class Food extends Div {

    public Food(String color) {
        setWidth("10px");
        setHeight("10px");
        getElement().getStyle().set("background-color", color);
    }


    public void setColor(String color) {
        getStyle().set("background-color", color);
    }

    public String getColor() {
        return getElement().getStyle().get("background-color");
    }

}

package com.dgsspa.snake.utils;

import com.dgsspa.snake.enums.CellType;
import com.vaadin.flow.component.html.Div;
import lombok.Data;

@Data
public class GridCell extends Div {

    private static final int squareSize=50;

    private int xc;
    private int yc;
    private CellType type;

    public GridCell(int x, int y,CellType type){
        super();
        this.yc=y;
        this.xc=x;
        this.type=type;
        this.setWidth(squareSize + "px");
        this.setHeight(squareSize + "px");
        this.getStyle().set("border", "0.1px solid black");
        this.getStyle().set("background-color", "#FFFFFF");

    }

}

package com.dgsspa.snake.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private int initialRowIndex;
    private int initialColIndex;
    private VerticalLayout gridLayout;
    private int gridSize = 50;

    private Button tastoGiu;
    private Button tastoSu;
    private Button tastoSinistra;
    private Button tastoDestra;

    public HomeView() {
        initialColIndex = gridSize / 2;

        H1 welcomeHeader = new H1("Benvenuto su SnaCkF");
        welcomeHeader.getElement().getStyle().set("margin", "auto");

        gridLayout = createEmptySquares(10, 50);
        gridLayout.getStyle().set("margin", "auto");

        HorizontalLayout centeredLayout = new HorizontalLayout();
        centeredLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        centeredLayout.add(gridLayout);

        VerticalLayout controlLayout = new VerticalLayout();
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        tastoGiu = new Button("Giu");
        tastoSu = new Button("Su");
        tastoSinistra = new Button("Sinistra");
        tastoDestra = new Button("Destra");

        tastoGiu.setEnabled(false);
        tastoSu.setEnabled(false);
        tastoSinistra.setEnabled(false);
        tastoDestra.setEnabled(false);

        tastoGiu.addClickListener(e -> moveUp());
        tastoSu.addClickListener(e -> moveDown());
        tastoSinistra.addClickListener(e -> moveLeft());
        tastoDestra.addClickListener(e -> moveRight());

        controlLayout.add(startButton, stopButton, tastoGiu, tastoSu, tastoSinistra, tastoDestra);

        startButton.addClickListener(e -> {
            colorCells();
            enableAllButtons();
        });

        stopButton.addClickListener(e -> {
            resetCellColors();
            disableAllButtons();
        });

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.add(centeredLayout, new Div(), controlLayout);

        add(welcomeHeader, mainLayout);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void enableAllButtons() {
        tastoGiu.setEnabled(true);
        tastoSu.setEnabled(true);
        tastoSinistra.setEnabled(true);
        tastoDestra.setEnabled(true);
    }

    private void disableAllButtons() {
        tastoGiu.setEnabled(false);
        tastoSu.setEnabled(false);
        tastoSinistra.setEnabled(false);
        tastoDestra.setEnabled(false);
    }

    private VerticalLayout createEmptySquares(int size, int count) {
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.getStyle().set("gap", "0");

        for (int i = 0; i < count; i++) {
            HorizontalLayout rowLayout = new HorizontalLayout();
            rowLayout.getStyle().set("gap", "0");

            for (int j = 0; j < 50; j++) {
                Div square = createEmptySquare(size);
                rowLayout.add(square);
            }
            gridLayout.add(rowLayout);
        }

        return gridLayout;
    }

    private Div createEmptySquare(int size) {
        Div square = new Div();
        square.setWidth(size + "px");
        square.setHeight(size + "px");
        square.getStyle().set("border", "0.1px solid black");
        square.getStyle().set("background-color", "#D3D3D3");

        return square;
    }

    private void colorCells() {
        int centerIndex = gridSize / 2;

        initialRowIndex = centerIndex;

        colorCell(centerIndex, centerIndex, 1);
        colorCell(centerIndex - 1, centerIndex, 2);
        colorCell(centerIndex + 1, centerIndex, 3);
    }

    private void resetCellColors() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                resetCellColor(i, j);
            }
        }
    }

    private void colorCell(int rowIndex, int colIndex, int number) {
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("background-color", "yellow");

        Span numberSpan = new Span(String.valueOf(number));
        numberSpan.getStyle().set("color", "black");
        cell.add(numberSpan);
    }

    private void resetCellColor(int rowIndex, int colIndex) {
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("background-color", "#D3D3D3");
    }

    private void moveUp() {
        int centerIndex = gridSize / 2;

        initialRowIndex = (initialRowIndex - 1 + gridSize) % gridSize;
        resetCellColor((initialRowIndex + 2) % gridSize, centerIndex);

        rotateCell((initialRowIndex + 1) % gridSize, centerIndex);
        rotateCell(initialRowIndex, centerIndex);

        colorCell((initialRowIndex + 1) % gridSize, centerIndex, 1);
        colorCell(initialRowIndex, centerIndex, 2);
        colorCell((initialRowIndex - 1 + gridSize) % gridSize, centerIndex, 3);
    }

    private void moveDown() {
        int centerIndex = gridSize / 2;

        initialRowIndex = (initialRowIndex + 1) % gridSize;
        resetCellColor((initialRowIndex - 2 + gridSize) % gridSize, centerIndex);

        rotateCell((initialRowIndex - 1 + gridSize) % gridSize, centerIndex);
        rotateCell(initialRowIndex, centerIndex);

        colorCell((initialRowIndex - 1 + gridSize) % gridSize, centerIndex, 1);
        colorCell(initialRowIndex, centerIndex, 2);
        colorCell((initialRowIndex + 1) % gridSize, centerIndex, 3);
    }

    private void moveRight() {
        int centerIndex = gridSize / 2;

        initialColIndex = (initialColIndex + 1) % gridSize;
        resetCellColor(centerIndex, (initialColIndex - 2 + gridSize) % gridSize);

        rotateCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize);
        rotateCell(centerIndex, initialColIndex);

        colorCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize, 1);
        colorCell(centerIndex, initialColIndex, 2);
        colorCell(centerIndex, (initialColIndex + 1) % gridSize, 3);

        initialRowIndex = centerIndex;
    }

    private void moveLeft() {
        int centerIndex = gridSize / 2;

        initialColIndex = (initialColIndex - 1 + gridSize) % gridSize;
        resetCellColor(centerIndex, (initialColIndex + 2) % gridSize);

        rotateCell(centerIndex, (initialColIndex + 1) % gridSize);
        rotateCell(centerIndex, initialColIndex);

        colorCell(centerIndex, (initialColIndex + 1) % gridSize, 1);
        colorCell(centerIndex, initialColIndex, 2);
        colorCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize, 3);

        initialRowIndex = centerIndex;
    }

    private void rotateCell(int rowIndex, int colIndex) {
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("transform", "rotate(90deg)");
    }
}

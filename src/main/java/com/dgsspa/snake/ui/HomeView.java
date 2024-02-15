package com.dgsspa.snake.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// Dichiarazione della classe con annotazioni per Vaadin
@Route("")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private List<Point> snakeCoordinates = new ArrayList<>();


    private int initialRowIndex;
    private int initialColIndex;
    private VerticalLayout gridLayout;
    private int gridSize = 50;
    private UI ui;

    private Map<Point, String> cellColors = new HashMap<>();

    private Timer timer;

    private int previousFoodRowIndex;
    private int previousFoodColIndex;

    private Button downButton;
    private Button upButton;
    private Button leftButton;
    private Button rightButton;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private boolean gameRunning = false;
    boolean isGoingUp = true;
    boolean isGoingDown = true;

    boolean isGoingLeft = true;
    boolean isGoingRight = true;


    // Costruttore principale della classe
    public HomeView() {

        initialRowIndex = gridSize / 2;
        initialColIndex = gridSize / 2;

        snakeCoordinates.add(new Point(initialRowIndex, initialColIndex));
        snakeCoordinates.add(new Point(initialRowIndex - 1, initialColIndex)); // Cella sopra il centro
        snakeCoordinates.add(new Point(initialRowIndex + 1, initialColIndex)); // Cella sotto il centro


        timer = new Timer();

        ui = UI.getCurrent();

        initialColIndex = gridSize / 2;

        H1 welcomeHeader = new H1("Welcome to SnaCkF");
        welcomeHeader.getElement().getStyle().set("margin", "auto");

        gridLayout = createEmptySquares(10, 50);
        gridLayout.getStyle().set("margin", "auto");

        HorizontalLayout centeredLayout = new HorizontalLayout();
        centeredLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        centeredLayout.add(gridLayout);

        VerticalLayout controlLayout = new VerticalLayout();
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        downButton = new Button("Up");
        upButton = new Button("Down");
        leftButton = new Button("Left");
        rightButton = new Button("Right");

        downButton.setEnabled(false);
        upButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);

        downButton.addClickListener(e -> moveUp());
        upButton.addClickListener(e -> moveDown());
        leftButton.addClickListener(e -> moveLeft());
        rightButton.addClickListener(e -> moveRight());

        controlLayout.add(startButton, stopButton, downButton, upButton, leftButton, rightButton);

        startButton.addClickListener(e -> {

            gameRunning = true;
            colorCells();
            enableAllButtons();
            addFood();
        });

        stopButton.addClickListener(e -> {
            resetCellColors();
            disableAllButtons();
            scheduler.shutdown();
            gameRunning = false;
        });

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.add(centeredLayout, new Div(), controlLayout);

        add(welcomeHeader, mainLayout);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void enableAllButtons() {
        downButton.setEnabled(true);
        upButton.setEnabled(true);
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
    }

    private void disableAllButtons() {
        downButton.setEnabled(false);
        upButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
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

        colorCell(centerIndex, centerIndex);
        colorCell(centerIndex - 1, centerIndex);
        colorCell(centerIndex + 1, centerIndex);
    }

    private void resetCellColors() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                resetCellColor(i, j);
            }
        }
    }

    private void colorCell(int rowIndex, int colIndex) {
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("background-color", "yellow");
    }

    private void moveUp() {
        isGoingDown = false;
        isGoingLeft=true;
        isGoingRight=true;
        if (isGoingUp) {
            System.out.println("Coordinate iniziali: " + snakeCoordinates);

            snakeCoordinates.remove(snakeCoordinates.size() - 1);
            int newHeadRowIndex = (snakeCoordinates.get(0).x - 1 + gridSize) % gridSize;
            int newHeadColIndex = snakeCoordinates.get(0).y;
            snakeCoordinates.add(0, new Point(newHeadRowIndex, newHeadColIndex));


            initialRowIndex = (initialRowIndex - 1 + gridSize) % gridSize;
            resetCellColor((newHeadRowIndex + 2) % gridSize, newHeadColIndex);

            rotateCell((newHeadRowIndex + 1) % gridSize, newHeadColIndex);
            rotateCell(newHeadRowIndex, newHeadColIndex);

            colorCell((newHeadRowIndex + 1) % gridSize, newHeadColIndex);
            colorCell(newHeadRowIndex, newHeadColIndex);
            colorCell((newHeadRowIndex - 1 + gridSize) % gridSize, newHeadColIndex);
            System.out.println("Coordinate dopo il movimento: " + snakeCoordinates);
        }

    }

    private void moveDown() {
        isGoingUp = false;
        isGoingLeft=true;
        isGoingRight=true;
        if (isGoingDown) {
            System.out.println("Coordinate iniziali: " + snakeCoordinates);

            // Rimuovi l'ultimo elemento (coda del serpente)
            snakeCoordinates.remove(snakeCoordinates.size() - 1);

            // Calcola la nuova posizione della testa del serpente
            int newHeadRowIndex = (snakeCoordinates.get(0).x + 1) % gridSize;
            int newHeadColIndex = snakeCoordinates.get(0).y;

            // Stampa le nuove coordinate della testa del serpente
            System.out.println("Nuove coordinate della testa: (" + newHeadRowIndex + ", " + newHeadColIndex + ")");

            // Aggiungi la nuova testa del serpente alla lista
            snakeCoordinates.add(0, new Point(newHeadRowIndex, newHeadColIndex));

            // Resetta il colore della cella precedente
            resetCellColor((newHeadRowIndex - 2 + gridSize) % gridSize, newHeadColIndex);

            // Ruota e colora le nuove celle del serpente
            rotateCell((newHeadRowIndex - 1 + gridSize) % gridSize, newHeadColIndex);
            rotateCell(newHeadRowIndex, newHeadColIndex);

            colorCell((newHeadRowIndex - 1 + gridSize) % gridSize, newHeadColIndex);
            colorCell(newHeadRowIndex, newHeadColIndex);
            colorCell((newHeadRowIndex + 1) % gridSize, newHeadColIndex);

            // Stampa le coordinate dopo il movimento
            System.out.println("Coordinate dopo il movimento: " + snakeCoordinates);
        }
    }


    private void moveRight() {
        isGoingLeft = false;
        isGoingDown = true;
        isGoingUp = true;

        if (isGoingRight) {
            int centerIndex = gridSize / 2;

            initialColIndex = (initialColIndex + 1) % gridSize;
            resetCellColor(centerIndex, (initialColIndex - 2 + gridSize) % gridSize);

            rotateCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize);
            rotateCell(centerIndex, initialColIndex);

            colorCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize);
            colorCell(centerIndex, initialColIndex);
            colorCell(centerIndex, (initialColIndex + 1) % gridSize);

            initialRowIndex = centerIndex;
        }
    }

    private void moveLeft() {
        isGoingRight=false;
        isGoingDown = true;
        isGoingUp = true;

        if(isGoingLeft) {
            int centerIndex = gridSize / 2;

            initialColIndex = (initialColIndex - 1 + gridSize) % gridSize;
            resetCellColor(centerIndex, (initialColIndex + 2) % gridSize);

            rotateCell(centerIndex, (initialColIndex + 1) % gridSize);
            rotateCell(centerIndex, initialColIndex);

            colorCell(centerIndex, (initialColIndex + 1) % gridSize);
            colorCell(centerIndex, initialColIndex);
            colorCell(centerIndex, (initialColIndex - 1 + gridSize) % gridSize);

            initialRowIndex = centerIndex;
        }
    }

    private void rotateCell(int rowIndex, int colIndex) {
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("transform", "rotate(90deg)");
    }

    private void addFood() {
        if (gameRunning) {
            Point newFoodPoint;
            Point oldFoodPoint;
            int foodRowIndex;
            int foodColIndex;

            oldFoodPoint = new Point(previousFoodRowIndex, previousFoodColIndex);

            do {
                foodRowIndex = new Random().nextInt(gridSize);
                foodColIndex = new Random().nextInt(gridSize);
                newFoodPoint = new Point(foodRowIndex, foodColIndex);
                System.out.println("Color of newFoodPoint: " + cellColors.get(newFoodPoint) + " coordinates x = " + foodRowIndex + "  y =  " + foodColIndex);

            } while (newFoodPoint.equals(oldFoodPoint) || "yellow".equals(cellColors.get(newFoodPoint)));

            System.out.println("Used");

            previousFoodRowIndex = foodRowIndex;
            previousFoodColIndex = foodColIndex;

            HorizontalLayout rowLayout = (HorizontalLayout) gridLayout.getComponentAt(newFoodPoint.x);
            Div cell = (Div) rowLayout.getComponentAt(newFoodPoint.y);

            ui.access(() -> {
                Food food = new Food("green");
                cell.getStyle().set("background-color", food.getColor());
            });

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ui.access(() -> {
                        Food food = new Food("#D3D3D3");
                        cell.getStyle().set("background-color", food.getColor());

                        addFood();
                    });
                }
            }, 500000);
        }
    }

    private void resetCellColor(int rowIndex, int colIndex) {
        System.out.println("------------------------------------------------");
        System.out.println("Inside resetCellColor()  foodRowIndex =  " + rowIndex + " foodColIndex = " + colIndex);

        UI.getCurrent().access(() -> {
            System.out.println("Inside UI thread access");

            System.out.println("gridLayout: " + gridLayout);
            System.out.println("rowIndex: " + rowIndex);
            System.out.println("colIndex: " + colIndex);

            Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);

            if (cell == null) {
                System.out.println("Cell is null");
            } else {
                cell.getStyle().set("background-color", "#D3D3D3");
            }
        });
    }
}

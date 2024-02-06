package com.dgsspa.snake.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private VerticalLayout gridLayout;
    private int currentRowIndex = 0;
    private int gridSize = 50; // Aggiunta della variabile gridSize

    private Button colorForwardResetBackwardButton;
    private Button colorBackwardResetForwardButton;

    private int moveInterval = 500; // Intervallo di movimento in millisecondi
    private boolean isMoving = false;
    public HomeView() {
        // Centered H1
        H1 welcomeHeader = new H1("Benvenuto su SnaCkF");
        welcomeHeader.getElement().getStyle().set("margin", "auto");

        // Example: Create 10x10 squares with a size of 100 pixels each
        gridLayout = createEmptySquares(10, 50);
        gridLayout.getStyle().set("margin", "auto"); // Center the grid

        // Centered layout
        HorizontalLayout centeredLayout = new HorizontalLayout();
        centeredLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        centeredLayout.add(gridLayout);

        // Right layout with control buttons
        VerticalLayout controlLayout = new VerticalLayout();
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        // Inizializzazione dei pulsanti colorForwardResetBackwardButton e colorBackwardResetForwardButton
        colorForwardResetBackwardButton = new Button("Giu");
        colorBackwardResetForwardButton = new Button("Su");

        // Imposta i pulsanti inizialmente disabilitati
        colorForwardResetBackwardButton.setEnabled(false);
        colorBackwardResetForwardButton.setEnabled(false);

        // Aggiungi gli ascoltatori ai pulsanti
        colorForwardResetBackwardButton.addClickListener(e -> colorForwardResetBackward());
        colorBackwardResetForwardButton.addClickListener(e -> colorBackwardResetForward());

        // Aggiungi i pulsanti al layout di controllo
        controlLayout.add(startButton, stopButton, colorForwardResetBackwardButton, colorBackwardResetForwardButton);

        // Aggiungi un gestore di eventi per il pulsante "Start"
        startButton.addClickListener(e -> {
            colorCells();
            // Abilita i pulsanti colorForwardResetBackwardButton e colorBackwardResetForwardButton
            colorForwardResetBackwardButton.setEnabled(true);
            colorBackwardResetForwardButton.setEnabled(true);
        });

        // Aggiungi un gestore di eventi per il pulsante "Stop"
        stopButton.addClickListener(e -> {
            resetCellColors();
            // Disabilita i pulsanti colorForwardResetBackwardButton e colorBackwardResetForwardButton
            colorForwardResetBackwardButton.setEnabled(false);
            colorBackwardResetForwardButton.setEnabled(false);
        });

        // Main layout with three columns
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.add(centeredLayout, new Div(), controlLayout); // Added a new empty column (col-3)

        // Add components to the main layout
        add(welcomeHeader, mainLayout);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private VerticalLayout createEmptySquares(int size, int count) {
        VerticalLayout gridLayout = new VerticalLayout();

        gridLayout.getStyle().set("gap", "0"); // Remove spacing between rows

        for (int i = 0; i < count; i++) {
            HorizontalLayout rowLayout = new HorizontalLayout();
            rowLayout.getStyle().set("gap", "0"); // Remove spacing between columns

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
        square.getStyle().set("border", "0.1px solid black"); // Black border
        square.getStyle().set("background-color", "#D3D3D3");

        return square;
    }

    private void colorCells() {
        // Ottieni la dimensione della griglia
        int gridSize = 50;

        // Calcola l'indice della cella centrale
        int centerIndex = gridSize / 2;

        // Colora la cella centrale
        colorCell(centerIndex, centerIndex);

        // Colora le celle laterali
        colorCell(centerIndex - 1, centerIndex);
        colorCell(centerIndex + 1, centerIndex);
    }

    private void resetCellColors() {
        // Ottieni la dimensione della griglia
        int gridSize = 50;

        // Calcola l'indice della cella centrale
        int centerIndex = gridSize / 2;

        // Annulla il colore della cella centrale
        resetCellColor(centerIndex, centerIndex);

        // Annulla il colore delle celle laterali
        resetCellColor(centerIndex - 1, centerIndex);
        resetCellColor(centerIndex + 1, centerIndex);
    }

    private void colorCell(int rowIndex, int colIndex) {
        // Imposta il colore della cella alla posizione specificata
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("background-color", "#FFFF00"); // Giallo, puoi sostituire con il tuo colore preferito
    }

    private void resetCellColor(int rowIndex, int colIndex) {
        // Imposta il colore della cella alla condizione iniziale (senza sfondo)
        Div cell = (Div) ((HorizontalLayout) gridLayout.getComponentAt(rowIndex)).getComponentAt(colIndex);
        cell.getStyle().set("background-color", "#D3D3D3");
    }

    private void colorForwardResetBackward() {
        // Colora la cella nella riga corrente e colonna centrale
        colorCell(currentRowIndex, gridSize / 2);
        colorCell((currentRowIndex + 1) % gridSize, gridSize / 2);
        colorCell((currentRowIndex + 2) % gridSize, gridSize / 2);

        // Resetta la cella nella riga precedente e colonna centrale
        resetCellColor((currentRowIndex - 1 + gridSize) % gridSize, gridSize / 2);
        resetCellColor((currentRowIndex - 2 + gridSize) % gridSize, gridSize / 2);

        // Sposta la riga corrente in avanti
        currentRowIndex = (currentRowIndex + 1) % gridSize;
    }

    private void colorBackwardResetForward() { colorCell(currentRowIndex, gridSize / 2);
        colorCell((currentRowIndex - 1 + gridSize) % gridSize, gridSize / 2);
        colorCell((currentRowIndex - 2 + gridSize) % gridSize, gridSize / 2);

        // Resetta la cella nella riga successiva e colonna centrale
        resetCellColor((currentRowIndex + 1) % gridSize, gridSize / 2);
        resetCellColor((currentRowIndex + 2) % gridSize, gridSize / 2);

        // Sposta la riga corrente all'indietro
        currentRowIndex = (currentRowIndex - 1 + gridSize) % gridSize;
    }
}
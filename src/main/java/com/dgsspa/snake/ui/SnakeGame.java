package com.dgsspa.snake.ui;

import com.dgsspa.snake.enums.CellType;
import com.dgsspa.snake.enums.Direction;
import com.dgsspa.snake.utils.GridCell;
import com.dgsspa.snake.utils.Snake;
import com.dgsspa.snake.utils.Point;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Route("/snake")
@AnonymousAllowed
public class SnakeGame extends VerticalLayout {

    private Snake snake;
    private Direction direction;
    private boolean isRunning = true;

    private boolean thereIsFood = false;

    private UI ui;

    private Timer timer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int gridSize = 12;
    private static final int defaultSnakePosition =5;
    private static final int snakeSpid = 1000;

    private VerticalLayout playGrid;
    private Dialog endGameDialog;

    Button startGame;
    Button moveUp;
    Button moveDown;
    Button moveRight;
    Button moveLeft;



    // Inizializza il gioco
    public SnakeGame() {
        ui = UI.getCurrent();
        snake = new Snake();
        direction = Direction.RIGHT;
        timer = new Timer();

        add(designEndGameDialog());
        add(designGameHeader());
        add(designGameBody());
        add(designGameFooter());
        setAlignItems(Alignment.CENTER);
        setSizeFull();

        enableButtons();

        showFood();
    }

    private HorizontalLayout designGameHeader(){
        HorizontalLayout gameHeader = new HorizontalLayout();
        gameHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        H1 welcomeHeader = new H1("Benvenuto!");
        welcomeHeader.getElement().getStyle().set("margin", "auto");
        gameHeader.add(welcomeHeader);
        return gameHeader;
    }

    private HorizontalLayout designGameBody(){
        HorizontalLayout gameBody = new HorizontalLayout();
        gameBody.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        playGrid = new VerticalLayout();
        playGrid.getStyle().set("border", "1px solid black");
        playGrid.getStyle().set("display", "grid");
        playGrid.getStyle().set("gap", "0");
        playGrid.setWidth("fit-content");
        designPlayGrid();
        gameBody.add(playGrid);
        return gameBody;
    }

    private HorizontalLayout designGameFooter(){
        HorizontalLayout gameFooter = new HorizontalLayout();
        gameFooter.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        startGame = new Button("START");
        moveUp = new Button("UP");
        moveDown = new Button("DOWN");
        moveLeft = new Button("LEFT");
        moveRight = new Button("RIGHT");

        startGame.addClickListener(e -> startSnakeMovement());
        moveUp.addClickListener(e -> handleInput(Direction.UP));
        moveDown.addClickListener(e -> handleInput(Direction.DOWN));
        moveLeft.addClickListener(e -> handleInput(Direction.LEFT));
        moveRight.addClickListener(e -> handleInput(Direction.RIGHT));

        gameFooter.add(startGame);
        gameFooter.add(moveUp);
        gameFooter.add(moveDown);
        gameFooter.add(moveLeft);
        gameFooter.add(moveRight);
        return gameFooter;
    }

    private Dialog designEndGameDialog(){
        endGameDialog = new Dialog();
        VerticalLayout modalLayout = new VerticalLayout();
        modalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        H1 modalTitle = new H1("GAME OVER");
        Span modalSpan = new Span("Eaten Apples: "+snake.getHeatenApples());
        Button restartButton = new Button("RESTART");
        restartButton.addClickListener(e -> reloadGame());
        modalLayout.add(modalTitle);
        modalLayout.add(modalSpan);
        modalLayout.add(restartButton);
        endGameDialog.add(modalLayout);
        endGameDialog.setCloseOnEsc(false);
        endGameDialog.setCloseOnOutsideClick(false);
        return endGameDialog;
    }

    // Metodo per la creazione dei dati delle celle con coordinate X e Y
    private void designPlayGrid() {

        for (int x = 0; x < gridSize; x++) {
            HorizontalLayout gridRow = new HorizontalLayout();
            gridRow.getStyle().set("gap", "0");
            for (int y = 0; y < gridSize; y++) {
                GridCell cell = new GridCell(x,y, CellType.EMPTY);
                if(x==defaultSnakePosition && y==defaultSnakePosition){
                    cell.setType(CellType.SNAKE);
                    cell.getStyle().set("background-color", "green");
                }
                gridRow.add(cell);
            }
            playGrid.add(gridRow);
        }

    }

    private void enableButtons(){
        startGame.setEnabled(true);
        moveUp.setEnabled(true);
        moveDown.setEnabled(true);
        moveLeft.setEnabled(true);
        moveRight.setEnabled(true);
    }



    // Gestisci l'input dell'utente per cambiare la direzione
    private void handleInput(Direction newDirection) {
        // Aggiorna la direzione del serpente in base all'input dell'utente
        boolean movimentoAbilitato=false;
        switch (newDirection){
            case UP:
                movimentoAbilitato=direction!=Direction.UP && direction!=Direction.DOWN;
                break;
            case DOWN:
                movimentoAbilitato=direction!=Direction.DOWN && direction!=Direction.UP;
                break;
            case LEFT:
                movimentoAbilitato=direction!=Direction.LEFT && direction!=Direction.RIGHT;
                break;
            case RIGHT:
                movimentoAbilitato=direction!=Direction.RIGHT && direction!=Direction.LEFT;
                break;

        }
        if(movimentoAbilitato)
        direction = newDirection;
    }

    // Controlla se il serpente ha mangiato una mela
    private boolean hasEatenApple() {
        // Implementa la logica per verificare se la testa del serpente
        // si sovrappone alla posizione della mela
        // ...

        return false; // Cambia in base alla tua implementazione
    }


    // Metodo principale per il movimento del serpente
    public void startSnakeMovement() {

        if(isRunning) {
            ui.access(() -> {
                moveSnake();
            });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startSnakeMovement();
            }
        }, snakeSpid);
        }

    }

    // Metodo per muovere il serpente
    private void moveSnake() {
            int yPoint = snake.getSnakeBody().get(0).getYc();
            int xPoint = snake.getSnakeBody().get(0).getXc();

            // Rimuovi il colore verde dalla cella attuale del serpente
            GridCell currentCell = (GridCell) ((HorizontalLayout) playGrid.getComponentAt(yPoint)).getComponentAt(xPoint);
            currentCell.setType(CellType.EMPTY);
            currentCell.getStyle().remove("background-color");

            // Calcola la nuova posizione della testa in base alla direzione
            switch (direction) {
                case RIGHT:
                    xPoint += 1;
                    break;
                case LEFT:
                    xPoint -= 1;
                    break;
                case UP:
                    yPoint -= 1;
                    break;
                case DOWN:
                    yPoint += 1;
                    break;
            }
            //verifico se non ho raggiunto la fine della griglia
            if (xPoint >= 0 && xPoint < gridSize
                    && yPoint >= 0 && yPoint < gridSize
            ) {
                List<Point> snakeBody = new ArrayList<>();
                snakeBody.add(new Point(xPoint, yPoint));
                snake.setSnakeBody(snakeBody);

                // Colora di verde la nuova cella del serpente
                GridCell newCell = (GridCell) ((HorizontalLayout) playGrid.getComponentAt(yPoint)).getComponentAt(xPoint);
                if(newCell.getType()==CellType.FOOD){
                    snake.setHeatenApples(snake.getHeatenApples()+1);
                    thereIsFood=false;
                    showFood();
                }
                newCell.setType(CellType.SNAKE);
                newCell.getStyle().set("background-color", "green");
            } else {
                stopSnakeMovement();
                gameOver();
            }


    }

    private void showFood(){
        List<Point> snakeBodyPositions = snake.getSnakeBody();
        Set<Integer> horizontalExcludePoints = new HashSet<Integer>();
        Set<Integer> verticalExcludePoints = new HashSet<Integer>();
        for(Point point:snakeBodyPositions){
            if(!horizontalExcludePoints.contains(point.getXc()))
            horizontalExcludePoints.add(point.getXc());

            if(!verticalExcludePoints.contains(point.getYc()))
                verticalExcludePoints.add(point.getYc());
        }
        int randomXpos = getRandomFreePos(horizontalExcludePoints);
        int randomYpos = getRandomFreePos(verticalExcludePoints);

        // Colora di rosso la nuova cella con la mela
        GridCell foodCell = (GridCell) ((HorizontalLayout) playGrid.getComponentAt(randomYpos)).getComponentAt(randomXpos);
        if(foodCell.getType()==CellType.EMPTY){
            foodCell.setType(CellType.FOOD);
            foodCell.getStyle().set("background-color", "red");
            thereIsFood=true;
        }


    }

    private int getRandomFreePos(Set<Integer> excludePoints){
        int randomPos = new Random().nextInt(0,gridSize);
        if(excludePoints.contains(randomPos)){
            getRandomFreePos(excludePoints);
        }
        return randomPos;
    }


    private void stopSnakeMovement() {
        isRunning = false;
        scheduler.shutdown();
    }

    private void gameOver(){
        System.out.println("###### GAME OVER BITCH ##########");
        endGameDialog.open();
    }

    private void reloadGame() {
        // Esegui il codice JavaScript per ricaricare la pagina
        ui.getCurrent().getPage().executeJs("location.reload()");
    }
}

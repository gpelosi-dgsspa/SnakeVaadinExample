package com.dgsspa.snake.ui;

import com.dgsspa.snake.enums.CellType;
import com.dgsspa.snake.enums.Direction;
import com.dgsspa.snake.utils.GridCell;
import com.dgsspa.snake.utils.Point;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

@Route("")
@AnonymousAllowed
public class SnakeGame extends VerticalLayout {

    private List<Point> snakeBody = new ArrayList<Point>();
    private int eatenApples=0;
    private Direction direction;
    private boolean isRunning = true;

    private boolean thereIsFood = false;

    private UI ui;

    private Timer timer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int gridSize = 24;
    private static final int defaultSnakePosition =5;
    private static final int snakeSpid = 500;

    private VerticalLayout playGrid;

    private Button startGame;
    private Dialog endGameDialog;
    private Span modalSpan;



    // Inizializza il gioco
    public SnakeGame() {
        ui = UI.getCurrent();
        direction = Direction.RIGHT;
        timer = new Timer();

        add(designEndGameDialog());
        add(designGameHeader());
        add(designGameBody());
        add(designGameFooter());
        setAlignItems(Alignment.CENTER);
        setSizeFull();

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
        gameFooter.setHeight("200px");
        gameFooter.getStyle().set("padding", "20px");
        startGame = new Button("START");

        Div controllerLayout = new Div();
        controllerLayout.getStyle().set("position", "relative");
        controllerLayout.setWidth("150px");
        controllerLayout.setHeight("150px");

        Button moveUp = new Button(new Icon(VaadinIcon.ARROW_UP));
        Button moveDown = new Button(new Icon(VaadinIcon.ARROW_DOWN));
        Button moveLeft = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        Button moveRight = new Button(new Icon(VaadinIcon.ARROW_RIGHT));

        moveUp.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ICON);
        moveDown.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ICON);
        moveLeft.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ICON);
        moveRight.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ICON);

        moveUp.getStyle().set("position", "absolute");
        moveUp.getStyle().set("left", "50%");
        moveUp.getStyle().set("top", "0");
        moveUp.getStyle().set("transform", "translate(-50%, 0)");
        moveUp.getStyle().set("padding", "10px");

        moveRight.getStyle().set("position", "absolute");
        moveRight.getStyle().set("left", "100%");
        moveRight.getStyle().set("top", "50%");
        moveRight.getStyle().set("transform", "translate(-100%, -50%)");
        moveRight.getStyle().set("padding", "10px");

        moveDown.getStyle().set("position", "absolute");
        moveDown.getStyle().set("left", "50%");
        moveDown.getStyle().set("bottom", "0");
        moveDown.getStyle().set("transform", "translate(-50%, 0)");
        moveDown.getStyle().set("padding", "10px");

        moveLeft.getStyle().set("position", "absolute");
        moveLeft.getStyle().set("left", "0");
        moveLeft.getStyle().set("top", "50%");
        moveLeft.getStyle().set("transform", "translate(0%, -50%)");
        moveLeft.getStyle().set("padding", "10px");

        controllerLayout.add(moveLeft);
        controllerLayout.add(moveDown);
        controllerLayout.add(moveUp);
        controllerLayout.add(moveRight);


        startGame.addClickListener(e -> startSnakeMovement());
        moveUp.addClickListener(e -> handleInput(Direction.UP));
        moveDown.addClickListener(e -> handleInput(Direction.DOWN));
        moveLeft.addClickListener(e -> handleInput(Direction.LEFT));
        moveRight.addClickListener(e -> handleInput(Direction.RIGHT));

        gameFooter.add(startGame);
        gameFooter.add(controllerLayout);

        startGame.setEnabled(true);
        moveUp.setEnabled(true);
        moveDown.setEnabled(true);
        moveLeft.setEnabled(true);
        moveRight.setEnabled(true);


        return gameFooter;
    }

    private Dialog designEndGameDialog(){
        endGameDialog = new Dialog();
        VerticalLayout modalLayout = new VerticalLayout();
        modalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        H1 modalTitle = new H1("GAME OVER");
        modalSpan = new Span("Eaten Apples: "+eatenApples);
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
        snakeBody.add(new Point(defaultSnakePosition+2,defaultSnakePosition));
        snakeBody.add(new Point(defaultSnakePosition+1,defaultSnakePosition));
        snakeBody.add(new Point(defaultSnakePosition,defaultSnakePosition));
        for (int y = 0; y < gridSize; y++) {
            HorizontalLayout gridRow = new HorizontalLayout();
            gridRow.getStyle().set("gap", "0");
            for (int x = 0; x < gridSize; x++) {
                GridCell cell = new GridCell(x,y, CellType.EMPTY);
                gridRow.add(initializeSnake(cell));
            }
            playGrid.add(gridRow);
        }

    }

    private GridCell initializeSnake(GridCell cell){
        for(Point point:snakeBody){
          if(cell.getXc()== point.getXc() && cell.getYc()==point.getYc())  {
              cell.setType(CellType.SNAKE);
              cell.getStyle().set("background-color", "green");
          }
        }
        return cell;
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
    private void startSnakeMovement() {
        startGame.setEnabled(false);
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
            int yPoint = snakeBody.get(0).getYc();
            int xPoint = snakeBody.get(0).getXc();


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
            //verifico se non ho raggiunto la fine della griglia altrimenti è game over
            if (xPoint >= 0 && xPoint < gridSize
                    && yPoint >= 0 && yPoint < gridSize
            ) {
                //Analizzo la prossima cella su cui dovrebbe finire il serpente che cos'è
                GridCell newCell = (GridCell) ((HorizontalLayout) playGrid.getComponentAt(yPoint)).getComponentAt(xPoint);

                //se è del tipo SNAKE, significa che il serpente ha toccato se stesso, e quindi è gameover
                if(newCell.getType()==CellType.SNAKE){
                    stopSnakeMovement();
                    gameOver();
                }
                // Se è tipo FOOD, significa che il serpente ha appena mangiato la mela
                if(newCell.getType()==CellType.FOOD){
                    //System.out.println("Nuova mela mangiata!");

                    //incremento il contatore delle mele mangiate
                    ++eatenApples;
                    modalSpan.setText("Eaten Apples: "+eatenApples);

                    //incremento di un'unita il serpente, spostandolo in avanti
                    addSnakeSegment(newCell);

                    //mi ricalcolo lunghezza e posizione del serpente
                    recalculateSnakeBody(xPoint,yPoint,snakeBody.size());

                    //faccio apparire una nuova mela
                    showFood();
                }
                // Se era semplicemente vuota, allora il serpente si sta semplicemente spostando
                if(newCell.getType()==CellType.EMPTY){
                    //incremento di un'unita il serpente, spostandolo in avanti
                    addSnakeSegment(newCell);

                    //rimuovo la coda del serpente
                    int xTailPoint = snakeBody.get(snakeBody.size()-1).getXc();
                    int yTailPoint = snakeBody.get(snakeBody.size()-1).getYc();

                    GridCell tailCell = (GridCell) ((HorizontalLayout) playGrid.getComponentAt(yTailPoint)).getComponentAt(xTailPoint);
                    removeSnakeSegment(tailCell);

                    //mi ricalcolo lunghezza e posizione del serpente
                    recalculateSnakeBody(xPoint,yPoint,snakeBody.size()-1);
                }


            } else {
                stopSnakeMovement();
                gameOver();
            }


    }

    private void recalculateSnakeBody(int xPoint,int yPoint, int snakeLength){
        //ricalcolo il corpo del serpente aggiungendo la nuova testa, e rimuovendo la coda se non ha mangiato la mela
        List<Point> newSnakeBody = new ArrayList<>();
        newSnakeBody.add(new Point(xPoint,yPoint));
        int i=0;
        while(i<snakeLength){
            newSnakeBody.add(snakeBody.get(i));
            i++;
        }
        snakeBody = newSnakeBody;
    }

    private void addSnakeSegment(GridCell snakeCell){
        snakeCell.setType(CellType.SNAKE);
        snakeCell.getStyle().set("background-color", "green");
    }

    private void removeSnakeSegment(GridCell snakeCell){
        snakeCell.setType(CellType.EMPTY);
        snakeCell.getStyle().remove("background-color");
    }

    private void showFood(){

        Set<Integer> horizontalExcludePoints = new HashSet<Integer>();
        Set<Integer> verticalExcludePoints = new HashSet<Integer>();
        for(Point point:snakeBody){
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
        //System.out.println("###### GAME OVER BITCH ##########");
        endGameDialog.open();
    }

    private void reloadGame() {
        // Esegui il codice JavaScript per ricaricare la pagina
        ui.getCurrent().getPage().executeJs("location.reload()");
    }
}

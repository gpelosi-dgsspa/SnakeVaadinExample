package com.dgsspa.snake.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Snake {

    private List<Point> snakeBody;
    private Integer heatenApples;

    public Snake(){
        super();
        snakeBody = new ArrayList<Point>(); // Inizializzo il serpente
        snakeBody.add(new Point(5,5)); //gli metto solo la testa e la posiziono al centro della griglia
        heatenApples = 0; //conteggio mele mangiate, inzialmente zero
    }
}

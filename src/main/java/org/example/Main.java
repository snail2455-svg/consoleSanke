package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main implements NativeKeyListener {
    private static final int snakeOriginalLength = 4;
    private static final List<Point> pointsOfSanke = new ArrayList<>();
    private static int direction = DirectionEnum.RIGHT.getDirection();
    private static int direction_last = 0;
    private static final int boundaryLength = 30;
    private static final int boundaryWidth = 10;
    private static final int speed = 300;

    public void nativeKeyPressed(NativeKeyEvent e) {
        //You could find every key's value by the sentence below
//         System.out.println("Key Pressed: " +NativeKeyEvent.getKeyText(e.getKeyCode()));

        //Monitor the keyboard
        switch (e.getKeyCode()) {
            //上
            case 57424:
                direction_last = direction;
                direction = DirectionEnum.UP.getDirection();
                break;
            case 57416:
                direction_last = direction;
                System.out.println("Key Pressed: " +NativeKeyEvent.getKeyText(e.getKeyCode()));
                direction = DirectionEnum.DOWN.getDirection();

                break;
            case 57419:
                direction_last = direction;
                direction = DirectionEnum.LEFT.getDirection();
                break;
            case 57421:
                direction_last = direction;
                direction = DirectionEnum.RIGHT.getDirection();
                System.out.println("Key Pressed: " +NativeKeyEvent.getKeyText(e.getKeyCode()));

                break;
            //Esc's code
            case 1:
                direction = 0;
                break;
        }

        //Control the direction to make it not U-turn
        if (direction == -direction_last) {
            direction = direction_last;
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();

            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        //register key listener
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new Main());
        //Start game
        System.out.print("snake game started");
        System.out.println("\r ");
        Point food = generateFood();
        //Generate snake's original coordinate
        initPoints(direction, food);
        int score = 0;
        while (true) {
            //Determine head's next move
            int headX = 0;
            int headY = 0;
            if (direction == DirectionEnum.RIGHT.getDirection()) {
                headX = pointsOfSanke.getFirst().getX() + 1;
                headY = pointsOfSanke.getFirst().getY();

            } else if (direction == DirectionEnum.LEFT.getDirection()) {
                headX = pointsOfSanke.getFirst().getX() - 1;
                headY = pointsOfSanke.getFirst().getY();

            } else if (direction == DirectionEnum.UP.getDirection()) {
                headX = pointsOfSanke.getFirst().getX();
                headY = pointsOfSanke.getFirst().getY() + 1;

            } else if (direction == DirectionEnum.DOWN.getDirection()) {
                headX = pointsOfSanke.getFirst().getX();
                headY = pointsOfSanke.getFirst().getY() - 1;

            }

            //Determine whether food has been eaten
            if (headX == food.getX() && headY == food.getY()) {
                Point tail = new Point(headX, headY, pointsOfSanke.getLast());
                pointsOfSanke.add(tail);
                score++;
                food = generateFood();
            }

            //Set the coordinates of points of snake
            for (int a = pointsOfSanke.size() - 1; a > 0; a--) {
                pointsOfSanke.get(a).setX(pointsOfSanke.get(a - 1).getX());
                pointsOfSanke.get(a).setY(pointsOfSanke.get(a - 1).getY());
            }
            pointsOfSanke.getFirst().setX(headX);
            pointsOfSanke.getFirst().setY(headY);

            //Determine the snake has bitten itself.
            boolean match = false;
            for (int i = 0; i < pointsOfSanke.size(); i++) {
                for (int j = i + 1; j < pointsOfSanke.size(); j++) {
                    if (pointsOfSanke.get(i).compareTo(pointsOfSanke.get(j)) == 0) {
                        match = true;
                        break;
                    }
                }
            }
            //Determine whether it is out of boundary or bitting itself.
            if(direction == 0 || match || headX >= boundaryLength || headX <= 0 || headY >= boundaryWidth || headY <= 0){
                System.out.print(pointsOfSanke.toString());
                System.out.println(headX + " " + headY + " " + match + direction);
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("GAME OVER,Your score is: " + score);
                System.exit(0);
            }

            //Clean the console(must run in console)
            System.out.print("\033[H\033[2J");
            System.out.flush();
            //Draw the graph
            printTitle(score);
            for (int i = 1; i <= boundaryWidth; i++) {
                for (int j = 1; j <= boundaryLength; j++) {
                    if (i == 1 || i == boundaryWidth) {
                        if (j == 1 || j == boundaryLength)
                            System.out.print("+");
                        else {
                            System.out.print("-");
                        }
                    } else {
                        if (j == 1 || j == boundaryLength)
                            System.out.print("|");
                        else {
                            int count = 0;
                            //Print snake consists of points
                            for (Point point : pointsOfSanke) {
                                if (point.getX() == j && point.getY() == i) {
                                    if (count == 0) {
                                        //Print head
                                        System.out.print("0");
                                    } else {
                                        //Print body
                                        System.out.print("*");
                                    }
                                    break;
                                }
                                count++;
                            }
                            //Print food
                            if (food.getX() == j && food.getY() == i) {
                                System.out.print("@");
                            } else {
                                // If not point printed,then print blackSpace
                                if (count == pointsOfSanke.size()) {
                                    System.out.print(" ");
                                }
                            }

                        }
                    }
                }

                System.out.println();

            }
            printPrompt();

            //Control the speed
            Thread.sleep(speed);


        }
    }

    private static void printPrompt() {
        System.out.print("Press Esc to exit");
    }

    private static void printTitle(int score) {
        for (int w = 0; w < boundaryLength - 6; w++) {
            System.out.print(" ");
        }
        System.out.println("score:"+ score);
    }

    private static Point generateFood() {
        Random random = new Random();
        int x;
        int y;
        while(true) {
             x = random.nextInt(1,boundaryLength);
             y = random.nextInt(1,boundaryWidth);
            int finalX = x;
            int finalY = y;
            boolean match = pointsOfSanke.stream().anyMatch(point -> point.getX()== finalX && point.getY() == finalY);
             if (!match) {
                 break;
             }
        }
        System.out.print("foodX:"+x+" foodY:"+y);
        return new Point(x, y);
    }

    private static void initPoints(int direction, Point food) {
        Random random = new Random();
        int headX;
        int headY;

        while (true) {
            headX = random.nextInt(snakeOriginalLength,boundaryLength - Main.snakeOriginalLength);
            headY = random.nextInt(snakeOriginalLength,boundaryWidth - Main.snakeOriginalLength);
            List<Point> temp = new ArrayList<>();
            temp.add(new Point(headX, headY));
            for (int i = 1; i < Main.snakeOriginalLength; i++) {
                if (direction == DirectionEnum.RIGHT.getDirection()) {
                    temp.add(new Point(headX - i, headY, temp.get(i - 1)));
                } else if (direction == DirectionEnum.LEFT.getDirection()) {
                    temp.add(new Point(headX + i, headY, temp.get(i - 1)));
                } else if (direction == DirectionEnum.UP.getDirection()) {
                    temp.add(new Point(headX, headY + 1, temp.get(i - 1)));
                } else if (direction == DirectionEnum.DOWN.getDirection()) {
                    temp.add(new Point(headX, headY - 1, temp.get(i - 1)));
                }
            }
            //Make sure food is not located at the position of snake
            boolean match = temp.stream().anyMatch(p -> p.getX() == food.getX() || p.getY() == food.getY());
            if (!match) {
                pointsOfSanke.addAll(temp);
                break;
            }

        }


    }


}
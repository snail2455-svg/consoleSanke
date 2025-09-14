package org.example;

public class Point implements Comparable<Point> {
    @Override
    public int compareTo(Point o) {
        if(x == o.x && y == o.y) {return 0;}
        else if(x < o.x) {return -1;}
        else{return 1;}
    }

    @Override
    public String toString() {
        String string ="x:"+x+"y:"+y+";";
        return string;
    }

    private int x;
    private int y;
    private Point next;
    public Point(int x, int y, Point next) {
        this.x = x;
        this.y = y;
        this.next = next;
    }
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getNext() {
        return next;
    }

    public void setNext(Point next) {
        this.next = next;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}

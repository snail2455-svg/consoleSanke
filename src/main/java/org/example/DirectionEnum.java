package org.example;

public enum DirectionEnum {
    UP(1),
    DOWN(-1),
    LEFT(2),
    RIGHT(-2);
    private final Integer direction;
    DirectionEnum(Integer direction) {
        this.direction = direction;
    }
    public Integer getDirection() {
        return direction;
    }
}

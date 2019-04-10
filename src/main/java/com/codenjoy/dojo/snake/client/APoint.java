package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Point;

public class APoint {

    private int x;
    private int y;

    public APoint(Point p) {
        this(p.getX(), p.getY());
    }

    public APoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    APoint move(APoint delta) {
        return new APoint(
                this.x + delta.x,
                this.y + delta.y);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APoint point = (APoint) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[x:%2d, y:%2d]", x, y);
    }

}
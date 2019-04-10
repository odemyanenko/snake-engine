
package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Point;

import java.util.*;
import java.util.stream.Collectors;

public class Lee {
    private final List<APoint> deltas = new ArrayList<APoint>() {{
        add(new APoint(0, -1));
        add(new APoint(-1, 0));
        add(new APoint(+1, 0));
        add(new APoint(0, +1));
    }};
    private final static int OBSTACLE = -10;
    private final static int START = -1;
    private final int dimX;
    private final int dimY;
    private int[][] data;

    public Lee(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.data = new int[dimY][dimX];
    }

    public String element(APoint p, boolean isFinal, List<APoint> path) {
        int val = get(p);
        if (val == OBSTACLE) {
            return " XX ";
        }
        if (!isFinal) {
            return String.format("%3d ", val);
        } else if (path.contains(p)) {
            return String.format("%3d ", val);
        } else {
            return "    ";
        }
    }

    void printMe() {
        printMe(false, new ArrayList<>());
    }

    void printMe(boolean isFinal, List<APoint> path) {
        for (int row = 0; row < dimY; row++) {
            for (int col = 0; col < dimX; col++) {
                APoint p = new APoint(col, row);
                System.out.printf(element(p, isFinal, path));
            }
            System.out.println();
        }
        System.out.println();
    }

    int get(APoint p) {
        return this.data[p.y()][p.x()];
    }

    void set(APoint p, int val) {
        this.data[p.y()][p.x()] = val;
    }

    boolean isOnBoard(APoint p) {
        return p.x() >= 0 && p.x() < dimX && p.y() >= 0 && p.y() < dimY;
    }

    boolean isUnvisited(APoint p) {
        return get(p) == 0;
    }

    Set<APoint> neighbors(APoint point) {
        return deltas.stream()
                .map(d -> d.move(point))
                .filter(p -> isOnBoard(p))
                .collect(Collectors.toSet());
    }

    Set<APoint> neighborsUnvisited(APoint point) {
        return neighbors(point).stream()
                .filter(p -> isUnvisited(p))
                .collect(Collectors.toSet());
    }

    APoint neighborByValue(APoint point, int value) {
        return neighbors(point).stream()
                .filter(p -> get(p) == value)
                .findFirst()
                .get();
    }

    public void setObstacle(int x, int y) {
        setObstacle(new APoint(x, y));
    }

    void setObstacle(APoint p) {
        set(p, OBSTACLE);
    }

    public Optional<List<APoint>> trace(Point start, Point finish) {
        return trace(new APoint(start), new APoint(finish));
    }

    public Optional<List<APoint>> trace(APoint start, APoint finish) {
        boolean found = false;
        set(start, START);
        Set<APoint> curr = new HashSet<>();
        int counter[] = new int[]{0};
        curr.add(start);
        while (!curr.isEmpty() && !found) {
            counter[0]++;
            Set<APoint> next = curr.stream().map(p -> neighborsUnvisited(p)).flatMap(Collection::stream).collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            if (next.contains(finish)) {
                found = true;
            }
//            printMe();
            curr.clear();
            curr.addAll(next);
            next.clear();
        }

        if (found) {
            set(start, 0);
            ArrayList<APoint> path = new ArrayList<>();
            path.add(finish);
            APoint curr_p = finish;
            while (counter[0] > 0) {
                counter[0]--;
                APoint prev_p = neighborByValue(curr_p, counter[0]);
                path.add(prev_p);
                curr_p = prev_p;
            }
            Collections.reverse(path);
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
//        BoardLee b = new BoardLee(15, 10);
//        APoint p1 = new APoint(0, 0);
//        APoint p2 = new APoint(14, 9);
//        b.setObstacle(new APoint(5,0));
//        b.setObstacle(new APoint(5,1));
//        b.setObstacle(new APoint(5,2));
//        b.setObstacle(new APoint(5,3));
//        b.setObstacle(new APoint(5,4));
//        b.setObstacle(new APoint(5,5));
//        b.setObstacle(new APoint(5,6));
//
//        b.setObstacle(new APoint(10,4));
//        b.setObstacle(new APoint(10,5));
//        b.setObstacle(new APoint(10,6));
//        b.setObstacle(new APoint(10,7));
//        b.setObstacle(new APoint(10,8));
//        b.setObstacle(new APoint(10,9));
//        b.printMe();
//        Optional<List<APoint>> result = b.trace(p1, p2);
//        if (result.isPresent()) {
//            System.out.println("Path has been found");
//            List<APoint> path = result.get();
//            path.forEach(System.out::println);
//            b.printMe(true, path);
//        } else {
//            System.out.println("Trace can't be found");
//        }
    }

}
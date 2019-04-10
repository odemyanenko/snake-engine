package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    private Direction prev_dir = Direction.UP;

    private int dimX, dimY = 0;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {

        this.board = board;
        List<Point> walls = board.getWalls();

        dimX = walls.stream().mapToInt(Point::getX).max().getAsInt() + 1;
        dimY = walls.stream().mapToInt(Point::getY).max().getAsInt() + 1;

        List<Point> snake = board.getSnake();
        Point stone = board.getStones().get(0);
        Point apple = board.getApples().get(0);

        ArrayList<Point> obstacles = new ArrayList<>();
        obstacles.addAll(walls);
        obstacles.addAll(snake);
        obstacles.add(stone);

        Lee lee = new Lee(dimX, dimY);
        APoint next_step;
        obstacles.forEach(o -> lee.setObstacle(o.getX(), board.inversionY(o.getY())));
        lee.printMe();
        Optional<List<APoint>> trace_apple_opt =
                lee.trace(board.getHead(), board.getApples().get(0));
        if (!trace_apple_opt.isPresent()) {
            Optional<List<APoint>> trace_stone_opt =
                    lee.trace(board.getHead(), stone);
            if (!trace_stone_opt.isPresent()) {
                //return Direction.UP.toString();
                APoint destPoint = new APoint(board.getHead().getX(), board.getHead().getY() + 1);
                return convert(destPoint, board.getHead(), stone, obstacles).toString();
            }
            List<APoint> trace_stone = trace_stone_opt.get();
            next_step = trace_stone.get(1);

            return convert(next_step, board.getHead(), apple, obstacles).toString();
        }
        List<APoint> trace_apple = trace_apple_opt.get();
        next_step = trace_apple.get(1);

        // System.out.println(board.getHead());
        System.out.println(trace_apple);
        System.out.println(obstacles);

        System.out.printf("next_step: %s; head: %s", next_step, board.getHead());

        prev_dir = convert(next_step, board.getHead(), apple, obstacles);
        System.out.printf("Prevdir: %s\n", prev_dir);

        return prev_dir.toString();
    }

    boolean isObstacle(APoint next_step, ArrayList<Point> obstacles) {
        for (Point point : obstacles) {
            if (point.getX() == next_step.x() && point.getY() == next_step.y()) {
                return true;
            }
        }
        return false;
    }

    Direction convert(APoint dst, Point src, Point target, ArrayList<Point> obstacles) {
        System.out.printf("\ndest(%d;%d) src(%d;%d)\n", dst.x(), dst.y(), src.getX(), src.getY());

        if (dst.x() < 0 || dst.x() > dimX || dst.y() < 0 || dst.y() > dimY) {
            return prev_dir;
        }
        if (dst.x() < src.getX()) {
            if (isObstacle(dst, obstacles)) {//<
                if (target.getY() > src.getY() && !isObstacle(new APoint(src.getX(), src.getY() + 1), obstacles)) {
                    return Direction.UP;
                } else if (target.getY() < src.getY() && !isObstacle(new APoint(src.getX(), src.getY() - 1), obstacles)) {
                    return Direction.DOWN;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() + 1), obstacles)) {
                    return Direction.UP;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() - 1), obstacles)) {
                    return Direction.DOWN;
                } else if (!isObstacle(new APoint(src.getX() + 1, src.getY()), obstacles)) {
                    return Direction.RIGHT;
                }
            }
            return Direction.LEFT;
        }

        if (dst.x() > src.getX()) {
            if (isObstacle(dst, obstacles)) {//<
                if (target.getY() > src.getY() && !isObstacle(new APoint(src.getX(), src.getY() + 1), obstacles)) {
                    return Direction.UP;
                } else if (target.getY() < src.getY() && !isObstacle(new APoint(src.getX(), src.getY() - 1), obstacles)) {
                    return Direction.DOWN;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() + 1), obstacles)) {
                    return Direction.UP;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() - 1), obstacles)) {
                    return Direction.DOWN;
                } else if (!isObstacle(new APoint(src.getX() - 1, src.getY()), obstacles)) {
                    return Direction.LEFT;
                }
            }
            return Direction.RIGHT;
        }
        //if (board.inversionY(dst.y())>src.getY()) return Direction.DOWN;
        //if (board.inversionY(dst.y())<src.getY()) return Direction.UP;
        if (dst.y() > src.getY()) {
            if (isObstacle(dst, obstacles)) {
                if (target.getX() < src.getX() && !isObstacle(new APoint(src.getX() - 1, src.getY()), obstacles)) {
                    return Direction.LEFT;
                } else if (target.getX() > src.getX() && !isObstacle(new APoint(src.getX() + 1, src.getY()), obstacles)) {
                    return Direction.RIGHT;
                } else if (!isObstacle(new APoint(src.getX() - 1, src.getY()), obstacles)) {
                    return Direction.LEFT;
                } else if (!isObstacle(new APoint(src.getX() + 1, src.getY()), obstacles)) {
                    return Direction.RIGHT;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() - 1), obstacles)) {
                    return Direction.DOWN;
                }
            }
            return Direction.UP;
        }
        if (dst.y() < src.getY()) {
            if (isObstacle(dst, obstacles)) {
                if (target.getX() < src.getX() && !isObstacle(new APoint(src.getX() - 1, src.getY()), obstacles)) {
                    return Direction.LEFT;
                } else if (target.getX() > src.getX() && !isObstacle(new APoint(src.getX() + 1, src.getY()), obstacles)) {
                    return Direction.RIGHT;
                } else if (!isObstacle(new APoint(src.getX() - 1, src.getY()), obstacles)) {
                    return Direction.LEFT;
                } else if (!isObstacle(new APoint(src.getX() + 1, src.getY()), obstacles)) {
                    return Direction.RIGHT;
                } else if (!isObstacle(new APoint(src.getX(), src.getY() + 1), obstacles)) {
                    return Direction.UP;
                }
            }
            return Direction.DOWN;
        }
        return Direction.UP;
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://46.101.112.224/codenjoy-contest/board/player/jx4aj7bythwhfkibrex8?code=6463440002135754912",
                new YourSolver(new RandomDice()),
                new Board());
    }

}

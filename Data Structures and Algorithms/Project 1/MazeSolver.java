public class MazeSolver {
    public static StepStack findPath(Maze maze) throws MazeHasNoSolutionException {
        //TODO: Implement MazeSolver()
        StepStack path = new StepStack();
        int row = 0;
        int col = 0;
        boolean[][] visited = new boolean[maze.rows][maze.cols];
        while (!(row == (maze.rows-1) && col == (maze.cols-1))) {
            StepQueue queue = maze.mazeArray[row][col];
            visited[row][col] = true;
            Step s = null;
            boolean push = false;
            while (visited[row][col]) {
                try {
                    int size2 = queue.size();
                    s = queue.dequeue();
                    if (!queue.isEmpty()) {
                        if (s.name().equals("UP")) {
                            row--;
                        } else if (s.name().equals("DOWN")) {
                            row++;
                        } else if (s.name().equals("LEFT")) {
                            col--;
                        } else if (s.name().equals("RIGHT")) {
                            col++;
                        }
                        if (!visited[row][col]) {
                            path.push(s);
                        } else {
                            if (s.name().equals("UP")) {
                                row++;
                            } else if (s.name().equals("DOWN")) {
                                row--;
                            } else if (s.name().equals("LEFT")) {
                                col++;
                            } else if (s.name().equals("RIGHT")) {
                                col--;
                            }
                        }
                    } else if (size2 == 1) {
                        if (s.name().equals("UP")) {
                            row--;
                        } else if (s.name().equals("DOWN")) {
                            row++;
                        } else if (s.name().equals("LEFT")) {
                            col--;
                        } else if (s.name().equals("RIGHT")) {
                            col++;
                        }
                        if (!visited[row][col]) {
                            path.push(s);
                        } else {
                            if (s.name().equals("UP")) {
                                row++;
                            } else if (s.name().equals("DOWN")) {
                                row--;
                            } else if (s.name().equals("LEFT")) {
                                col++;
                            } else if (s.name().equals("RIGHT")) {
                                col--;
                            }
                        }
                    }
                } catch (EmptyQueueException e) {
                    Step q = null;
                    try {
                        q = path.peek();
                        path.pop();
                        visited[row][col] = false;

                        if (q.name().equals("UP")) {
                            row++;
                        } else if (q.name().equals("DOWN")) {
                            row--;
                        } else if (q.name().equals("LEFT")) {
                            col++;
                        } else if (q.name().equals("RIGHT")) {
                            col--;
                        }
                        if (path.size() <= 0) {
                            row = 0;
                            col = 0;
                            break;
                        }
                        break;
                    } catch (EmptyStackException ex) {
                        throw new MazeHasNoSolutionException();
                    }
                }
            }
        }
        return path;
    }
}

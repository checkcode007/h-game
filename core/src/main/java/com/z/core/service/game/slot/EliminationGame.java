package com.z.core.service.game.slot;

import java.util.*;

public class EliminationGame {

    private static final int ROWS = 5; // 行数
    private static final int COLS = 3; // 列数
    private static final String[] SYMBOLS_LIST = {"A", "B", "C", "D", "E","F","G"}; // 符号列表
    private static final Random random = new Random();
    private static final int MAX_TRIES = 15; // 生成符号最大尝试次数

    private final String[][] grid = new String[ROWS][COLS]; // 游戏网格
    private final int[] symbolWeights = {50, 30, 10, 5, 5}; // 符号权重

    /**
     * 初始化网格
     */
    public void initializeGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = generateValidSymbol(row, col);
            }
        }
    }

    /**
     * 显示当前网格
     */
    public void displayGrid() {
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                String symbol = "x:"+row+" y:"+col+"->"+((grid[row][col] == null ? " " : grid[row][col]) + " ");

                symbol =((grid[row][col] == null ? " " : grid[row][col]) + " ");
                System.out.print(symbol);


            }
            System.out.println();
        }
    }

    /**
     * 生成符号，根据权重随机选择
     */
    private String generateSymbolForGrid() {
        int totalWeight = Arrays.stream(symbolWeights).sum();
        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (int i = 0; i < SYMBOLS_LIST.length; i++) {
            currentWeight += symbolWeights[i];
            if (randomValue < currentWeight) {
                return SYMBOLS_LIST[i];
            }
        }
        return SYMBOLS_LIST[0]; // 默认返回
    }
    /**
     * 检查符号是否在当前位置形成消除
     * 规则：任意一行，从左到右，只要连续三个及以上相同符号则不可用。
     */
    private boolean isValidForGrid(int row, int col, String symbol) {
        // 横向检测：从左到右的行消除规则
        int consecutiveCount = 1; // 当前连续计数，包含当前位置

        // 检查当前符号左侧连续的相同符号个数
        for (int i = 0; i <ROWS; i++) {
            if (Objects.equals(symbol, grid[i][col])) {
                consecutiveCount++;
            } else {
                break; // 不相同的符号，终止向左检查
            }
        }

        // 如果当前符号与其左右形成了3个或以上连续的相同符号，则返回不可用
        if (consecutiveCount > 2) {
            return false;
        }

        // 如果横向符合规则，还需继续处理竖向（如果需要额外限制）
        // 示例：纵向规则禁止连续3个及以上符号的处理（可选）
    /*
    consecutiveCount = 1;

    // 检查当前符号上方连续相同符号
    for (int up = row - 1; up >= 0; up--) {
        if (Objects.equals(symbol, grid[up][col])) {
            consecutiveCount++;
        } else {
            break;
        }
    }

    // 检查当前符号下方连续相同符号
    for (int down = row + 1; down < ROWS; down++) {
        if (Objects.equals(symbol, grid[down][col])) {
            consecutiveCount++;
        } else {
            break;
        }
    }

    // 如果纵向也形成3个及以上连续符号，不合法
    if (consecutiveCount >= 3) {
        return false;
    }
    */

        return true; // 当前符号位置合法
    }

    /**
     * 检查符号是否在当前位置形成消除
     */
    private boolean isValidForGrid1(int row, int col, String symbol) {
        // 横向检测
        if (col >= 2 && Objects.equals(symbol, grid[row][col - 1]) && Objects.equals(symbol, grid[row][col - 2])) {
            return false;
        }
        if (col < COLS - 2 && Objects.equals(symbol, grid[row][col + 1]) && Objects.equals(symbol, grid[row][col + 2])) {
            return false;
        }
        if (col >= 1 && col < COLS - 1 && Objects.equals(symbol, grid[row][col - 1]) && Objects.equals(symbol, grid[row][col + 1])) {
            return false;
        }

        // 纵向检测
        if (row >= 2 && Objects.equals(symbol, grid[row - 1][col]) && Objects.equals(symbol, grid[row - 2][col])) {
            return false;
        }
        if (row < ROWS - 2 && Objects.equals(symbol, grid[row + 1][col]) && Objects.equals(symbol, grid[row + 2][col])) {
            return false;
        }
        if (row >= 1 && row < ROWS - 1 && Objects.equals(symbol, grid[row - 1][col]) && Objects.equals(symbol, grid[row + 1][col])) {
            return false;
        }

        return true;
    }

    /**
     * 在特定位置生成符号，确保不会形成消除
     */
    private String generateValidSymbol(int row, int col) {
        int tries = 0;
        while (tries < MAX_TRIES) {
            String symbol = generateSymbolForGrid();
            if (isValidForGrid(row, col, symbol)) {
                return symbol;
            }
            tries++;
        }
        throw new IllegalStateException("Unable to generate valid symbol after " + MAX_TRIES + " tries.");
    }

    /**
     * 补充空位并保证符号有效
     */
    private void fillEmptySlots() {
        for (int col = 0; col < COLS; col++) {
            for (int row = ROWS - 1; row >= 0; row--) {
                if (grid[row][col] == null) {
                    grid[row][col] = generateValidSymbol(row, col);
                }
            }
        }
    }

    /**
     * 模拟游戏步骤
     */
    public void simulateGame() {
        initializeGrid();
        System.out.println("Initial Grid:");
        displayGrid();

        // 消除和补充循环
        while (true) {
            List<int[]> eliminated = eliminateSymbols();
            if (eliminated.isEmpty()) {
                break; // 没有新的消除，结束
            }

            System.out.println("\nAfter Elimination:");
            displayGrid();

            fillEmptySlots();
            System.out.println("\nAfter Refilling:");
            displayGrid();
        }
    }

    /**
     * 消除网格中的符号
     *
     * @return 消除的符号位置列表
     */
    private List<int[]> eliminateSymbols() {
        List<int[]> eliminatedPositions = new ArrayList<>();
        boolean[][] marked = new boolean[ROWS][COLS]; // 标记被消除的符号

        // 检查消除规则
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 2; col++) {
                if (grid[row][col] != null && grid[row][col].equals(grid[row][col + 1]) && grid[row][col].equals(grid[row][col + 2])) {
                    marked[row][col] = marked[row][col + 1] = marked[row][col + 2] = true;
                }
            }
        }
        for (int row = 0; row < ROWS - 2; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != null && grid[row][col].equals(grid[row + 1][col]) && grid[row][col].equals(grid[row + 2][col])) {
                    marked[row][col] = marked[row + 1][col] = marked[row + 2][col] = true;
                }
            }
        }

        // 清空并记录被消除的符号
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (marked[row][col]) {
                    eliminatedPositions.add(new int[]{row, col});
                    grid[row][col] = null;
                }
            }
        }

        return eliminatedPositions;
    }

    public static void main(String[] args) {
        EliminationGame game = new EliminationGame();
//        game.simulateGame();
        for (int i = 0; i < 10; i++) {
            game.simulateGame();
            System.out.println("==================");
        }


    }
}

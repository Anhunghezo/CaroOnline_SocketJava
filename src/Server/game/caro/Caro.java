/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.game.caro;

import java.util.ArrayList;
import shared.helper.Line;
import shared.helper.Point;
import server.game.GameLogic;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class Caro extends GameLogic {

    final int ROW = 16, COL = 16;

    ArrayList<History> history;
    History preMove;
    String[][] board;

    public Caro() {
        history = new ArrayList<>();
        preMove = null;

        board = new String[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                board[i][j] = " ";
            }
        }
    }

    private void addHistory(int row, int col, String playerId) {
        History newHis = new History(row, col, playerId);
        history.add(newHis);
        preMove = newHis;
    }

    public boolean move(int row, int col, String playerId) {
        // nếu người này đã đánh trước đó thì không cho đánh nữa
        if (preMove != null && preMove.getPlayerId().equals(playerId)) {
            return false;
        }

        // nếu vị trí đánh nằm ngoài board
        if (row < 0 && row >= ROW && col < 0 && col >= COL) {
            return false;
        }

        // nếu vị trí đó đã đánh rồi
        if (!board[row][col].equals(" ")) {
            return false;
        }

        board[row][col] = playerId;
        addHistory(row, col, playerId);
        return true;
    }

    public String getValueAt(int x, int y) {
        if (x >= 0 && x < COL && y >= 0 && y < ROW) {
            return board[y][x];
        }

        return " ";
    }

    public Line CheckWin(int x, int y) {
        Point currentCell = new Point(x, y);
        Point backDir, frontDir;
        Line winPath;

        // ============ check chieu ngang =============
        backDir = new Point(-1, 0);
        frontDir = new Point(1, 0);
        winPath = this.checkWinTemplate(currentCell, backDir, frontDir);
        if (winPath != null) {
            return winPath;
        }
        // ============ check chieu doc ============
        backDir = new Point(0, -1);
        frontDir = new Point(0, 1);
        winPath = this.checkWinTemplate(currentCell, backDir, frontDir);
        if (winPath != null) {
            return winPath;
        }
        // ============ check cheo trai sang phai ============
        backDir = new Point(-1, -1);
        frontDir = new Point(1, 1);
        winPath = this.checkWinTemplate(currentCell, backDir, frontDir);
        if (winPath != null) {
            return winPath;
        }
        // ============ check cheo phai sang trai ============
        backDir = new Point(1, -1);
        frontDir = new Point(-1, 1);
        winPath = this.checkWinTemplate(currentCell, backDir, frontDir);
        if (winPath != null) {
            return winPath;
        }

        return null;
    }

    public Line checkWinTemplate(Point currentCell, Point backDir, Point frontDir) {
        // get data from current cell
        String currentData = this.getValueAt(currentCell.x, currentCell.y);

        // if there is nodata => out
        if (currentData.equals(" ")) {
            return null;
        }

        // đếm số lượng ô thỏa điều kiện (>= 5 ô liên tiếp là win)
        int count = 1;
        Point from, to, temp;

        // count to back
        from = currentCell;
        while (true) {
            temp = new Point(from.x + backDir.x, from.y + backDir.y);
            String data = this.getValueAt(temp.x, temp.y);

            if (!data.equals(currentData)) {
                break;
            }
            from = temp;
            count++;
        }

        // count to front
        to = currentCell;
        while (true) {
            temp = new Point(to.x + frontDir.x, to.y + frontDir.y);
            String data = this.getValueAt(temp.x, temp.y);

            if (!data.equals(currentData)) {
                break;
            }
            to = temp;
            count++;
        }

        // nếu có 5 ô giống nhau liên tiếp nhau => win
        if (count == 5) {
            return new Line(from.x, from.y, to.x, to.y);
        }

        return null;
    }
}

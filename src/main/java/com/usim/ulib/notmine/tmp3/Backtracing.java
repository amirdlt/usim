package com.usim.ulib.notmine.tmp3;

public class Backtracing
{
    private static final int N = 9;

    private int[][] board;

    public int[][] getBoard()
    {
        return board;
    }

    public boolean solve(int[][] board)
    {
        this.board = board;
        return solvable() && solve(0, 0);
    }

    private boolean solve(int row, int col)
    {

        if(row == N - 1 && col == N)
            return true;

        if(col == N)
        {
            col = 0;
            row++;
        }

        if(board[row][col] > 0)
        {
            if(legal(row, col))
                return solve(row, col + 1);
            else
                return false;
        }

        for(int move = 1;  move <= N; move++)
        {
            if(legal(row, col))
            {
                board[row][col] = move;
                return solve(row, col + 1);
            }
        }

        board[row][col] = 0;
        return false;
    }

    public boolean solvable()
    {
        for(int r = 0; r < N; r++)
            for(int c = 0; c < N; c++)
                if(!legal(r, c))
                    return false;

        return true;
    }

    private boolean legal(int row, int col)
    {
        return checkAxis(row, col) && checkArea(row, col);
    }

    private boolean checkAxis(int row, int col)
    {
        if(board[row][col] == 0)
            return true;

        for(int i = 0; i < N; i++)
        {
            if(board[row][i] ==  board[row][col] && i != col)
                return false;

            if(board[i][col] == board[row][col] && i != row)
                return false;
        }

        return true;
    }

    private boolean checkArea(int row, int col)
    {
        if(board[row][col] == 0)
            return true;

        int r = row - (row % 3);
        int c = col - (col % 3);

        for (int i = r; i < r + 3; i++)
        {
            for (int j = c; j < c + 3; j++)
            {
                if((i != row) && (j != col) && (board[i][j] == board[row][col]))
                    return false;
            }
        }

        return true;
    }
}


class NoSolutionException extends Exception
{
    public NoSolutionException(String message)
    {
        super(message);
    }
}

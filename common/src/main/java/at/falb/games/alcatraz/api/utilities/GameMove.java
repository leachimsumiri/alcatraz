package at.falb.games.alcatraz.api.utilities;


import at.falb.games.alcatraz.api.Prisoner;

import java.util.Objects;

public class GameMove {
    private int column;
    private int row;
    private int rowOrCol;
    private Prisoner prisoner;

    public GameMove(int column, int row, int rowOrCol, Prisoner prisoner) {
        this.column = column;
        this.row = row;
        this.rowOrCol = rowOrCol;
        this.prisoner = prisoner;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public Prisoner getPrisoner() {
        return prisoner;
    }

    public int getRowOrCol() {
        return rowOrCol;
    }

    public void setPrisoner(Prisoner prisoner) {
        this.prisoner = prisoner;
    }

    public void setRowOrCol(int rowOrCol) {
        this.rowOrCol = rowOrCol;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMove gameMove = (GameMove) o;
        return gameMove.getColumn() == column && Objects.equals(gameMove.getRowOrCol(), rowOrCol) &&
                gameMove.getRow() == row && Objects.equals(gameMove.getPrisoner(), prisoner);
    }

    @Override
    public String toString() {
        return "Position{" +
                "col='" + column +
                ", row='" + row +
                ", Prisoner=" + "\'" + prisoner.toString() + "\'" +
                ", rowOrCol=" + prisoner.getRow() +
                '}';
    }
}



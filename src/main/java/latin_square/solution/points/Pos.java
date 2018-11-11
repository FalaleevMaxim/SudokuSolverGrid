package latin_square.solution.points;

public class Pos {
    public final int str;
    public final int col;

    public Pos(int str, int col) {
        this.str = str;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pos)) return false;

        Pos pos = (Pos) o;

        if (str != pos.str) return false;
        return col == pos.col;
    }

    @Override
    public int hashCode() {
        int result = str;
        result = 31 * result + col;
        return result;
    }
}

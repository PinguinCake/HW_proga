package mapeditor;

public class CellReport {
    public int x, y;
    public String zone;
    public String obstacleType;
    public String symbol;
    public int gold;

    public CellReport(int x, int y, String zone, String obstacleType, String symbol, int gold) {
        this.x = x;
        this.y = y;
        this.zone = zone;
        this.obstacleType = obstacleType;
        this.symbol = symbol;
        this.gold = gold;
    }
}

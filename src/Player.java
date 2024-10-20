public class Player {
  protected final String name;
  protected final Side side;

  public Player(String name, Side side) {
    this.name = name;
    this.side = side;
  }

  public Side getSide() {
    return side;
  }

  public String getName() {
    return name;
  }
}

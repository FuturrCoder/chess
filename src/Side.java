public enum Side {
  BLACK, WHITE;

  public Side invert() {
    switch (this) {
      case BLACK:
        return WHITE;
      case WHITE:
        return BLACK;
      default:
        throw new IllegalStateException("Unexpected value: " + this);
    }
  }

  // 0 if white, 1 if black
  public int id() {
    switch (this) {
      case BLACK:
        return 1;
      case WHITE:
        return 0;
      default:
        throw new IllegalStateException("Unexpected value: " + this);
    }
  }
}

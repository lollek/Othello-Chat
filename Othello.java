class Othello {
  private final int board_size;
  private char[] board;

  public Othello(int board_size) {
    this.board_size = board_size;
    this.board = new char[board_size * board_size];
    resetBoard();
  }

  public int board_size() {
    return this.board_size;
  }

  public String toString() {
    StringBuffer strbuf = new StringBuffer();
    for (int i = 0; i < board_size; ++i) {

      // Top border
      if (i == 0) {
        strbuf.append("  ");
        for (int j = 0; j < board_size; ++j) {
          strbuf.append((char)('a' + j));
          strbuf.append(" ");
        }
        strbuf.append("\n");
      }

      // Game board
      strbuf.append(i + 1);
      strbuf.append("|");
      for (int j = 0; j < board_size; ++j) {
        char chip = board[j + i*board_size];
        strbuf.append(chip == 0 ? ' ' : chip);
        strbuf.append("|");
      }
      strbuf.append("\n");

      // Divider
      strbuf.append(" ");
      for (int j = 0; j < board_size; ++j) {
        strbuf.append("--");
      }
      strbuf.append("-\n");
    }
    return strbuf.toString();
  }

  public boolean putChip(char team, char x, char y) {
    x -= 'a';
    y -= '1';
    if (board[x + y*board_size] == 0) {
      board[x + y*board_size] = team;
      return true;
    } else {
      return false;
    }
  }

  private void resetBoard() {
    for (int i = 0; i < board_size; ++i) {
      for (int j = 0; j < board_size; ++j) {
        board[j + i*board_size] = 0;
      }
    }
    board[(board_size/2) -1 + (board_size/2)*board_size] = 'X';
    board[(board_size/2) + (board_size/2)*board_size] = 'O';
    board[(board_size/2) -1 + (board_size/2 -1)*board_size] = 'O';
    board[(board_size/2) + (board_size/2 -1)*board_size] = 'X';
  }
}

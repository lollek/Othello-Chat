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

    boolean action_is_possible = false;

    // Check for possible moves, and execute them
    if (board[x + y*board_size] == 0) {
      for (char i = 0; i < board_size; ++i) {
        for (char j = 0; j < board_size; ++j) {
          if (board[i + j*board_size] == team) {
            if (rayCheck(false, x, y, i, j)) {
              rayCheck(true, x, y, i, j);
              action_is_possible = true;
            }
          }
        }
      }
    }
    return action_is_possible;
  }

  private boolean rayCheck(boolean flip, char x, char y, char i, char j) {
    char team = board[i + j*board_size];
    char other_team = team  == 'X' ? 'O' : 'X';

    int dx = x - i;
    int dy = y - j;
    if ((x == i && y == j) ||
        ((-1 <= dx && dx <= 1) &&
         (-1 <= dy && dy <= 1))) {
      return false;
    }

    if (dx > 0) {
      dx = 1;
    } else if (dx < 0) {
      dx = -1;
    }

    if (dy > 0) {
      dy = 1;
    } else if (dy < 0) {
      dy = -1;
    }

    // Check x axis
    if (x == i) {
      if (flip) {
        board[x + y*board_size] = team;
      }
      for (int k = y - dy; k != j; k -= dy) {
        if (other_team != board[x + k*board_size]) {
          return false;
        } else if (flip) {
          board[x + k*board_size] = team;
        }
      }
      return true;

    // Check y axis
    } else if (y == j) {
      if (flip) {
        board[x + y*board_size] = team;
      }
      for (int k = x - dx; k != i; k -= dx) {
        if (other_team != board[k + y*board_size]) {
          return false;
        } else if (flip) {
          board[k + y*board_size] = team;
        }
      }
      return true;

    // Check diagonal
    /*
    } else if (x - i == y - j) {
      for (int k = x; k != i; k += dx) {
        if (other_team != board[x + dx*k + (y + dy*k)*board_size]) {
          System.out.println("Same diagonal but..no");
          return false;
        }
      }
      return true;
    */
    }
    return false;
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

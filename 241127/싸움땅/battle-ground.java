import java.util.*;
import java.io.*;

/**
 * 플레이어 이동 방식
 * - 방향으로 1칸 이동(단, 격자를 나가면 반대로 1칸 이동)
 *
 * 이동 후
 * - 총이 있는 경우
 *  - 총을 획득(만약, 총이 있다면? 공격력이 쎈 총을 유지 후 나머지 총들은 격자에 버리기)
 * - 플레이어가 있는 경우
 *  - (초기 능력치 + 공격력)이 높은 플레이어 WIN
 *  - 같다면? 초기 능력치가 높은 플레이어 승
 *  - 대결 후 점수(초기 능력치 + 공격력 합의 차이) 획득
 *  - 대결 후 플레이어 행동
 *   - LOSE : 총 버리고, 기존 방향으로 한 칸 이동
 *    - 플레이어 or 격자 : 오른쪽으로 90도 회전하면서 이동
 *    - 총 : 가장 공격력이 높은 총을 획득 후 나머지 총 버리기
 *   - WIN : 가장 높은 공격력을 지닌 총 획득(현재 위치 내가 가진 총 비교)
 */

public class Main {

	public static final int[] dr = {-1, 0, 1, 0};
	public static final int[] dc = {0, 1, 0, -1};

	public static final int WIN = 1;
	public static final int LOSE = 2;

	public static class Player {

		int number;
		int row;
		int col;
		int direction;
		int stat;
		int gun;
		int score;

		Player(int n, int r, int c, int d, int s) {
			this.number = n;
			this.row = r;
			this.col = c;
			this.direction = d;
			this.stat = s;
		}

		public void move() {

			int nRow = row + dr[this.direction];
			int nCol = col + dc[this.direction];

			// 범위를 벗어나는 경우(방향을 반대로 바꾸고 다시 이동)
			if (outRange(nRow, nCol)) {
				this.direction = (this.direction + 2) % 4;
				move();
				return;
			}

			// 이동
			this.row = nRow;
			this.col = nCol;

		}

		public void win(int power) {
			this.score += Math.abs((this.getPower() - power));
			if (canSwap(this, this.row, this.col)) {
				swapGun(this, this.row, this.col);
			}
		}

		public void lose() {

			// 총 격자에 내려놓기
			grid[this.row].get(this.col).offer(this.gun);
			this.gun = 0;

			// 현재 위치에서 현재 방향으로 이동
			int nRow = this.row + dr[this.direction];
			int nCol = this.col + dc[this.direction];

			// 이동할 방향이 격자 밖이거나 플레이어가 존재하는 경우
			if (outRange(nRow, nCol) || isExists(nRow, nCol)) {
				int dir = (this.direction + 1) % 4;
				while(true) {
					int nnRow = this.row + dr[dir];
					int nnCol = this.col + dc[dir];
					if (!outRange(nnRow, nnCol) && !isExists(nnRow, nnCol)) {
						this.row = nnRow;
						this.col = nnCol;
						this.direction = dir;
						break;
					}
					dir = (dir + 1) % 4;
				}
			} else {
				this.row = nRow;
				this.col = nCol;
			}

		}

		public boolean isExists(int row, int col) {
			for (Player player : players) {
				if (player.row == row && player.col == col) {
					return true;
				}
			}
			return false;
		}

		public boolean outRange(int row, int col) {
			return row < 0 || size <= row || col < 0 || size <= col;
		}

		public int getPower() {
			return stat + gun;
		}

	}

	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;

	static int size;
	static int playerCount;
	static int roundCount;

	static List<PriorityQueue<Integer>>[] grid;
	static Player[] players;

	public static void main(String[] args) throws IOException {

		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();

		input();

		for (int rCount = 0; rCount < roundCount; rCount++) {

			for (Player player : players) {

				// 이동
				player.move();

				// 이동 후
				int cRow = player.row;
				int cCol = player.col;

				List<Player> candidates = getCandidates(cRow, cCol);
				if (candidates.size() == 2) {

					Player p1 = candidates.get(0);
					Player p2 = candidates.get(1);

					switch (judge(p1, p2)) {
						case WIN:
							handleBattle(p1, p2);
							break;
						case LOSE:
							handleBattle(p2, p1);
							break;
					}

				}

				else if (canSwap(player, cRow, cCol)) {
					swapGun(player, cRow, cCol);
				}


			}

		}

		for (Player player : players) {
			sb.append(player.score).append(" ");
		}

		bw.write(sb.toString());
		bw.close();

	}

	public static int judge(Player p1, Player p2) {

		if (p1.getPower() > p2.getPower()) {
			return WIN;
		}
		else if (p1.getPower() < p2.getPower()) {
			return LOSE;
		}
		else {
			if (p1.stat > p2.stat) {
				return WIN;
			} else {
				return LOSE;
			}
		}

	}

	public static List<Player> getCandidates(int row, int col) {

		List<Player> candidates = new ArrayList<>();

		for (Player player : players) {
			if (player.row == row && player.col == col) {
				candidates.add(player);
			}
		}

		return candidates;

	}

	public static boolean canSwap(Player p, int row, int col) {
		return !grid[row].get(col).isEmpty() && p.gun < grid[row].get(col).peek();
	}

	public static void swapGun(Player p, int row, int col) {
		int tmp = p.gun;
		p.gun = grid[row].get(col).poll();
		grid[row].get(col).offer(tmp);
	}

	public static void handleBattle(Player winner, Player loser) {
		int power = loser.getPower();
		loser.lose();
		winner.win(power);
		swapGun(loser, loser.row, loser.col);
	}

	public static void input() throws IOException {

		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		playerCount = Integer.parseInt(inputs[1]);
		roundCount = Integer.parseInt(inputs[2]);

		grid = new ArrayList[size];

		for (int row = 0; row < size; row++) {
			grid[row] = new ArrayList<>();
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < size; col++) {
				grid[row].add(new PriorityQueue<>(Collections.reverseOrder()));
				grid[row].get(col).offer(Integer.parseInt(inputs[col]));
			}
		}

		players = new Player[playerCount];
		for (int pCount = 0; pCount < playerCount; pCount++) {
			inputs = br.readLine().trim().split(" ");

			int r = Integer.parseInt(inputs[0]) - 1;
			int c = Integer.parseInt(inputs[1]) - 1;
			int d = Integer.parseInt(inputs[2]);
			int s = Integer.parseInt(inputs[3]);

			players[pCount] = new Player(pCount + 1, r, c, d, s);
		}

	}

}
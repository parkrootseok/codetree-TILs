import java.util.*;
import java.io.*;

public class Main {

	public static final int[] dr = {-1, 0, 1, 0};
	public static final int[] dc = {0, 1, 0, -1};

	public static class Player {

		int number, row, col, direction, stat, gun, score;

		Player(int n, int r, int c, int d, int s) {
			this.number = n;
			this.row = r;
			this.col = c;
			this.direction = d;
			this.stat = s;
			this.gun = 0;
			this.score = 0;
		}

		public void move() {
			// 이전 위치 제거
			candidates[row][col].remove(this);

			// 새 위치 계산
			int nRow = row + dr[direction];
			int nCol = col + dc[direction];

			// 격자 범위를 벗어나면 방향 반대로
			if (outRange(nRow, nCol)) {
				direction = (direction + 2) % 4;
				nRow = row + dr[direction];
				nCol = col + dc[direction];
			}

			// 이동
			row = nRow;
			col = nCol;

			// 새 위치 추가
			candidates[row][col].add(this);
		}

		public void win(int powerDifference) {
			this.score += powerDifference;
			if (canSwapGun(this)) swapGun(this);
		}

		public void lose() {
			// 총 내려놓기
			if (gun > 0) grid[row][col].offer(gun);
			gun = 0;

			// 이동
			for (int i = 0; i < 4; i++) {
				int newDirection = (direction + i) % 4;
				int nRow = row + dr[newDirection];
				int nCol = col + dc[newDirection];
				if (!outRange(nRow, nCol) && candidates[nRow][nCol].isEmpty()) {
					row = nRow;
					col = nCol;
					direction = newDirection;
					candidates[row][col].add(this);
					if (canSwapGun(this)) swapGun(this);
					return;
				}
			}
		}

		public int getPower() {
			return stat + gun;
		}

		public boolean outRange(int r, int c) {
			return r < 0 || c < 0 || r >= size || c >= size;
		}
	}

	static int size, playerCount, roundCount;
	static List<PriorityQueue<Integer>>[] grid;
	static List<Player>[][] candidates;
	static Player[] players;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();

		// Input
		String[] inputs = br.readLine().split(" ");
		size = Integer.parseInt(inputs[0]);
		playerCount = Integer.parseInt(inputs[1]);
		roundCount = Integer.parseInt(inputs[2]);

		grid = new ArrayList[size];
		candidates = new ArrayList[size][size];

		for (int i = 0; i < size; i++) {
			grid[i] = new ArrayList<>();
			String[] row = br.readLine().split(" ");
			for (int j = 0; j < size; j++) {
				grid[i].add(new PriorityQueue<>(Collections.reverseOrder()));
				int gunPower = Integer.parseInt(row[j]);
				if (gunPower > 0) grid[i].get(j).offer(gunPower);
				candidates[i][j] = new ArrayList<>();
			}
		}

		players = new Player[playerCount];
		for (int i = 0; i < playerCount; i++) {
			String[] playerData = br.readLine().split(" ");
			int r = Integer.parseInt(playerData[0]) - 1;
			int c = Integer.parseInt(playerData[1]) - 1;
			int d = Integer.parseInt(playerData[2]);
			int s = Integer.parseInt(playerData[3]);
			players[i] = new Player(i + 1, r, c, d, s);
			candidates[r][c].add(players[i]);
		}

		// Simulation
		for (int r = 0; r < roundCount; r++) {
			for (Player player : players) {
				player.move();
				handleCandidate(player);
			}
		}

		// Output
		for (Player player : players) {
			sb.append(player.score).append(" ");
		}
		System.out.println(sb.toString());
	}

	public static void handleCandidate(Player player) {
		List<Player> cellPlayers = candidates[player.row][player.col];
		if (cellPlayers.size() > 1) {
			Player p1 = cellPlayers.get(0);
			Player p2 = cellPlayers.get(1);
			Player winner = judge(p1, p2);
			Player loser = (winner == p1) ? p2 : p1;
			handleBattle(winner, loser);
		} else if (canSwapGun(player)) {
			swapGun(player);
		}
	}

	public static void handleBattle(Player winner, Player loser) {
		int powerDifference = Math.abs(winner.getPower() - loser.getPower());
		loser.lose();
		winner.win(powerDifference);
	}

	public static Player judge(Player p1, Player p2) {
		if (p1.getPower() != p2.getPower()) return (p1.getPower() > p2.getPower()) ? p1 : p2;
		return (p1.stat > p2.stat) ? p1 : p2;
	}

	public static boolean canSwapGun(Player player) {
		PriorityQueue<Integer> weapons = grid[player.row].get(player.col);
		return !weapons.isEmpty() && player.gun < weapons.peek();
	}

	public static void swapGun(Player player) {
		PriorityQueue<Integer> weapons = grid[player.row].get(player.col);
		if (!weapons.isEmpty()) {
			int tmp = player.gun;
			player.gun = weapons.poll();
			weapons.offer(tmp);
		}
	}
}

import java.util.*;
import java.io.*;

/**
 * CT_코드트리빵
 * @author parkrootseok
 **/
public class Main {

	public static class Node implements Comparable<Node> {

		int row;
		int col;
		int direction;
		int dist;

		public Node(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public Node(int row, int col, int dist) {
			this.row = row;
			this.col = col;
			this.dist = dist;
		}

		public Node(int row, int col, int direction, int dist) {
			this.row = row;
			this.col = col;
			this.direction = direction;
			this.dist = dist;
		}

		public boolean isEqual(Node n) {
			return this.row == n.row && this.col == n.col;
		}

		@Override
		public int compareTo(Node n) {

			if (this.dist == n.dist) {

				if (this.row == n.row) {
					return Integer.compare(this.col, n.col);
				}

				return Integer.compare(this.row, n.row);

			}

			return Integer.compare(this.dist, n.dist);

		}

	}

	public static final int EMPTY = 0;
	public static final int BASECAMP = 1;
	public static final int WALL = 2;

	public static final int[] dr = {-1, 0, 0, 1};
	public static final int[] dc = {0, -1, 1, 0};

	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;

	static int size;
	static int[][] grid;
	static int personCount;

	static Node[] stores;
	static Node[] people;

	public static void main(String[] args) throws IOException {

		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();

		input();

		int time = 0;
		while (!isFinished()) {

			moveStore();

			if (time < personCount) {
				moveBasecamp(time);
			}

			time++;
		}

		sb.append(time);
		bw.write(sb.toString());
		bw.close();

	}

	public static void moveStore() {

		for (int pCount = 0; pCount < personCount; pCount++) {

			Node person = people[pCount];
			Node store = stores[pCount];

			if (outRange(person.row, person.col) || person.isEqual(store)) {
				continue;
			}

			int dir = getDirection(person, store);
			person.row += dr[dir];
			person.col += dc[dir];

		}

		for (int pCount = 0; pCount < personCount; pCount++) {

			if (people[pCount].isEqual(stores[pCount])) {
				grid[people[pCount].row][people[pCount].col] = WALL;
			}
		}

	}

	public static int getDirection(Node person, Node store) {

		boolean[][] isVisited = new boolean[size][size];
		PriorityQueue<Node> nodePQ = new PriorityQueue<>();
		nodePQ.offer(new Node(person.row, person.col, -1, 0));
		isVisited[person.row][person.col] = true;

		while (!nodePQ.isEmpty()) {

			Node curN = nodePQ.poll();

			if (store.isEqual(curN)) {
				return curN.direction;
			}

			for (int dir = 0; dir < dr.length; dir++) {

				int nRow = curN.row + dr[dir];
				int nCol = curN.col + dc[dir];

				if (outRange(nRow, nCol) || isVisited[nRow][nCol] || grid[nRow][nCol] == WALL) {
					continue;
				}

				if (curN.direction == -1) {
					isVisited[nRow][nCol] = true;
					nodePQ.offer(new Node(nRow, nCol, dir, curN.dist + 1));
				} else {
					isVisited[nRow][nCol] = true;
					nodePQ.offer(new Node(nRow, nCol, curN.direction, curN.dist + 1));
				}

			}

		}

		return 0;

	}

	public static void moveBasecamp(int time) {

		boolean[][] isVisited = new boolean[size][size];
		PriorityQueue<Node> nodePQ = new PriorityQueue<>();
		nodePQ.offer(new Node(stores[time].row, stores[time].col, 0));
		isVisited[stores[time].row][stores[time].col] = true;

		while (!nodePQ.isEmpty()) {

			Node curN = nodePQ.poll();

			if (grid[curN.row][curN.col] == BASECAMP) {
				grid[curN.row][curN.col] = WALL;
				people[time].row = curN.row;
				people[time].col = curN.col;
				return;
			}

			for (int dir = 0; dir < dr.length; dir++) {

				int nRow = curN.row + dr[dir];
				int nCol = curN.col + dc[dir];

				if (outRange(nRow, nCol) || isVisited[nRow][nCol] || grid[nRow][nCol] == WALL) {
					continue;
				}

				isVisited[nRow][nCol] = true;
				nodePQ.offer(new Node(nRow, nCol, curN.dist + 1));

			}

		}

	}

	public static boolean outRange(int row, int col) {
		return row < 0 || size <= row || col < 0 || size <= col;
	}

	public static boolean isFinished() {

		for (int pCount = 0; pCount < personCount; pCount++) {
			if (!people[pCount].isEqual(stores[pCount])) {
				return false;
			}
		}

		return true;

	}

	public static void input() throws IOException {

		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		personCount = Integer.parseInt(inputs[1]);

		grid = new int[size][size];
		for (int row = 0; row < size; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < size; col++) {
				grid[row][col] = Integer.parseInt(inputs[col]);
			}
		}

		people = new Node[personCount];
		stores = new Node[personCount];
		for (int pCount = 0; pCount < personCount; pCount++) {

			inputs = br.readLine().trim().split(" ");

			int row = Integer.parseInt(inputs[0]) - 1;
			int col = Integer.parseInt(inputs[1]) - 1;

			stores[pCount] = new Node(row, col);
			people[pCount] = new Node(-1, -1);

		}

	}

}
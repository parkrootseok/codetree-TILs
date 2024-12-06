import java.util.*;
import java.io.*;

/**
 * CT_왕실의기사대결
 * @author parkrootseok
 **/
public class Main {
	
	public static class Position implements Comparable<Position> {
		
		int row;
		int col;
		
		public Position(int row, int col) {
			super();
			this.row = row;
			this.col = col;
		}

		@Override
		public int compareTo(Position o) {
			if (this.col != o.col) {
				return Integer.compare(this.col, o.col);
			}
			return Integer.compare(o.row, this.row);
		}
		
	}
	
	public static class Node implements Comparable<Node> {
		
		int row;
		int col;
		int max;
		int angle;
		
		public Node(int row, int col, int max, int angle) {
			this.row = row;
			this.col = col;
			this.max = max;
			this.angle = angle;
		}
		
		@Override
		public int compareTo(Main.Node o) {
			
			if (this.max != o.max) {
				return Integer.compare(o.max, this.max);
			}
			
			if (this.angle != o.angle) {
				return Integer.compare(this.angle, o.angle);
			}
			
			if (this.col != o.angle) {
				return Integer.compare(this.col, o.col);
			}
			
			
			return Integer.compare(this.row, o.col);
			
		}
		
		
	}
	
	public static final int SIZE = 5;
	public static final int[] dr = {0, 1, -1, 0};
	public static final int[] dc = {-1, 0, 0, 1};
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int K;
	static int M;
	static int[][] map;
	static Queue<Integer> piece;
	static int answer;
	
	static int[][] copy;
 	static boolean[][] isVisited;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (int k = 1; k <= K; k++) {
			
			List<Node> candidates = new ArrayList<>();
			
			for (int angle = 1; angle <= 3; angle++) {

				for (int row = 1; row <= 3; row++) {
					for (int col = 1; col <= 3; col++) {
						
						rotate(angle, row - 1, col - 1);
						
						int score = 0;
						isVisited = new boolean[SIZE][SIZE];
						for (int r = 0; r < SIZE; r++) {
							for (int c = 0; c < SIZE; c++) {
								if (!isVisited[r][c]) {
									score += bfs(r, c);
								}
							}	
						}
						
						if (0 < score) {
							candidates.add(new Node(row, col, score, angle));
						}
						
					}
				}
				
			}
			
			if (candidates.isEmpty()) {
				break;
			}
			
			Collections.sort(candidates);
			Node candidate = candidates.get(0);
			rotate(candidate.angle, candidate.row - 1, candidate.col - 1);
			map = copy;
			
			int score = 0;
			while (true) {
				
				int cScore = 0;
				isVisited = new boolean[SIZE][SIZE];
				for (int r = SIZE - 1; 0 <= r; r--) {
					for (int c = 0; c < SIZE; c++) {
						if (!isVisited[r][c]) {
							cScore += getScore(r, c);
						}
					}	
				}
				
				if (cScore == 0) {
					break;
				}
				
				score += cScore;
				
			}
			
			output(score);
			
		}
		
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static int getScore(int row, int col) {
		
		PriorityQueue<Position> positions = new PriorityQueue<Position>();
		Queue<int[]> posQ = new ArrayDeque<int[]>();
		posQ.offer(new int[] {row, col});
		isVisited[row][col] = true;
		positions.add(new Position(row, col));
		
		while (!posQ.isEmpty()) {
		
			int[] pos = posQ.poll();
			int cRow = pos[0];
			int cCol = pos[1];
			
			for (int dir = 0; dir < dr.length; dir++) {
			
				int nRow = cRow + dr[dir];
				int nCol = cCol + dc[dir];
				
				if (isPossible(nRow, nCol) && map[cRow][cCol] == map[nRow][nCol]) {
					isVisited[nRow][nCol] = true;
					positions.add(new Position(nRow, nCol));
					posQ.offer(new int[] {nRow, nCol});
				}
				
			}
			
		}
		
		int count = positions.size();
		if (3 <= count) {
			while (!positions.isEmpty()) {
				Position pos = positions.poll();
				if (piece.isEmpty()) {
					map[pos.row][pos.col] = 0;
				} else {
					map[pos.row][pos.col] = piece.poll();
				}
			}
			return count;
		}
		
		return 0;
		
	}
	
	public static int bfs(int row, int col) {
		
		Queue<int[]> posQ = new ArrayDeque<int[]>();
		posQ.offer(new int[] {row, col});
		isVisited[row][col] = true;
		int count = 1;
		
		while (!posQ.isEmpty()) {
		
			int[] pos = posQ.poll();
			int cRow = pos[0];
			int cCol = pos[1];
			
			for (int dir = 0; dir < dr.length; dir++) {
			
				int nRow = cRow + dr[dir];
				int nCol = cCol + dc[dir];
				
				if (isPossible(nRow, nCol) && copy[cRow][cCol] == copy[nRow][nCol]) {
					isVisited[nRow][nCol] = true;
					count++;
					posQ.offer(new int[] {nRow, nCol});
				}
				
			}
			
		}
		
		if (3 <= count) {
			return count;
		}
		
		return 0;
		
	}
	
	public static void rotate(int angle, int row, int col) {
		
		int size = 3;
		copy = copy();
		for (int sRow = row; sRow < row + 3; sRow++) {
			for (int sCol = col; sCol < col + 3; sCol++) {
				
				int oRow = sRow - row;
				int oCol = sCol - col;
				
				int cRow = oCol;
				int cCol = size - oRow - 1;
				
				switch (angle) {
					case 2:
						cRow = size - oRow - 1;
						cCol = size - oCol - 1;
						break;
					case 3:
						cRow = size - oCol - 1;
						cCol = oRow;
						break;
				}
				
				copy[cRow + row][cCol + col] = map[sRow][sCol];
				
			}
			
		}
		
	}
	
	public static boolean isPossible(int row, int col) {
		
		if (row < 0 || SIZE <= row || col < 0 || SIZE <= col) {
			return false;
		}
		
		if (isVisited[row][col]) {
			return false;
		}
		
		return true;
	}
	
	public static int[][] copy() {
		
		int[][] copy = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				copy[row][col] = map[row][col];
			}	
		}
		
		return copy;
		
	}
	
	public static void input() throws IOException {
		
		String[] inputs = br.readLine().trim().split(" ");
		K = Integer.parseInt(inputs[0]);
		M = Integer.parseInt(inputs[1]);
		
		map = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < SIZE; col++) {
				map[row][col] = Integer.parseInt(inputs[col]);
			}
		}
		
		inputs = br.readLine().trim().split(" ");
		piece = new ArrayDeque<>();
		for (int m = 0; m < M; m++) {
			piece.offer(Integer.parseInt(inputs[m]));
		}
		
	}
	
	public static void output(int score) {
		sb.append(score).append(" ");
	}
	
}
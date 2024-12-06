import java.util.*;
import java.io.*;

/**
 * CT_왕실의기사대결
 * @author parkrootseok
 **/
public class Main {
	
	public static class Node implements Comparable<Node> {
		
		int max;
		int angle;
		int row;
		int col;
		
		public Node(int max, int angle) {
			this.max = max;
			this.angle = angle;
		}
		
		public Node(int max, int angle, int row, int col) {
			this.max = max;
			this.angle = angle;
			this.row = row;
			this.col = col;
		}
		
		@Override
		public int compareTo(Node o) {
			
			if (this.max != o.max) {
				return Integer.compare(o.max, this.max);
			}
			
			if (this.angle != o.angle) {
				return Integer.compare(this.angle, o.angle);
			}
			
			if (this.col != o.col) {
				return Integer.compare(this.col, o.col);
			}
			
			return Integer.compare(this.row, o.row);
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
	static Queue<Integer> candidates;
	static int answer;
	
	static boolean[][] isVisited;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (int k = 1; k <= K; k++) {

			List<Node> nodes = new ArrayList<>();
			for (int row = 1; row <= 3; row++) {
			
				for (int col = 1; col <= 3; col++) {
					
					Node node = findAngle(row, col);
					node.row = row;
					node.col = col;
					nodes.add(node);
                    
				}
				
			}
			
			Collections.sort(nodes);
			Node node = nodes.get(0);
			rotation(node.angle, node.row, node.col);
			
			if (node.max == 0) {
				break;
			}
			
			int score = 0;
			while (true) {
				
				int cScore = 0;
				isVisited = new boolean[SIZE][SIZE];
				for (int row = SIZE - 1; row >= 0; row--) {
					for (int col = 0; col < SIZE; col++) {
						if (!isVisited[row][col]) {
							cScore += getScore(row, col);
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
		
		Queue<int[]> scoreQ = new ArrayDeque<>();
		Queue<int[]> q = new ArrayDeque<>();
		q.offer(new int[]{row, col});
		isVisited[row][col] = true;
		
		while (!q.isEmpty()) {
			
			int[] pos = q.poll();
			int cRow = pos[0];
			int cCol = pos[1];
			
			scoreQ.offer(new int[] {cRow, cCol});
			
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = cRow + dr[dir];
				int nCol = cCol + dc[dir];
				
				if (isOutRange(nRow, nCol) || isVisited[nRow][nCol]) {
					continue;
				}
				
				if (map[cRow][cCol] == map[nRow][nCol]) {
					isVisited[nRow][nCol] = true;
					q.offer(new int[] {nRow, nCol});
				}
				
			}
			
		}
		
		if (3 <= scoreQ.size()) {
			
			for (int[] pos : scoreQ) {
				if (candidates.isEmpty()) {
					break;
				}
				map[pos[0]][pos[1]] = candidates.poll();
			}
			
			return scoreQ.size();
		
		}
		
		return 0;
		
	}

	public static void rotation(int angle, int cRow, int cCol) {
		
		int size = 3;
		int sRow = cRow - 1;
		int sCol = cCol - 1;
		
		int[][] copyMap = copy();
		for (int row = sRow; row < sRow + 3; row++) {
			for (int col = sCol; col < sCol + 3; col++) {
				
				int ox = row - sRow;
				int oy = col - sCol;
				
				int rx = oy;
				int ry = size - ox - 1;
				
				switch (angle) {
					case 0:
						rx = oy;
						ry = size - ox - 1;
						break;
					case 1:
						rx = size - ox - 1;
						ry = size - oy - 1;
						break;
					case 2:
						rx = size - oy - 1;
						ry = ox;
						break;
				}
				
				copyMap[rx + sRow][ry + sCol] = map[row][col];
				
			}
		}
		
		map = copyMap;
		
	}
	
	public static Node findAngle(int cRow, int cCol) {
		
		int max = 0;
		int mAngle = 0;
		int size = 3;
		int sRow = cRow - 1;
		int sCol = cCol - 1;

		for (int angle = 0; angle < 3; angle++) {
			
			int[][] copyMap = copy();
			for (int row = sRow; row < sRow + 3; row++) {
				for (int col = sCol; col < sCol + 3; col++) {
					
					int ox = row - sRow;
					int oy = col - sCol;
					
					int rx = oy;
					int ry = size - ox - 1;
					
					switch (angle) {
						case 0:
							rx = oy;
							ry = size - ox - 1;
							break;
						case 1:
							rx = size - ox - 1;
							ry = size - oy - 1;
							break;
						case 2:
							rx = size - oy - 1;
							ry = ox;
							break;
					}
					
					copyMap[rx + sRow][ry + sCol] = map[row][col];
					
				}
				
			}
			
			int score = 0;
			isVisited = new boolean[SIZE][SIZE];
			for (int row = SIZE - 1; row >= 0; row--) {
				for (int col = 0; col < SIZE; col++) {
					if (!isVisited[row][col]) {
						score += check(copyMap, row, col);
					}
				}
			}
	
			if (max < score) {
				max = score;
				mAngle = angle;
			}
			
		}
		
		return new Node(max, mAngle);
		
	}
	
	public static int check(int[][] cMap, int row, int col) {
		
		Queue<int[]> q = new ArrayDeque<>();
		q.offer(new int[]{row, col});
		isVisited[row][col] = true;
		
		int count = 0;
		while (!q.isEmpty()) {
			
			int[] pos = q.poll();
			int cRow = pos[0];
			int cCol = pos[1];
			
			count++;
			
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = cRow + dr[dir];
				int nCol = cCol + dc[dir];
				
				if (isOutRange(nRow, nCol) || isVisited[nRow][nCol]) {
					continue;
				}
				
				if (cMap[cRow][cCol] == cMap[nRow][nCol]) {
					isVisited[nRow][nCol] = true;
					q.offer(new int[] {nRow, nCol});
				}
				
			}
			
		}
		
		if (3 <= count) {
			return count;
		}
		
		return 0;
		
	}
	
	public static boolean isOutRange(int row, int col) {
		return row < 0 || SIZE <= row || col < 0 || SIZE <= col;
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
		candidates = new ArrayDeque<>();
		for (int m = 0; m < M; m++) {
			candidates.offer(Integer.parseInt(inputs[m]));
		}
		
	}
	
	public static void output(int score) {
		sb.append(score).append(" ");
	}
	
}
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
			this.row = row;
			this.col = col;
		}

		@Override
		public int compareTo(Position o) {
			if (this.col == o.col) {
				return Integer.compare(o.row, this.row);
			}
			return Integer.compare(this.col, o.col);
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
		public int compareTo(Node o) {
			
			if (this.max == o.max) {
				
				if (this.angle == o.angle) {
					
					if (this.row == o.row) {
						return Integer.compare(this.row, o.row);
					}
					
					return Integer.compare(this.col, o.col);
				
				}
				
				return Integer.compare(this.angle, o.angle);
				
			}
			
			return Integer.compare(o.max, this.max);
			
		}
		
		
	}
	
	public static final int SIZE = 5;
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1}; 
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int K;
	static int M;
	static int[][] map;
	static int[][] copy;
	
	static Queue<Integer> piece;
	static PriorityQueue<Position> usePieces;
 	static boolean[][] isVisited;
	static int[] answer;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		answer = new int[K];
		for (int k = 0; k < K; k++) {
			
			List<Node> candidates = new ArrayList<>();
			
			for (int angle = 1; angle <= 3; angle++) {

				for (int row = 1; row <= 3; row++) {
					for (int col = 1; col <= 3; col++) {
						
						rotate(angle, row - 1, col - 1);
						int score = bfs(copy);
						
						if (0 < score) {
							candidates.add(new Node(row , col, score, angle));
						}
						
					}
				}
				
			}
			
			if (candidates.isEmpty()) {
				break;
			}
			
			Collections.sort(candidates);
			Node best = candidates.get(0);
			rotate(best.angle, best.row - 1, best.col - 1);
			map = copy;
			
			int score = bfs(map);
			int sum = 0;
			
			while (score > 0) {
				chagePiece();
				sum += score;
				score = bfs(map);
			}
			
			answer[k] = sum;
			
		}
		
		output();
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static void chagePiece() {
		while (!usePieces.isEmpty()) {
			Position pos = usePieces.poll();
			map[pos.row][pos.col] = piece.poll();
		}
	
	}
	
	public static int bfs(int[][] arr) {
		
		Queue<Position> posQ = new ArrayDeque<>();
		usePieces = new PriorityQueue<>();
		isVisited = new boolean[SIZE][SIZE];
		
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				if (!isVisited[row][col]) {
					
					posQ.offer(new Position(row, col));
					isVisited[row][col] = true;

					
					int count = 1;
					List<Position> positions = new ArrayList<Position>();
					positions.add(new Position(row, col));
					
					while (!posQ.isEmpty()) {
						
						Position pos = posQ.poll();
						int cRow = pos.row;
						int cCol = pos.col;
						
						for (int dir = 0; dir < dr.length; dir++) {
						
							int nRow = cRow + dr[dir];
							int nCol = cCol + dc[dir];
							
							if (outRange(nRow, nCol) ) {
								continue;
							}
							
							if (isVisited[nRow][nCol]) {
								continue;
							}
							
							if (arr[nRow][nCol] == arr[row][col]) {
								posQ.offer(new Position(nRow, nCol));
								isVisited[nRow][nCol] = true;
								
								count++;
								positions.add(new Position(nRow, nCol));
								
							}
							
						}
						
					}
					
					if (count >= 3) {
						usePieces.addAll(positions);
					}
					
				}
				
			}	
			
		}
		
		return usePieces.size();
		
	}
	
	public static void rotate(int angle, int sRow, int sCol) {
		
		int size = 3;
		copy();
		for (int row = sRow; row < sRow + 3; row++) {
			for (int col = sCol; col < sCol + 3; col++) {
				
				int oR = row - sRow;
				int oC = col - sCol;
				int cR = oC;
				int cC = size - oR - 1;
				
				switch (angle) {
				
					case 2:
						cR = size - oR - 1;
						cC = size - oC - 1;
						break;
					case 3:
						cR = size - oC - 1;
						cC = oR;
						break;
						
				}

				copy[cR + sRow][cC + sCol] = map[row][col];
				
			}
			
		}
		
	}
	
	public static boolean outRange(int row, int col) {
		return row < 0 || SIZE <= row || col < 0 || SIZE <= col;
	}
	
	public static void copy() {
		
		copy = new int[SIZE][SIZE];
		
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				copy[row][col] = map[row][col];
			}	
		}
				
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
	
	public static void output() {
	
		for (int a : answer) {
			
			if (a == 0) {
				return;
			}
			
			sb.append(a).append(" ");
			
		}
		
	}
	
}
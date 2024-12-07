import java.util.*;

import javax.swing.text.html.ParagraphView;

import java.io.*;

/**
 * CT_메이즈러너
 * @author parkrootseok
 **/
public class Main {
	
	public static class Pair implements Comparable<Pair> {
		
		int r;
		int c;
		
		public Pair(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		@Override
		public int compareTo(Pair o) {
			if (this.c == o.c) {
				return Integer.compare(o.r, this.r);
			}
			return Integer.compare(this.c, o.c);
		}
		
		
	}
	
	public static class Node implements Comparable<Node> {
		
		int r;
		int c;
		int s;
		int a;
		
		public Node(int r, int c, int s, int a) {
			this.r = r;
			this.c = c;
			this.s = s;
			this.a = a;
		}
		
		@Override
		public int compareTo(Node o) {
			
			if (this.s == o.s) {
				
				if (this.a == o.a) {
					
					if (this.c == o.c) {
						
						return Integer.compare(this.r, o.r);
						
					}
					
					return Integer.compare(this.c, o.c);
					
				}
				
				return Integer.compare(this.a, o.a);
				
			}
			
			return Integer.compare(o.s, this.s);
			
		}
		
	}
	
	public static final int[] dr = {-1, 1, 0, 0};
	public static final int[] dc = {0, 0, -1, 1};
	
	public static final int SIZE = 5;
	public static final int R_SIZE = 3;
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int gameRound;
	static int pieceCount;
	
	static int[][] map;
	static int[][] copyMap;
	
	static Queue<Integer> pieces;
	static PriorityQueue<Pair> usePieces;
	
	static int[] scores;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		
		for (int gRound = 0; gRound < gameRound; gRound++) {
			
			
			PriorityQueue<Node> candidates = new PriorityQueue<Node>();
			for (int row = 1; row <= 3; row++) {
			
				for (int col = 1; col <= 3; col++) {
					
					for (int angle = 1; angle <= 3; angle++) {
						
						rotation(angle, row - 1, col - 1);
						
						int score = getScore(copyMap);
						if (0 < score) {
							candidates.offer(new Node(row, col, score, angle));
						}
						
					}
					
				}
				
			}
			
			if (candidates.isEmpty()) {
				break;
			}
			
			Node best = candidates.poll();
			rotation(best.a, best.r - 1, best.c - 1);
			map = copyMap;
			
			int score = getScore(map);
			int sum = 0;
			
			while (score > 0) {
				changePiece();
				sum += score;
				score = getScore(map);
			}
			
			scores[gRound] = sum;
			
		}
		
		output();
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static void changePiece() {
		
		while(!usePieces.isEmpty()) {
			Pair p = usePieces.poll();
			map[p.r][p.c] = pieces.poll();
		}
		
	}
	
	public static int getScore(int[][] map) {
		
		Queue<Pair> queue = new ArrayDeque<>();
		boolean[][] isVisited = new boolean[SIZE][SIZE];
		usePieces = new PriorityQueue<>();
		
		for (int row = 0; row < SIZE; row++) {
		
			for (int col = 0; col < SIZE; col++) {
				
				if (!isVisited[row][col]) {
					
					
					List<Pair> pairs = new ArrayList<Pair>();
					int count = 1;
					
					queue.offer(new Pair(row, col));
					pairs.add(new Pair(row, col));
					isVisited[row][col] = true;
					
					while (!queue.isEmpty()) {
						
						Pair p = queue.poll();
						int cR = p.r;
						int cC = p.c;
					
						for (int dir = 0; dir < dr.length; dir++) {
							
							int nR = cR + dr[dir];
							int nC = cC + dc[dir];
							
							if (outRange(nR, nC)) {
								continue;
							}
							
							if (isVisited[nR][nC]) {
								continue;
							}
							
							if (map[row][col] != map[nR][nC]) {
								continue;
							}
							
							queue.offer(new Pair(nR, nC));
							pairs.add(new Pair(nR, nC));
							count++;
							isVisited[nR][nC] = true;
							
								
						}
						
					}
					
					if (3 <= count) {
						usePieces.addAll(pairs);
					}
					
				}	
				
			}
			
		}
		
		return usePieces.size();
		
	}
	
	public static boolean outRange(int row, int col) {
		return row < 0 || SIZE <= row || col < 0 || SIZE <= col;
	}
	
	public static void rotation(int angle, int sRow, int sCol) {
		
		copy();
		
		for (int row = sRow; row < sRow + R_SIZE; row++) {
			
			for (int col = sCol; col < sCol + R_SIZE; col++) {
				
				int oR = row - sRow;
				int oC = col - sCol;
				int rR = oC;
				int rC = R_SIZE - oR - 1;
				
				switch (angle) {
					case 2:
						rR = R_SIZE - oR - 1;
						rC = R_SIZE - oC - 1;
						break;
					case 3:
						rR = R_SIZE - oC - 1;
						rC = oR;
						break;
				}
				
				copyMap[rR + sRow][rC + sCol] = map[row][col];
				
			}
			
		}
		
	}
	
	public static void copy() {
		
		copyMap = new int[SIZE][SIZE];
		
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				copyMap[row][col] = map[row][col];
			}
		}
		
	}
	
	public static void isPossible(int row, int col) {
		
	}
	
	
	public static void input() throws IOException{
		
		String[] inputs = br.readLine().trim().split(" ");
		gameRound = Integer.parseInt(inputs[0]);
		pieceCount = Integer.parseInt(inputs[1]);
		
		scores = new int[gameRound];
		map = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < SIZE; col++) {
				map[row][col] = Integer.parseInt(inputs[col]);
			}
		}
		
		inputs = br.readLine().trim().split(" ");
		pieces = new ArrayDeque<>();
		for (int pCount = 0; pCount < pieceCount; pCount++) {
			pieces.add(Integer.parseInt(inputs[pCount]));
		}
		
	}
	
	public static void output() {
		
		for (int score : scores) {
			if (score == 0) {
				return;
			}
			sb.append(score).append(" ");
		}
	
	}
	
}
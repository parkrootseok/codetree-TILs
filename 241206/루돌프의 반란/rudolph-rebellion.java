import java.util.*;
import java.io.*;

/**
 * CT_왕실의기사대결
 * @author parkrootseok
 **/
public class Main {
	
	static class Node implements Comparable<Node>{
		
		int index;
		int row;
		int col;
		int dis;
		
		public Node(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public Node(int index, int row, int col) {
			this.index = index;
			this.row = row;
			this.col = col;
		}
		
		public Node(int index, int row, int col, int dis) {
			this.index = index;
			this.row = row;
			this.col = col;
			this.dis = dis;
		}

		@Override
		public int compareTo(Node o) {
			if (this.dis != o.dis) {
				return Integer.compare(this.dis, o.dis);
			}
			
			if (this.row != o.row) {
				return Integer.compare(o.row, this.row);
			}
			
			return Integer.compare(o.col, this.col);
		}
		
	}
	
	public static final int[] dr = {-1, 0, 1, 0, 1, 1, -1, -1};
	public static final int[] dc = {0, 1, 0, -1, -1, 1, 1, -1};
	public static final int EMPTY = 0;
	public static final int RUDOLPH = -1;
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int size;
	static int gameRount;
	static int santaCount;
	static int santaPower;
	static int rudolphPower;
	
	static Node rudolph;
	static Node[] santas;
	
	static int[][] map;
	static int[] score;
	static int[] time;
	static boolean[] isAlive;
	static boolean[] isStun;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (int m = 1; m <= gameRount; m++) {
			
			moveRudolph(m, findCandidate());
			
			for (int p = 1; p <= santaCount; p++) {
				
				if (isStun[p] && time[p] == m) {
					isStun[p] = false;
				}
				
				if (isAlive[p] && !isStun[p]) {
					moveSanta(m, p);
				}
			}
			
			if (!isContinue()) {
				break;
			}
			
		}
		
		output();
		
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static int findCandidate() {
		
		List<Node> candidates = new ArrayList<>();
		for (int p = 1; p <= santaCount; p++) {
			
			if (isAlive[p]) {
				Node s = santas[p];
				int dis = getDistance(rudolph, s);
				candidates.add(new Node(p, s.row, s.col, dis));
			}
			
		}
		
		Collections.sort(candidates);
		return candidates.get(0).index;
		
	}
	
	public static void moveRudolph(int round, int index) {
		
		Node s = santas[index];
		int min = Integer.MAX_VALUE;
		int mDir = -1;
		for (int dir = 0; dir < dr.length; dir++) {
			
			Node nPos = new Node(rudolph.row + dr[dir], rudolph.col + dc[dir]);
			
			if (isInRange(nPos.row, nPos.col)) {
				int dis = getDistance(nPos, s);
				if (min > dis) {
					min = dis;
					mDir = dir;
				}
			}
			
		}
		
		rudolph.row += dr[mDir];
		rudolph.col += dc[mDir];
		
		if (isConflict(s.row, s.col)) {
			
			map[s.row][s.col] = EMPTY;
			s.row += (dr[mDir] * rudolphPower);
			s.col += (dc[mDir] * rudolphPower);
			
			interaction(mDir, index);
			
			score[index] += rudolphPower;
			time[index] = round + 2; 
			isStun[index] = true;
			
		}
		
	}
	
	public static void moveSanta(int round, int index) {
		
		Node s = santas[index];
		int min = getDistance(rudolph, s);
		int mDir = -1;
		
		for (int dir = 0; dir < 4; dir++) {
			
			Node nPos = new Node(s.row + dr[dir], s.col + dc[dir]);
			
			if (isInRange(nPos.row, nPos.col) && map[nPos.row][nPos.col] == EMPTY) {
				int dis = getDistance(nPos, rudolph);
				if (min > dis) {
					min = dis;
					mDir = dir;
				}
			}
			
		}
		
		if (mDir == -1) {
			return;
		}
		
		map[s.row][s.col] = EMPTY;
		s.row += dr[mDir];
		s.col += dc[mDir];
		
		if (isConflict(s.row, s.col)) {
			
			s.row -= (dr[mDir] * santaPower);
			s.col -= (dc[mDir] * santaPower);
		
			interaction((mDir + 2) % 4, index);
			
			score[index] += santaPower;
			time[index] = round + 2; 
			isStun[index] = true;
			
		} else {
			map[s.row][s.col] = s.index;
		}
		
	}
	
	public static void interaction(int dir, int index) {
		
		Queue<Integer> queue = new ArrayDeque<>();
		queue.add(index);
		
		while (!queue.isEmpty()) {
			
			Node cSanta = santas[queue.poll()];
			int cRow = cSanta.row;
			int cCol = cSanta.col;

			if (!isInRange(cRow, cCol)) {
				isAlive[cSanta.index] = false;
				break;
			}
			
			if (isEmpty(cRow, cCol)) {
				map[cRow][cCol] = cSanta.index;
				break;
			}
			
			Node nSanta = santas[map[cRow][cCol]];
			nSanta.row += dr[dir];
			nSanta.col += dc[dir];
			map[cRow][cCol] = cSanta.index;
			queue.offer(nSanta.index);
			
		}
		
	}
	
	public static void debug() {
		System.out.println("{row:"+rudolph.row+",col:"+rudolph.col+"}");
		for (int p = 1; p <= santaCount; p++) {
			Node santa = santas[p];
			System.out.println("{idx:"+p+",row:"+santa.row+",col:"+santa.col+",scr:"+score[p]+"}");
		}
	}
	
	public static int getDistance(Node a, Node b) {
		return (int) (Math.pow(a.row - b.row, 2) + Math.pow(a.col - b.col, 2));
	}
	
	public static boolean isInRange(int row, int col) {
		return 1 <= row && row <= size && 1 <= col && col <= size;
	}
	
	public static boolean isEmpty(int row, int col) {
		return 0 == map[row][col];
	}
	
	public static boolean isConflict(int row, int col) {
		return row == rudolph.row && col == rudolph.col;
	}
	
	public static boolean isContinue() {
		
		boolean isFlag = false;
		
		for (int p = 1; p <= santaCount; p++) {
			if (isAlive[p]) {
				isFlag = true;
				score[p]++;
			}
		}
		
		return isFlag;
		
	}
	
	public static void input() throws IOException {
		
		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		gameRount = Integer.parseInt(inputs[1]);
		santaCount = Integer.parseInt(inputs[2]);
		rudolphPower = Integer.parseInt(inputs[3]);
		santaPower = Integer.parseInt(inputs[4]);
		
		map = new int[size + 1][size + 1];
		
		inputs = br.readLine().trim().split(" ");
		rudolph = new Node(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
		
		
		santas = new Node[santaCount + 1];
		score = new int[santaCount + 1];
		time = new int[santaCount + 1];
		isAlive = new boolean[santaCount + 1];
		Arrays.fill(isAlive, true);
		isStun = new boolean[santaCount + 1];
		for (int sCount = 0; sCount < santaCount; sCount++) {
			inputs = br.readLine().trim().split(" ");
			int index = Integer.parseInt(inputs[0]);
			int row = Integer.parseInt(inputs[1]);
			int col = Integer.parseInt(inputs[2]);
			santas[index] = new Node(index, row, col);
			map[row][col] = index;
		}
		
	}
	
	public static void output() {
		for (int p = 1; p <= santaCount; p++) {
			sb.append(score[p]).append(" ");
		}
	}
	
}
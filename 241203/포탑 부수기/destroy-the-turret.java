import java.util.*;
import java.io.*;

/**
 * CT_포탑부수기
 * @author parkrootseok
 **/
public class Main {
	
	public static class Top implements Comparable<Top> {
		
		int row;
		int col;
		int power;
		int time;
		
		Top(int row, int col, int power, int time) {
			this.row = row;
			this.col = col;
			this.power = power;
			this.time = time;
		}
		
		@Override
		public int compareTo(Top o) {
			
			if (this.power != o.power) {
				return this.power - o.power;
			}
			
			if (this.time != o.time) {
				return o.time - this.time;
			}
			
				
			if ((this.row + this.col) != (o.row + o.col)) {
				return (o.row + o.col) - (this.row + this.col);
			}
					
			return o.col - this.col;
			
		}
		
	}
	
	public static class Node {
		
		int row;
		int col;

		Node(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
	}
	
	public static final int[] dr = {0, 1, 0, -1};
	public static final int[] dc = {1, 0, -1, 0};
    
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int rowSize;
	static int colSize;
	static int roundCount;
	static Top[][] map;
	static List<Top> tops;
	
	static boolean[][] isVisited;
	static boolean[][] isDamaged;
	
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (int rCount = 1; rCount <= roundCount; rCount++) {
			
			init();

			if (isFinished()) {
				break;
			}
			
			Collections.sort(tops);
			
			// 1. 공격자 선정
			Top attacker = tops.get(0);
			attacker.power += (rowSize + colSize);
			attacker.time = rCount;
			isDamaged[attacker.row][attacker.col] = true;
			
			Top defender = tops.get(tops.size() - 1);	
			isDamaged[defender.row][defender.col] = true;
			
			// 2. 공격
			if (!lazer(attacker, defender)) {
				bomb(attacker, defender);
			}
			
			// 3. 부서진 포탑 체크
			remove();
			
			// 4. 포탑 정비 (이미 부서진 포탑은 정비 불가)
			repair();
			
		}
		
		int max = Integer.MIN_VALUE;
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				max = Math.max(max, map[row][col].power);
			}
		}
		
		sb.append(max);
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static void init() {
		
		isVisited = new boolean[rowSize][colSize];
		isDamaged = new boolean[rowSize][colSize];
		tops.clear();
		
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				if (map[row][col].power > 0) {
					tops.add(map[row][col]);
				}
			}
		}
		
	}
	
	public static boolean lazer(Top attacker, Top defender) {
		
		boolean isPossible = false;
		Node[][] route = new Node[rowSize][colSize];
		
		Queue<Node> nodes = new ArrayDeque<>();
		nodes.offer(new Node(attacker.row, attacker.col));
		isVisited[attacker.row][attacker.col] = true;
		
		while(!nodes.isEmpty()) {
			
			Node cur = nodes.poll();
			
			if (cur.row == defender.row && cur.col == defender.col) {
				isPossible = true;
				break;
			}
			
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = (cur.row + dr[dir] + rowSize) % rowSize;
				int nCol = (cur.col + dc[dir] + colSize) % colSize;
				
				if (isVisited[nRow][nCol] || map[nRow][nCol].power == 0) {
					continue;
				}
				
				route[nRow][nCol] = new Node(cur.row, cur.col);
				nodes.offer(new Node(nRow, nCol));
				isVisited[nRow][nCol] = true;
				
			}
			
		}
		
		if (isPossible) {
			
			map[defender.row][defender.col].power -= attacker.power;
			Node cur = route[defender.row][defender.col];
			
			while (!(attacker.row == cur.row && attacker.col == cur.col)) {
				isDamaged[cur.row][cur.col] = true;
				map[cur.row][cur.col].power -= (attacker.power / 2);
				cur = route[cur.row][cur.col];
			}
			
		}
		
		return isPossible;
		
	}
	
	public static void bomb(Top attacker, Top defender) {
		
		int[] ddr = {0, 0, -1, -1, -1, 1, 1, 1};
		int[] ddc = {-1, 1, -1, 0, 1, -1, 0, 1};
		
		defender.power -= attacker.power;
		
		int power = attacker.power / 2;
		for (int dir = 0; dir < ddr.length; dir++) {
			
			int nRow = (defender.row + ddr[dir] + rowSize) % rowSize;
			int nCol = (defender.col + ddc[dir] + colSize) % colSize;
			
			if (map[nRow][nCol].power == 0) {
				continue;
			}
			
			if (nRow == attacker.row && nCol == attacker.col) {
				continue;
			}
		
			isDamaged[nRow][nCol] = true;
			map[nRow][nCol].power -= (attacker.power / 2);
			
		}
		
	}
	
	public static void remove() {
	
		tops.clear();
		
		for (int row = 0; row < rowSize; row++) {
			
			for (int col = 0; col < colSize; col++) {
				
				if (map[row][col].power < 0) {
					map[row][col].power = 0;
				}
				
			}
			
		}
	
		
	}
	
	public static void repair() {
		
		for (int row = 0; row < rowSize; row++) {
			
			for (int col = 0; col < colSize; col++) {
				
				if (map[row][col].power == 0 || isDamaged[row][col]) {
					continue;
				}
				
				map[row][col].power++;
				
			}
			
		}

	}
		
	public static boolean isFinished() {
		return tops.size() == 1;
	}
    
	
	public static void input() throws IOException {
		
		String[] inputs = br.readLine().trim().split(" ");
		rowSize = Integer.parseInt(inputs[0]);
		colSize = Integer.parseInt(inputs[1]);
		roundCount = Integer.parseInt(inputs[2]);
		
		map = new Top[rowSize][colSize];
		tops = new ArrayList<>();
		for (int row = 0; row < rowSize; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < colSize; col++) {
				int power = Integer.parseInt(inputs[col]);
				map[row][col] = new Top(row, col, power, 0);
			}
		}
		
	}
	
}
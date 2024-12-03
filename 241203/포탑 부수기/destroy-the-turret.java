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
		int attackedAt;
		
		Top(int row, int col, int power) {
			this.row = row;
			this.col = col;
			this.power = power;
		}
		
		@Override
		public int compareTo(Top t) {
			
			if (this.power != t.power) {
				return Integer.compare(this.power, t.power);
			}
			
			if (this.attackedAt != t.attackedAt) {
				return Integer.compare(t.attackedAt, this.attackedAt);
			}
			
				
			if (this.row + this.col != t.row + t.col) {
				return Integer.compare(t.row + t.col, this.row + this.col);
			}
					
			return Integer.compare(t.col, this.col);
			
		}
		
	}
	
	public static class Node {
		
		int row;
		int col;
		Node prev;
		
		Node(int row, int col) {
			this.row = row;
			this.col = col;
			this.prev = null;
		}
		
		Node(int row, int col, Node prev) {
			this.row = row;
			this.col = col;
			this.prev = prev;
		}
		
	}
	
	public static final int[] dr = {0, 1, 0, -1, 1, -1, 1, -1};
	public static final int[] dc = {1, 0, -1, 0, 1, -1, -1, 1};
    
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int rowSize;
	static int colSize;
	static int handicap;
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
			
			if (isFinished()) {
				break;
			}
			
			init();
			Collections.sort(tops);

			// 1. 공격자 선정
			Top attacker = tops.get(0);
			attacker.power += handicap;
			attacker.attackedAt = rCount;
			isDamaged[attacker.row][attacker.col] = true;
			
			Top defender = tops.get(tops.size() - 1);	
			isDamaged[defender.row][defender.col] = true;
			
			// 2. 공격
			if (!lazer(attacker, defender)) {
				bomb(attacker, defender);
			}
			
			// 3. 부서진 포탑 체크
			remove(attacker);
			
			// 4. 포탑 정비 (이미 부서진 포탑은 정비 불가)
			repair(attacker, defender);
			
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
		
		tops.clear();
		isVisited = new boolean[rowSize][colSize];
		isDamaged = new boolean[rowSize][colSize];
		
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				if (map[row][col].power > 0) {
					tops.add(map[row][col]);
				}
			}
		}
		
	}
	
	public static boolean lazer(Top attacker, Top defender) {
		
		Queue<Node> nodes = new ArrayDeque<>();
		nodes.offer(new Node(attacker.row, attacker.col));
		isVisited[attacker.row][attacker.col] = true;
		
		int attackPower = attacker.power;
		
		while(!nodes.isEmpty()) {
			
			Node node = nodes.poll();
			
			if (node.row == defender.row && node.col == defender.col) {
				
				map[node.row][node.col].power -= attackPower;
				
				Node prev = node.prev;
				while(!Objects.isNull(prev.prev)) {
					isDamaged[prev.row][prev.col] = true;
					map[prev.row][prev.col].power -= (attackPower / 2);
					prev = prev.prev;
				}
				
				return true;
				
			}
			
			for (int dir = 0; dir < 4; dir++) {
				
				int nRow = (node.row + dr[dir] + rowSize) % rowSize;
				int nCol = (node.col + dc[dir] + colSize) % colSize;
				
				if (isVisited[nRow][nCol] || map[nRow][nCol].power == 0) {
					continue;
				}
				
				isVisited[nRow][nCol] = true;
				nodes.offer(new Node(nRow, nCol, node));
				
			}
			
		}
		
		return false;
		
	}
	
	public static void bomb(Top attacker, Top defender) {
		
		defender.power -= attacker.power;
		
		for (int dir = 0; dir < dr.length; dir++) {
			
			int nRow = (defender.row + dr[dir] + rowSize) % rowSize;
			int nCol = (defender.col + dc[dir] + colSize) % colSize;
			
			if (map[nRow][nCol].power == 0) {
				continue;
			}
			
			if (nRow != attacker.row && nCol != attacker.col) {
				continue;
			}
		
			isDamaged[nRow][nCol] = true;
			map[nRow][nCol].power -= (attacker.power / 2);
			
		}
		
	}
	
	public static void remove(Top attacker) {
	
		for (int row = 0; row < rowSize; row++) {
			
			for (int col = 0; col < colSize; col++) {
				
				if (map[row][col].power < 0) {
					map[row][col].power = 0;
				}
				
			}
			
		}
	
		
	}
	
	
	public static void repair(Top attacker, Top defender) {
		
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
		
		handicap = (rowSize + colSize);
		map = new Top[rowSize][colSize];
		tops = new ArrayList<>();
		for (int row = 0; row < rowSize; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < colSize; col++) {
				int power = Integer.parseInt(inputs[col]);
				map[row][col] = new Top(row, col, power);
			}
		}
		
	}
	
}
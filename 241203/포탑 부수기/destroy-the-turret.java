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
		boolean isDamaged;
		
		Top(int row, int col, int power) {
			this.row = row;
			this.col = col;
			this.power = power;
		}
		
		@Override
		public int compareTo(Top t) {
			
			if (powers[this.row][this.col] == powers[t.row][t.col]) {
		
				if (this.attackedAt == t.attackedAt) {
					
					if (this.row + this.col == t.row + t.col) {
					
						return Integer.compare(t.col, this.col);
				
					}
					
					return Integer.compare(t.row + t.col, this.row + this.col);
			
				}	
				
				return Integer.compare(t.attackedAt, this.attackedAt);
			
			}
		
			return Integer.compare(powers[this.row][this.col], powers[t.row][t.col]);
			
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
	static int[][] powers;
	static List<Top> tops;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		
		for (int rCount = 1; rCount <= roundCount; rCount++) {
			
			if (isFinished()) {
				System.out.println(tops.get(0).power);
				return;
			}
			
			// 1. 공격자 선정
			Collections.sort(tops);
			Top attacker = tops.get(0);
			Top defender = tops.get(tops.size() - 1);	
			
			// 2. 공격
			powers[attacker.row][attacker.col] += handicap;
			attacker.attackedAt = rCount;
			attack(attacker, defender);
			
			sync(attacker);
			
			// 3. 부서진 포탑 체크
			remove(attacker);
			
			for (int row = 0; row < rowSize; row++) {
				for (int col = 0; col < colSize; col++) {
					System.out.print(powers[row][col] + " ");
				}
				System.out.println();
			}
			
			// 4. 포탑 정비 (이미 부서진 포탑은 정비 불가)
			repairTop(attacker, defender);
			
		}
		
		int max = Integer.MIN_VALUE;
		for (Top top : tops) {
			max = Math.max(max, top.power);
		}
		
		sb.append(max);
		bw.write(sb.toString());
		bw.close();
		
    }
	
	public static void attack(Top attacker, Top defender) {
		
		boolean isPossible = false;
		boolean[][] isVisited = new boolean[rowSize][colSize];
		Queue<Node> nodes = new ArrayDeque<>();
		nodes.offer(new Node(attacker.row, attacker.col));
		isVisited[attacker.row][attacker.col] = true;
		
		int attackPower = powers[attacker.row][attacker.col];
		
		while(!nodes.isEmpty()) {
			
			Node node = nodes.poll();
			
			if (node.row == defender.row && node.col == defender.col) {
				
				powers[node.row][node.col] -= attackPower;
				Node prev = node.prev;
				while(!Objects.isNull(prev.prev)) {
					powers[prev.row][prev.col] -= (attackPower / 2);
					prev = prev.prev;
				}
				
				isPossible = true;
				break;
			}
			
			for (int dir = 0; dir < 4; dir++) {
				
				int nRow = (node.row + dr[dir] + rowSize) % rowSize;
				int nCol = (node.col + dc[dir] + colSize) % colSize;
				
				if (isVisited[nRow][nCol] || powers[nRow][nCol] == 0) {
					continue;
				}
				
				isVisited[nRow][nCol] = true;
				nodes.offer(new Node(nRow, nCol, node));
				
			}
			
		}
		
		if (!isPossible) {
			
			powers[defender.row][defender.col] -= attackPower;
			
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = (defender.row + dr[dir] + rowSize) % rowSize;
				int nCol = (defender.col + dc[dir] + colSize) % colSize;
				
				if (powers[nRow][nCol] > 0) {
					powers[nRow][nCol] -= (attackPower / 2);
				}
				
			}
		}
		
		return;
		
	}
	
	public static void sync(Top attacker) {
		
		attacker.power = powers[attacker.row][attacker.col];
        
		for (int tCount = 0; tCount < tops.size(); tCount++) {
			Top top = tops.get(tCount);
			
			if (top.power != powers[top.row][top.col]) {
				top.isDamaged = true;
			}
			
			top.power = powers[top.row][top.col];
		}
		
	}
	
	public static void remove(Top attacker) {
			
		List<Top> candidates = new ArrayList<>();
		for (int tCount = 0; tCount < tops.size(); tCount++) {
			
			Top top = tops.get(tCount);
			
			if (top.power <= 0) {
				powers[top.row][top.col] = 0;
				candidates.add(top);
			}
			
		}
		
		for (Top c : candidates) {
			tops.remove(c);
		}
		
	}
	
	
	public static void repairTop(Top attacker, Top defender) {
		
		for (int tCount = 0; tCount < tops.size(); tCount++) {
			
			Top top = tops.get(tCount);
			
			if (top.isDamaged || top.equals(attacker) || top.equals(defender)) {
				top.isDamaged = false;
				continue;
			}		
			
			top.power++;
			powers[top.row][top.col]++;
			
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
		powers = new int[rowSize][colSize];
		tops = new ArrayList<>();
		for (int row = 0; row < rowSize; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < colSize; col++) {
				powers[row][col] = Integer.parseInt(inputs[col]);
				
				if (powers[row][col] != 0) {
					tops.add(new Top(row, col, powers[row][col]));
				}
				
			}
		}
		
	}
	
}
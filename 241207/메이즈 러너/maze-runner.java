import java.util.*;
import java.io.*;

/**
 * CT_메이즈러너
 * @author parkrootseok
 **/
public class Main {
	
	public static class Node {
		
		int row;
		int col;
		int size;
		
		public Node(int row, int col, int size) {
			this.row = row;
			this.col = col;
			this.size = size;
		}
			
	}
	
	public static class Person {
		
		int index;
		int row;
		int col;
		int distance;
		
		Person(int index, int row, int col, int distance) {
			this.index = index;
			this.row = row;
			this.col = col;
			this.distance = distance;
		}
		
		public void move() {
			
			int mRow = -1;
			int mCol = -1;
			int min = getDiff(this.row, this.col);
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = this.row + dr[dir];
				int nCol = this.col + dc[dir];
				
				// 범위 안이면서 벽이 아닌 곳인지 확인
				if (!isPossible(nRow, nCol)) {
					continue;
				}
				
				// 현재 위치보다 출구와 가까워져야 함
				int diff = getDiff(nRow, nCol);
				if (min > diff) {
					min = diff;
					mRow = nRow;
					mCol = nCol;
				}
				
			}
			
			if (mRow != -1 && mCol != -1) {
				this.row = mRow;
				this.col = mCol;
				this.distance++;	
			}
			
		}
		
		public void check() {
			isArrived[this.index] = (gRow == this.row && gCol == this.col);
		}
		
		private int getDiff(int nRow, int nCol) {
			
			return Math.abs(gRow - nRow) + Math.abs(gCol - nCol);
			
		}
		
		private boolean isPossible(int nRow, int nCol) {
			
			if (nRow <= 0 || size < nRow || nCol <= 0 || size < nCol) {
				return false;
			}
			
			if (1 <= map[nRow][nCol] && map[nRow][nCol] <= 9) {
				return false;
			}
			
			return true;
			
		}
		
	}
	
	public static final int EMPTY = 0;
	public static final int EXIT = -1;
	public static final int[] dr = {1, -1, 0, 0};
	public static final int[] dc = {0, 0, -1, 1};

	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int size;
	static int personCount;
	static int gameCount;
	
	static int[][] map;
	static Person[] people;
	static boolean[] isArrived;
	
	static int gRow;
	static int gCol;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (int gCount = 1; gCount <= gameCount; gCount++) {
		
			if (isFinished()) {
				break;
			}
			
			for (Person p : people) {
				
				if (isArrived[p.index]) {
					continue;
				}
				
				p.move();
				p.check();
				
			}
			
			Node condition = getBestCondition();
			if (!Objects.isNull(condition)) {
				rotationMaze(condition.row, condition.col, condition.size);
			}
			
		}
		
		output();
		bw.write(sb.toString());
		bw.close();
		
    }
	
	
	public static Node getBestCondition() {
	
		for (int cSize = 1; cSize <= size; cSize++) {
			
			for (int row = 1; row <= size; row++) {
				
				for (int col = 1; col <= size; col++) {
					
					if (size < row + cSize || size < col + cSize) {
						continue;
					}
					
					if (isPossible(row, col, cSize)) {
						return new Node(row, col, cSize);
					}
					
				}
				
			}
			
		}
		
		return null;
		
	}
	
	public static void rotationMaze(int sRow, int sCol, int cSize) {
		
		List<Person> rotationPerson = new ArrayList<>();
		int[][] rotationMap = new int[size + 1][size + 1];
		
		for (int row = sRow; row <= sRow + cSize; row++) {
			
			for (int col = sCol; col <= sCol + cSize; col++) {
				
				int offsetRow = row - sRow;
				int offsetCol = col - sCol;
				int rotationRow = offsetCol + sRow;
				int rotationCol = cSize - offsetRow + sCol;
				
				
				if (1 <= map[row][col] && map[row][col] <= 9) {
					rotationMap[rotationRow][rotationCol] = (map[row][col] - 1);
				} else if (map[row][col] == EXIT) {
					rotationMap[rotationRow][rotationCol] = EXIT;
					gRow = rotationRow;
					gCol = rotationCol;
				}
				
				for (Person p : people) {
					if (p.row == row && p.col == col) {
						rotationPerson.add(new Person(p.index, rotationRow, rotationCol, p.distance));
					}
				}
				
			}
			
		}
		
		for (int row = sRow; row <= sRow + cSize; row++) {
			for (int col = sCol; col <= sCol + cSize; col++) {
				map[row][col] = rotationMap[row][col];
			}
		}
		
		for (Person p : rotationPerson) {
			people[p.index] = p;
		}
		
	}
	
	public static boolean isPossible(int row, int col, int cSize) {
		
		boolean hasExit = false;
		for (int cRow = row; cRow <= row + cSize; cRow++) {
			
			for (int cCol = col; cCol <= col + cSize; cCol++) {
				if (gRow == cRow && gCol == cCol) {
					hasExit = true;
					break;
				}
			}
			
			if (hasExit) {
				break;
			}
			
		}
		
		if (hasExit) {
			for (Person p : people) {
				if (!isArrived[p.index] && (row <= p.row && p.row <= row + cSize) && (col <= p.col && p.col <= col + cSize)) {
					return true;
				}
			}	
		}
		
		return false;
		
	}
	
	public static boolean isFinished() {
		
		for (boolean b : isArrived) {
			if (!b) {
				return false;
			}
		}
	
		return true;
	}
	
	public static void input() throws IOException{
		
		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		personCount = Integer.parseInt(inputs[1]);
		gameCount = Integer.parseInt(inputs[2]);
		
		map = new int[size + 1][size + 1];
		for (int row = 1; row <= size; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 1; col <= size; col++) {
				map[row][col] = Integer.parseInt(inputs[col - 1]);
			}
		}
		
		people = new Person[personCount];
		isArrived = new boolean[personCount];
		for (int pCount = 0; pCount < personCount; pCount++) {
			
			inputs = br.readLine().trim().split(" ");
			
			int row = Integer.parseInt(inputs[0]);
			int col = Integer.parseInt(inputs[1]);
			
			people[pCount] = new Person(pCount, row, col, 0);
		
		}
		
		inputs = br.readLine().trim().split(" ");	
		gRow = Integer.parseInt(inputs[0]);
		gCol = Integer.parseInt(inputs[1]);
		map[gRow][gCol] = EXIT;
		
	}
	
	public static void output() {
		
		int sum = 0;
		for (Person p : people) {
			sum += p.distance;
		}
		
		sb.append(sum).append("\n").append(gRow).append(" ").append(gCol);
	
	}
	
}
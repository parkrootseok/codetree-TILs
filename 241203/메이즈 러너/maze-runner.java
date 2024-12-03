import java.util.*;
import java.io.*;

/**
 * CT_메이즈러너
 * @author parkrootseok
 **/
public class Main {
	
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
			
			rotation();
			
		}
		
		output();
		bw.write(sb.toString());
		bw.close();
		
    }
	
	
	public static void rotation() {
	
		for (int cSize = 1; cSize <= size; cSize++) {
			
			for (int row = 1; row <= size; row++) {
				
				for (int col = 1; col <= size; col++) {
					
					if (size < row + cSize || size < col + cSize) {
						continue;
					}
					
					if (isPossible(row, col, cSize)) {
						rotationMaze(row, col, cSize);
						return;
					}
					
				}
				
			}
			
		}
		
	}
	
	public static void rotationMaze(int row, int col, int cSize) {
		
		List<Person> rotationPerson = new ArrayList<>();
		int[][] rotationMap = new int[size + 1][size + 1];
		
		for (int cCol = col, oRow = row; cCol <= col + cSize; cCol++, oRow++) {
			
			for (int cRow = row + cSize, oCol = col; cRow >= row; cRow--, oCol++) {
				
				if (1 <= map[cRow][cCol] && map[cRow][cCol]<= 9) {
					rotationMap[oRow][oCol] = (map[cRow][cCol] - 1);
				} else if (map[cRow][cCol] == EXIT) {
					rotationMap[oRow][oCol] = EXIT;
					gRow = oRow;
					gCol = oCol;
				}
				
				for (Person p : people) {
					if (p.row == cRow && p.col == cCol) {
						rotationPerson.add(new Person(p.index, oRow, oCol, p.distance));
					}
				}
				
			}
			
		}
		
		for (int oRow = row; oRow <= row + cSize; oRow++) {
			for (int oCol = col; oCol <= col + cSize; oCol++) {
				map[oRow][oCol] = rotationMap[oRow][oCol];
			}
		}
		
		for (Person p : rotationPerson) {
			people[p.index] = p;
		}
		
	}
	
	public static boolean isPossible(int row, int col, int cSize) {
		
		// (row, col) / (row + cSize, col) / (row, col + cSize) / (row + cSize, col + cSize)
		// 위 좌표안에 탈출구가 있는지 확인
		
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
		
		// 탈출구가 있다면, 참가자도 존재하는지 확인
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
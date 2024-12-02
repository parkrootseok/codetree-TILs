import java.util.*;
import java.io.*;

/**
 * CT_코드트리빵 
 * @author parkrootseok
 **/

public class Main {
	
	public static class Position {
		
		int row;
		int col;
		
		Position(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
		public boolean isMovable() {
		
			if (this.row < 0 || size <= this.row || this.col < 0 || size <= this.col) {
				return false;
			}
			
			if (grid[this.row][this.col] == WALL || grid[this.row][this.col] == PERSON) {
				return false;
			}
			
			return true;
			
		}
		
		public boolean isEqual(Position p) {
			return this.row == p.row && this.col == p.col;
		}
		
		public int getDistance(Position p) {
			return Math.abs(this.row - p.row) + Math.abs(this.col - p.col);
		}
		
		
		
	}
	
	public static class Basecamp implements Comparable<Basecamp>{
		
		Position position;
		boolean isUsed;
		
		Basecamp(int row, int col) {
			this.position = new Position(row, col);
		}
		
		@Override
		public int compareTo(Basecamp o) {
			
			if (this.position.row == o.position.row) {
				return Integer.compare(this.position.col, o.position.col);
			}
			
			return Integer.compare(this.position.row, o.position.row);
			
		}

	}
	
	public static class Store {
		
		Position position;
		boolean isArrived;
		
		Store(int row, int col) {
			this.position = new Position(row, col);
		}
		
	}
	
	public static class Person {
		
		int index;
		Position position;
		boolean isStart;
		
		Person(int index) {
			this.index = index;
		}
		
		public void move() {
			
			int min = Integer.MAX_VALUE;
			Position candidate = null;
			Store goal = stores[index];
			for (int dir = 0; dir < dr.length; dir++) {
				
				int nRow = this.position.row + dr[dir];
				int nCol = this.position.col + dc[dir];
				Position nPos = new Position(nRow, nCol);
				
				if (!nPos.isMovable()) {
					continue;
				}
				
				int distance = goal.position.getDistance(new Position(nRow, nCol));
				if (min > distance) {
					min = distance;
					candidate = nPos;
				}
				
			}
			
			grid[this.position.row][this.position.col] = EMPTY;
			this.position = candidate;
			grid[candidate.row][candidate.col] = PERSON;
			
			if (stores[this.index].position.isEqual(this.position)) {
				stores[this.index].isArrived = true;
			}
				
		}
		
		public void start() {
		
			List<Basecamp> candidates = new ArrayList<>();
			int min = Integer.MAX_VALUE;
			Store goal = stores[index];
			for (Basecamp b : basecamps) {
				
				if (b.isUsed) {
					continue;
				}
				
				int distance = goal.position.getDistance(b.position);
				
				if (min > distance) {
					min = distance;
					candidates.clear();
					candidates.add(b);
				} else if (min == distance) {
					candidates.add(b);
				}
				
			}
			
			Collections.sort(candidates);
			
			Basecamp selected = candidates.get(0);
			selected.isUsed = true;
			this.position = new Position(selected.position.row, selected.position.col);
			this.isStart = true;
			
		}
		
	}
	
	public static final int EMPTY = 0;
	public static final int BASECAMP = 1;
	public static final int WALL = 2;
	public static final int PERSON = 3;
	
	public static final int[] dr = {-1, 0, 0, 1};
	public static final int[] dc = {0, -1, 1, 0};
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int size;
	static int[][] grid;
	static int personCount;
	
	static Person[] people;
	static List<Basecamp> basecamps;
	static Store[] stores;
	
	public static void main(String[] args) throws IOException {
		
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		int time = 0;
		while (!isFinish()) {
			
			for (int pCount = 0; pCount < personCount; pCount++) {
				Person player = people[pCount]; 
				if (stores[pCount].isArrived) {
					continue;
				}
				
				if (player.isStart) {
					player.move();
				} 
			}
			
			if (time < personCount) {
				if (!people[time].isStart) {
					people[time].start();
				} 
			} 
			
			for (Basecamp b : basecamps) {
				if (b.isUsed) {
					grid[b.position.row][b.position.col] = WALL;
				}
			}
	
			time++;
			
		}
		
		sb.append(time);
		bw.write(sb.toString());
		bw.close();
	
	}
	
	public static boolean isFinish() {
		
		for (Store s : stores) {
			if (!s.isArrived) {
				return false;
			}
		}
		
		return true;
		
	}
	
	public static void input() throws IOException {
		
		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		personCount = Integer.parseInt(inputs[1]);
		
		grid = new int[size][size];
		basecamps = new ArrayList<>();
		for (int row = 0; row < size; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 0; col < size; col++) {
				grid[row][col] = Integer.parseInt(inputs[col]);
				if (grid[row][col] == BASECAMP) {
					basecamps.add(new Basecamp(row, col));
				}
			}
		}
		
		people = new Person[personCount];
		stores = new Store[personCount];
		for (int pCount = 0; pCount < personCount; pCount++) {
			inputs = br.readLine().trim().split(" ");
			int row = Integer.parseInt(inputs[0]) - 1;
			int col = Integer.parseInt(inputs[1]) - 1;
			stores[pCount] = new Store(row, col);
			people[pCount] = new Person(pCount);
		}
		
	}

}
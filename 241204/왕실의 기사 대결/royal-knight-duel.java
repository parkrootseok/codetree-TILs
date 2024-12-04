import java.util.*;
import java.io.*;

/**
 * CT_왕실의기사대결
 * @author parkrootseok
 **/
public class Main {
	
	static class Command {
		
		int id;
		int direction;
		
		Command(int id, int direction) {
			this.id = id;
			this.direction = direction;
		}
		
	}

	static class Person {
		
		int index;
		int row;
		int col;
		int h;
		int w;
		int hp;
		int damage;
		boolean isChange;
		
		Person(int index, int row, int col, int h, int w, int hp, boolean isChange) {
			this.index = index;
			this.row = row;
			this.col = col;
			this.h = h;
			this.w = w;
			this.hp = hp;
			this.isChange = isChange;
		}
		
		public void check() {
			
			int count = 0;
			for (int row = this.row; row <= this.row + this.h; row++) {
				for (int col = this.col; col <= this.col + this.w; col++) {
					if (map[row][col] == TRAP) {
						count++;
					}
					
				}
			}
			
			if (this.hp < count) {
				this.damage += this.hp;
				this.hp = 0;
			} else {
				this.damage += count;
				this.hp -= count;
			}
			
		}
		
		public void move(int dir) {
			
			int nRow = this.row + dr[dir];
			int nCol = this.col + dc[dir];		
			
			List<Person> afterMove = new ArrayList<>();
			Queue<Person> peopleQ = new ArrayDeque<>();
			peopleQ.offer(new Person(this.index, nRow, nCol, this.h, this.w, this.hp, false));
			
			while(!peopleQ.isEmpty()) {
				
				// 이전에 이동한 사람
				Person prevMove = peopleQ.poll();
				
				// 이전에 이동한 사람의 위치가 유효한지 확인
				if (!prevMove.isValid()) {
					return;
				}
				
				afterMove.add(prevMove);
				
				// 이전에 이동한 사람과 겹치는 영역을 가지고 있는 사람을 탐색
				for (Person p : people) {	
					if (p.isDead()) {
						continue;
					}
					if (prevMove.index != p.index && prevMove.sameArea(p)) {
						peopleQ.add(new Person(p.index, p.row + dr[dir], p.col + dc[dir], p.h, p.w, p.hp, true));
					}
				}
				
			}
		
			// 유효하다면 실제로 이동
			for (Person p : afterMove) {
				people[p.index].index = p.index;
				people[p.index].row = p.row;
				people[p.index].col = p.col;
				people[p.index].h = p.h;
				people[p.index].w = p.w;
				people[p.index].isChange = p.isChange;
			}
			
		}
		
		
		private boolean isValid() {
			
			// 자신의 위치가 체스판 안에 있는지 확인
			if (outRange(this.row, this.col)) {
				return false;
			}
			
			// 자신의 영역에 벽이 없으면서 체스판안에 위치해야함 
			return isMoveable(this.row, this.col);
			
		}
		
		private boolean outRange(int nRow, int nCol) {
			return nRow <= 0 || size < nRow || nCol <= 0 || size < nCol;
		}
		
		private boolean isMoveable(int nRow, int nCol) {
			
			for (int row = nRow; row <= nRow + this.h; row++) {
				for (int col = nCol; col <= nRow + this.w; col++) {
					
					if (outRange(row, col)) {
						return false;
					}
					
					if (map[row][col] == WALL) {
						return false;
					}
					
				}
			}
			
			return true;
			
		}
		
		private boolean sameArea(Person p) {
			
			boolean[][] area = new boolean[size + 1][size + 1];
			
			for (int row = this.row; row <= this.row + this.h; row++) {
				for (int col = this.col; col <= this.col + this.w; col++) {
					area[row][col] = true;
				}
			}
			
			
			for (int row = p.row; row <= p.row + p.h; row++) {
				for (int col = p.col; col <= p.col + p.w; col++) {
					if (area[row][col]) {
						return true;
					}
				}
			}
			
			return false;
			
		}
		
		public boolean isDead() {
			return this.hp == 0;
		}
		
		@Override
		public String toString() {
			return "[idx:" + this.index + "]\n" + "[pos:" + "{r:"+this.row+",c:"+this.col+"}] " + "[hp:" + this.hp + "]";
		}
		
		
	}
	
	public static final int TRAP = 1;
	public static final int WALL = 2;
	public static final int[] dr = {-1, 0, 1, 0};
	public static final int[] dc = {0, 1, 0, -1};
	
	static BufferedReader br;
	static BufferedWriter bw;
	static StringBuilder sb;
	
	static int size;
	static int personCount;
	static int commandCount;
	
	static int[][] map;
	static Person[] people;
	static Command[] commands;
	
	public static void main(String[] args) throws IOException {
        
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();
		
		input();
		
		for (Command c : commands) {
			

			Person player = people[c.id - 1];
			
			if (player.isDead()) {
				continue;
			}
			
			player.move(c.direction);
			
			for (Person p : people) {
				if (p.isChange) {
					p.check();
					p.isChange = false;
				}
			}
			
		}

		sb.append(output());
		bw.write(sb.toString());
		bw.close();
		
    }

	public static int output() {
		
		int answer = 0;
		for (Person p : people) {
			if (!p.isDead()) {
				answer += p.damage;
			}
		}
		return answer;
	}
	
	public static void input() throws IOException {
		
		String[] inputs = br.readLine().trim().split(" ");
		size = Integer.parseInt(inputs[0]);
		personCount = Integer.parseInt(inputs[1]);
		commandCount = Integer.parseInt(inputs[2]);
		
		map = new int[size + 1][size + 1];
		for (int row = 1; row <= size; row++) {
			inputs = br.readLine().trim().split(" ");
			for (int col = 1; col <= size; col++) {
				map[row][col] = Integer.parseInt(inputs[col - 1]);
			}
		}
		
		people = new Person[personCount];
		for (int pCount = 0; pCount < personCount; pCount++) {
			
			inputs = br.readLine().trim().split(" ");
			
			int r = Integer.parseInt(inputs[0]);
			int c = Integer.parseInt(inputs[1]);
			int h = Integer.parseInt(inputs[2]) - 1;
			int w = Integer.parseInt(inputs[3]) - 1;
			int hp = Integer.parseInt(inputs[4]);
			people[pCount] = new Person(pCount, r, c, h, w, hp, false);
			
		}
		
		commands = new Command[commandCount];
		for (int cCount = 0; cCount < commandCount; cCount++) {
			
			inputs = br.readLine().trim().split(" ");

			int id = Integer.parseInt(inputs[0]);
			int dir = Integer.parseInt(inputs[1]);
			commands[cCount] = new Command(id, dir);
		
		}
		
	}
	
}
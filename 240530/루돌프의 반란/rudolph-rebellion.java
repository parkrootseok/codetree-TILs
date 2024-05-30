import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import com.sun.org.apache.xpath.internal.objects.XObject;

/**
 * CT_루돌프의반란
 * @author parkrootseok
 *
 * 루돌프
 * - 가장 가까운 산타를 향해 1칸 돌진
 *  - 만약, 2개 이상이 존재하면 r좌표가 큰 산타로 돌진하고 동일하면 c좌표가 큰 산타를 향해 돌진
 *  - 8방향으로 움직일 수 있음
 *  - 8방향 중 가장 가까워 지는 방향으로 한 칸 돌진
 *
 * 산타
 *  - 1번부터 순차적으로 이동 시작
 *  - 루돌프와 가까운 방향으로 1칸 이동
 *  - 만약, 루돌프와 가까워 질 수 없다면 이동하지 않음
 *  - 4방향으로 움직일 수 있음
 *  - 가능한 방향이 여러 개라면 상 -> 우 -> 하 -> 좌 우선순위에 맞게 이동
 *
 * 충돌
 *  - 루돌프가 움직여서 발생한 경우 -> 산타 C 점수 획득 -> 산타는 루돌프가 이동한 방향으로 C만큼 이동
 *  - 산타가 움직여서 발생한 경우 -> 산타는 D 만큼 획득 -> 산타는 자신이 이동한 '반대' 방향으로 D만큼 이동
 *  - 밀려난 칸에 다른 산타가 있는 경우 상호작용이 발생
 *
 * 상호작용
 *  - 기존 칸에 있던 산타는 들어온 산타와 동일한 방향으로 1칸 밀려남
 *  - 기존 산타가 밀려난 곳에 또 산타가 있다면 연쇄적으로 작용함
 *
 * 기절
 *  - 산타는 루돌프와 충돌 후 기절
 *  - 기절은 K + 1(다음 턴)까지 K + 2(다다음 턴)부터는 다시 정상
 *  - 움직일 수 없지만 상호작용에는 반응
 *  - 루돌프는 기절한 산타에게도 돌진 가능
 *
 * 1. 입력
 *  1-1. 크기, 턴 수, 산타수, 루돌프 힘, 산타 힘 입력
 *  1-2. 루돌프 초기 위치 입력 후 루돌프 객체 생성
 *  1-3. 산타 번호, 초기 위치를 받고 산타 객체를 생성
 *  2. 게임 진행
 *   2-1. 루돌프 이동
 *   2-2. 충돌 발생 여부 확인(루돌프에 의해 발생)
 *    2-2-1. 충돌이 발생한 경우 상호작용 발생
 *   2-3. 산타 이동
 *   2-4. 충돌 발생 여부 확인(산타에 의해 발생)
 *   2-5. 충돌한 산타가 있을 경우 상호작용 발생
 *   2-6. 기절한 산타들 딜레이 증가 후 상태 초기화 및 탈락하지 않은 산타 점수 증가
 *   2-7. 살아있는 산타가 없다면 게임 종료
 **/
public class Main {

	public static BufferedReader br;
	public static BufferedWriter bw;
	public static StringBuilder sb;
	public static String[] inputs;

	public static int size;
	public static int totalTurnNumber;
	public static int santaNumber;
	public static int santaPower;
	public static int ludolphPower;

	public static Santa[] santas;
	public static Ludolph ludolph;
	public static Santa conflictSanta;

	public static void input() throws IOException {

		//  1-1. 크기, 턴 수, 산타수, 루돌프 힘, 산타 힘 입력
		inputs = br.readLine().trim().split(" ");

		size = Integer.parseInt(inputs[0]);
		totalTurnNumber = Integer.parseInt(inputs[1]);
		santaNumber = Integer.parseInt(inputs[2]);
		ludolphPower = Integer.parseInt(inputs[3]);
		santaPower = Integer.parseInt(inputs[4]);

		//  1-2. 루돌프 초기 위치 입력 후 루돌프 객체 생성
		inputs = br.readLine().trim().split(" ");
		ludolph = new Ludolph(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));

		//  1-3. 산타 번호, 초기 위치를 받고 산타 객체를 생성
		santas = new Santa[santaNumber];
		for (int index = 0; index < santaNumber; index++) {
			inputs = br.readLine().trim().split(" ");
			int number = Integer.parseInt(inputs[0]);
			int row = Integer.parseInt(inputs[1]);
			int col = Integer.parseInt(inputs[2]);

			santas[number - 1] = new Santa(number, row, col);
		}

	}

	public static void main(String[] args) throws IOException {

		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
		sb = new StringBuilder();

		// 1. 입력
		input();

		// 2. 받은 턴 수 만큼 게임 진행
		for (int curTurnNumber = 0; curTurnNumber < totalTurnNumber; curTurnNumber++) {

			System.out.println(curTurnNumber + "번 째 턴 시작");

			for (Santa santa : santas) {
				System.out.println(santa);
			}

			// 2-1. 루돌프 이동
			ludolph.move();

			System.out.println("----2-1. 루돌프 이동----");
			System.out.println(ludolph.pos);

			// 2-2. 충돌 발생 여부 확인(루돌프에 의해 발생)
			if (ludolph.hasConflict()) {
				System.out.println("----2-2. 루돌프에 의해 충돌 발생----");

				// 2-2-1. 상호작용 발생
				interactionByLudolph();
				System.out.println("----2-2-1. 상호 작용 발생 후----");
				for (Santa santa : santas) {
					System.out.println(santa.number+ " " + santa.pos);
				}

			}

			System.out.println("----2-4. 산타 이동----");
			// 2-3. 산타 이동
			for (Santa santa : santas) {

				if (santa.isAlive && santa.isMovable) {
					santa.move();
					System.out.println(santa.number+ " " +santa.pos);
				}

			}

			System.out.println("----2-5. 산타에 의해 충돌 발생----");
			// 2-4. 충돌 발생 여부 확인(산타에 의해 발생)
			for (Santa santa : santas) {
				if (santa.isAlive) {
					santa.hasConflict();
				}
			}

			// 2-5. 충돌한 산타가 있을 경우 상호작용 발생
			if (!Objects.isNull(conflictSanta)) {
				interactionBySanta();
				System.out.println("----2-6. 상호 작용 발생----");
				for (Santa santa : santas) {
					if(santa.isAlive) {
						System.out.println(santa.number+ " " +santa.pos);
					}
				}
			}

			// 2-6. 기절한 산타들 딜레이 증가 후 상태 초기화 및 탈락하지 않은 산타 점수 증가
			boolean isFinished = true;
			for (Santa santa : santas) {

				if (santa.isAlive) {
					santa.score++;
					isFinished = false;
				}

				if (!santa.isMovable) {

					// 증가한 값이 2라면 상태 초기화
					if (++santa.delay == 2) {
						santa.isMovable = true;
                        santa.delay = 0;
					}

				}

			}

			if (isFinished) {
				break;
			}

		}

		for (Santa santa : santas) {
			sb.append(santa.score).append(" ");
		}

		bw.write(sb.toString());
		bw.close();

	}

	public static void interactionByLudolph() {

		while (true) {

			// 같은 위치에 존재한는 산타들이 있는지 확인
			Santa findSanta = null;
			for (Santa santa : santas)  {

				if (conflictSanta.number == santa.number) {
					continue;
				}

				// 같은 위치에 산타가 존재하면
				if (conflictSanta.pos.row == santa.pos.row && conflictSanta.pos.col == santa.pos.col)  {
					// 리스트에 추가
					findSanta = santa;
				}

			}

			// 같은 위치에 존재하는 산타가 없을 경우 종료
			if (Objects.isNull(findSanta)) {
				conflictSanta = null;
				return;
			}

			// 충돌이 발생한 산타를 루돌프 진행 방향으로 한 칸 이동
			findSanta.pos.row += ludolph.dr[ludolph.recentDirection];
			findSanta.pos.col += ludolph.dc[ludolph.recentDirection];

			// 충돌로 인해 이동한 위치에서도 상호 작용을 수행하기 위해 충돌한 산타를 변경
			conflictSanta = findSanta;

		}

	}

	public static void interactionBySanta() {

		while (true) {

			// 같은 위치에 존재한는 산타들이 있는지 확인
			Santa findSanta = null;
			for (Santa santa : santas)  {

				if (conflictSanta.number == santa.number) {
					continue;
				}

				// 같은 위치에 산타가 존재하면
				if (conflictSanta.pos.row == santa.pos.row && conflictSanta.pos.col == santa.pos.col)  {
					// 리스트에 추가
					findSanta = santa;
				}

			}

			// 같은 위치에 존재하는 산타가 없을 경우 종료
			if (Objects.isNull(findSanta)) {
				conflictSanta = null;
				return;
			}

			// 충돌이 발생한 산타를 이동한 방향의 반대로 1칸 이동
			findSanta.pos.row -= conflictSanta.dr[conflictSanta.recentDirection];
			findSanta.pos.col -= conflictSanta.dc[conflictSanta.recentDirection];

			// 충돌로 인해 이동한 위치에서도 상호 작용을 수행하기 위해 충돌한 산타를 변경
			findSanta.recentDirection = conflictSanta.recentDirection;
			conflictSanta = findSanta;

		}

	}


	public static class Position {

		int row;
		int col;

		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public int getDistance(Position santa) {
			return (int) (Math.pow((this.row - santa.row), 2) + Math.pow((this.col - santa.col), 2));
		}

		public boolean isValid() {
			return 1 <= row && row <= size && 1 <= col && col <= size;
		}

		@Override
		public String toString() {
			return "{" +
				"row=" + row +
				", col=" + col +
				'}';
		}
	}

	public static class Ludolph {

		int[] dr = {1, -1, 0, 0, 1, 1, -1, -1};
		int[] dc = {0, 0, 1, -1, 1, -1, 1, -1};

		Position pos;
		int recentDirection;

		public Ludolph(int row, int col) {
			this.pos = new Position(row, col);
			this.recentDirection = -1;
		}

		public void move() {

			// 가장 거리가 가까운 산타를 탐색
			PriorityQueue<Santa> findSantas = new PriorityQueue<>();
			int minDis = Integer.MAX_VALUE;
			for (Santa santa : santas) {

				// 살아있는 산타가 아니라면 스킵
				if (!santa.isAlive) {
					continue;
				}

				// 살아있는 산타라면 루돌프와의 거리 차이를 구한 후
				int distance = ludolph.pos.getDistance(santa.pos);

				// 현재 최소값보다 작을 때
				if (minDis > distance) {
					minDis = distance;

					// 우선순위 큐를 초기화 후
					findSantas.clear();

					// 산타를 추가
					findSantas.add(santa);
				}

				// 현재 최소값과 동일할 때
				else if (minDis == distance) {
					// 우선순위 큐에 추가
					findSantas.add(santa);
				}

			}

			// 찾은 산타와 가장 가까운 거리로 이동
			// 단, 산타는 r이 가장 크고 동일한 경우에 c가 가장 큰 산타
			Santa findSanta = findSantas.poll();
			minDis = Integer.MAX_VALUE;

			int findDirection = -1;
			for (int dir = 0; dir < this.dr.length; dir++) {

				// 기존 방향에서 이동한 방향
				Position newPos = new Position(this.pos.row + this.dr[dir], this.pos.col +  this.dc[dir]);

				// 이동한 방향이 유효한 방향이 아니라면 스킵
				if (!newPos.isValid()) {
					continue;
				}

				// 이동한 방향과 산타와의 거리를 계산 후 최소 거리를 가지는 방향을 기록
				int dis = newPos.getDistance(findSanta.pos);
				if (minDis > dis) {
					minDis = dis;
					findDirection = dir;
				}

			}

			// 위에서 찾은 가장 가까워지는 방향으로 1칸 이동
			this.pos.row += this.dr[findDirection];
			this.pos.col += this.dc[findDirection];

			// 가장 최근에 이동한 방향을 기록
			recentDirection = findDirection;

		}

		public boolean hasConflict() {

			// 충돌이 발생한 산타를 탐색
			conflictSanta = null;
			for (Santa santa : santas) {

				// 살아있지 않은 산타는 스킵
				if (!santa.isAlive) {
					continue;
				}

				// 충동이 일어나지 않은 경우 스킵
				if (this.pos.row != santa.pos.row || this.pos.col != santa.pos.col) {
					continue;
				}

				// 산타는 루돌프의 힘만큼 점수를 획득
				santa.score += ludolphPower;
				conflictSanta = santa;
				break;

			}

			// 충돌한 산타가 없다면 종료
			if (Objects.isNull(conflictSanta)) {
				return false;
			}

			// 충돌이 일어난 산타의 경우 루돌프의 진행 방향과 동일하게 힘만큼 이동
			conflictSanta.pos.row += (this.dr[recentDirection] * ludolphPower);
			conflictSanta.pos.col += (this.dc[recentDirection] * ludolphPower);

			// 충돌이 발생한 산타의 생사여부를 초기화
			conflictSanta.initAlive();

			// 루돌프와 충돌한 산타는 기절
			conflictSanta.isMovable = false;
			conflictSanta.delay = 0;

			// System.out.println(conflictSanta.number + " " + conflictSanta.pos);
			return true;

		}

		@Override
		public String toString() {
			return "Ludolph {" +
				"pos=" + pos +
				'}';
		}
	}

	public static class Santa implements Comparable<Santa> {

		int[] dr = {-1, 0, 1, 0};
		int[] dc = {0, 1, 0, -1};

		int number;
		Position pos;
		int score;
		boolean isMovable;
		boolean isAlive;
		int delay;
		int recentDirection;

		public Santa(int number, int row, int col) {
			this.number = number;
			this.pos = new Position(row, col);
			this.score = 0;
			this.isMovable = true;
			this.isAlive = true;
			this.delay = 0;
			this.recentDirection = -1;
		}

		public boolean initAlive() {

			if (pos.row < 1 || size < pos.row || pos.col < 1 || size < pos.col) {
				isAlive = false;
				return false;
			}

			return true;
		}

		public boolean isAlreadyExist(Position newPos) {

			// 동일한 위치를 가지는 산타가 존재하는지 확인
			for (Santa santa : santas) {

				// 동일한 위치에 산타가 존재하면 true 반환
				if (santa.number != this.number && newPos.row == santa.pos.row && newPos.col == santa.pos.col) {
					return true;
				}

			}

			return false;

		}

		public void move() {

			int curDistance = this.pos.getDistance(ludolph.pos);
			int possibleDirection = -1;
			int minDis = Integer.MAX_VALUE;
			for (int dir = 0; dir < this.dr.length; dir++) {

				Position newPos = new Position(this.pos.row + this.dr[dir], this.pos.col + this.dc[dir]);

				// 이동할 수 없는 좌표라면 스킵
				if (!newPos.isValid())  {
					continue;
				}

				// 동일한 위치를 가지는 산타가 존재하는지 확인
				if (isAlreadyExist(newPos)) {
					continue;
				}

				// 이동 가능한 위치와 루돌프 사이의 거리를 구하고
				int distance = newPos.getDistance(ludolph.pos);

				// 현재 거리보다 더 멀어지는 곳은 이동 스킵
				if (curDistance < distance) {
					continue;
				}

				// 현재 최소값보다 작을 때
				if (minDis > distance) {
					minDis = distance;

					// 방향을 기록
					possibleDirection = dir;
				}

			}

			// 움직일 방향이 정해진 경우에만 이동
			if (possibleDirection != -1) {
				this.pos.row += this.dr[possibleDirection];
				this.pos.col += this.dc[possibleDirection];
			}

			// 최근에 이동한 방향을 기록
			recentDirection = possibleDirection;

		}

		public boolean hasConflict() {

			conflictSanta = null;

			// 살아있지 않은 산타는 스킵
			if (!this.isAlive) {
				return false;
			}

			// 충동이 일어나지 않은 경우 스킵
			if (this.pos.row != ludolph.pos.row || this.pos.col != ludolph.pos.col) {
				return false;
			}

			// 충돌한 산타를 기록
			conflictSanta = this;

			// 산타는 산타의 힘만큼 점수를 획득
			this.score += santaPower;

			// 충돌이 일어난 산타의 경우 산타의 진행 방향의 반대로 동일하게 힘만큼 이동
			this.pos.row -= (this.dr[recentDirection] * santaPower);
			this.pos.col -= (this.dc[recentDirection] * santaPower);

			// 충돌이 발생한 산타의 생사여부를 초기화
			this.initAlive();

			// 루돌프와 충돌한 산타는 기절
			this.isMovable = false;
			this.delay = 0;

			// System.out.println(this.number+ " " +this.pos);
			return true;

		}

		@Override
		public int compareTo(Santa o) {

			// r좌표가 동일할 때 c좌표 내림차순
			if (this.pos.row == o.pos.row) {
				return Integer.compare(o.pos.col, this.pos.col);
			}

			// 기본적으로는 r좌표 내림차순
			return Integer.compare(o.pos.row, this.pos.row);

		}

		@Override
		public String toString() {
			return "Santa{" +
				"number=" + number +
				", pos=" + pos +
				", score=" + score +
				", isAlive=" + isAlive +
				'}';
		}
	}

}
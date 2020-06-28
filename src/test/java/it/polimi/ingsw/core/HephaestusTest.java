package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Hephaestus;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class HephaestusTest {
	private Turn turn;
	private Map map;
	private TypeBuild type;
	private Hephaestus hephaestus;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple build position
	int s,t; //second build

	@Before
	public void testSetup(){
		map = new Map();
		player = new Player("Pippo",0);
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto",50);
		opponent.setPlayerColor(Color.BLACK);
		hephaestus = new Hephaestus(player);
		turn = new Turn();
		while(turn.getGamePhase() != GamePhase.BUILD){
			turn.advance();
		}
	}

	/**
	 * Worker1 in (0,0) (level 0), opponent worker in (1,1) (level 0): it should return 4 builds (2 are the first potentially build, 2 are the second one in the same cell where is the first building), which I compare "manually" with the returned arrayList of the checkBuild
	 */
	@Test
	public void checkBuildTestCorner() throws NoBuildException {
		x=0; y=0; x1=3; y1=3;
		h=1; k=1; h1=4; k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(4, hephaestus.checkBuild(map, player.getWorker1(), turn).size());

		i=0; j=1;
		Build newBuild = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild));

		i=1; j=0;
		Build newBuild1 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild1));

		i=0; j=1; s=0; t=1;
		Build newBuild2 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.CONDITIONED_BUILD);
		newBuild2.setCondition(new Build(player.getWorker1(), map.getCell(s, t), false, TypeBuild.SIMPLE_BUILD));
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild2));

		i=1; j=0; s=1; t=0;
		Build newBuild3 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.CONDITIONED_BUILD);
		newBuild3.setCondition(new Build(player.getWorker1(), map.getCell(s, t), false, TypeBuild.SIMPLE_BUILD));
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild3));

		i=1; j=0; s=1; t=0;
		Build newBuild4 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.CONDITIONED_BUILD);
		newBuild4.setCondition(new Build(player.getWorker1(), map.getCell(s, t), true, TypeBuild.SIMPLE_BUILD));
		assertFalse(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild4)); //assertFalse because he could not build a dome there

	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1), building level 2 in (0,2): it should return only 4 cells which I compare "manually" with the returned arrayList of the checkBuild
	 */
	@Test
	public void checkBuildTestGeneral() throws NoBuildException {
		x=1; y=1; x1=1; y1=2;
		h=2; k=2; h1=2; k1=1;
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().setDome();

		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();

		map.getCell(0, 0).getBuilding().incrementLevel();

		map.getCell(0, 2).getBuilding().incrementLevel();
		map.getCell(0, 2).getBuilding().incrementLevel();

		map.getCell(0,1).getBuilding().setDome(); //dome only: due to Atlas (hypothetically)

		assertEquals(4, hephaestus.checkBuild(map, player.getWorker1(), turn).size());

		i=0; j=0;
		Build newBuild = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild));

		i=1; j=0;
		Build newBuild1 = new Build(player.getWorker1(), map.getCell(i, j), true, TypeBuild.SIMPLE_BUILD);
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild1));

		i=0; j=2;
		Build newBuild2 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild2));

		i=0; j=0; s=0; t=0;
		Build newBuild3 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.CONDITIONED_BUILD);
		newBuild3.setCondition(new Build(player.getWorker1(), map.getCell(s, t), false, TypeBuild.SIMPLE_BUILD));
		assertTrue(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild3));

		i=1; j=0; s=1; t=0;
		Build newBuild4 = new Build(player.getWorker1(), map.getCell(i, j), true, TypeBuild.CONDITIONED_BUILD);
		newBuild4.setCondition(new Build(player.getWorker1(), map.getCell(s, t), false, TypeBuild.SIMPLE_BUILD));
		assertFalse(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild4)); //can't build second time over a dome, obviously

		i=1; j=0; s=1; t=0;
		Build newBuild5 = new Build(player.getWorker1(), map.getCell(i, j), true, TypeBuild.CONDITIONED_BUILD);
		newBuild5.setCondition(new Build(player.getWorker1(), map.getCell(s, t), true, TypeBuild.SIMPLE_BUILD));
		assertFalse(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild5)); //can't build second time (a dome) over a dome, obviously

		i=0; j=2; s=0; t=2;
		Build newBuild6 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.CONDITIONED_BUILD);
		newBuild6.setCondition(new Build(player.getWorker1(), map.getCell(s, t), true, TypeBuild.SIMPLE_BUILD));
		assertFalse(hephaestus.checkBuild(map, player.getWorker1(), turn).contains(newBuild6));  //assertFalse because the second build cannot be a dome

	}
}
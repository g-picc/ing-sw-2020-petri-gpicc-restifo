package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.network.stub.ServerControllerStub;
import it.polimi.ingsw.network.stub.ServerListenerStub;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RemoteViewTest {
	/*private RemoteView rv1;
	private RemoteView rv2;
	private RemoteView rv3;
	private ServerListenerStub listenerStub1;
	private ServerListenerStub listenerStub2;
	private ServerListenerStub listenerStub3;
	private ServerControllerStub controllerStub;
	private GameStub gameStub;
	private String[] playerNames;

	@Before
	public void setupTest() {
		playerNames = new String[]{"Desmond Miles","Alexios","Al Mualim"};
		gameStub = new GameStub(playerNames,true,true);
		listenerStub1 = new ServerListenerStub();
		listenerStub1.setPlayerName(playerNames[0]);
		listenerStub2 = new ServerListenerStub();
		listenerStub2.setPlayerName(playerNames[1]);
		listenerStub3 = new ServerListenerStub();
		listenerStub3.setPlayerName(playerNames[2]);
		controllerStub = new ServerControllerStub(gameStub);
		rv1 = new RemoteView(listenerStub1);
		rv2 = new RemoteView(listenerStub2);
		rv3 = new RemoteView(listenerStub3);
		rv1.addObserver(controllerStub);
		rv2.addObserver(controllerStub);
		rv3.addObserver(controllerStub);
		gameStub.addObserver(rv1);
		gameStub.addObserver(rv2);
		gameStub.addObserver(rv3);
	}

	@Test
	public void updateOrder() {
		listenerStub1.setGamePhase(NetworkPhase.LOBBY);
		rv1.updateOrder(gameStub,playerNames);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetLobbyPreparation);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.LOBBY_TURN);
		NetLobbyPreparation lobbyMsg = (NetLobbyPreparation) listenerStub1.pickMessageReceived();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			assertEquals(lobbyMsg.player,gameStub.getPlayers().get(i).getPlayerName());
			assertEquals(lobbyMsg.order,i+1);
			lobbyMsg = lobbyMsg.next;
		}
	}

	@Test
	public void updateColors() {
		HashMap<String, Color> matches = new HashMap<>();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			matches.put(gameStub.getPlayers().get(i).getPlayerName(),gameStub.getPlayers().get(i).getWorker1().color);
		}
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateColors(gameStub,matches);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.COLOR_CHOICES);
		NetColorPreparation message = (NetColorPreparation) listenerStub1.pickMessageReceived();
		while (message != null) {
			boolean playerFound = false;
			for (int i = 0; i < gameStub.getPlayers().size(); i++) {
				if (message.player.equals(gameStub.getPlayers().get(gameStub.getPlayers().size() - 1 - i).getPlayerName()) && message.color.equals(gameStub.getPlayers().get(gameStub.getPlayers().size() - 1 - i).getWorker1().color)) {
					playerFound = true;
				}
			}
			assertTrue(playerFound);
			message = message.next;
		}
	}

	@Test
	public void updateGodsChallenger() {
		List<GodCard> gods = gameStub.getPlayers().stream().map(Player::getCard).collect(Collectors.toList());

		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,gods);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GODS_GODS);
	}

	@Test
	public void updateGodsChoice() {
		HashMap<String, GodCard> matches = new HashMap<>();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			matches.put(gameStub.getPlayers().get(i).getPlayerName(),gameStub.getPlayers().get(i).getCard());
		}
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,matches);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GODS_CHOICES);
		NetDivinityChoice message = (NetDivinityChoice) listenerStub1.pickMessageReceived();
		while (message != null) {
			boolean coupleFound = false;
			for (int i = 0; i < gameStub.getPlayers().size(); i++) {
				if (message.player.equals(gameStub.getPlayers().get(i).getPlayerName()) && message.divinity.equals(gameStub.getPlayers().get(i).getCard().getName().toUpperCase())) {
					coupleFound = true;
				}
			}
			assertTrue(coupleFound);
			message = message.next;
		}
	}

	@Test
	public void updateGodsStarter() {
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,"Desmond Miles");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GODS_STARTER);
	}

	@Test
	public void updatePositions() {
		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updatePositions(gameStub,new Map(),false);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGameSetup);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateMove() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateMove(gameStub,new Map());

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateBuild() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateBuild(gameStub,new Map());

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateDefeat() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateDefeat(gameStub,"Al Mualim");
		rv2.updateDefeat(gameStub,"Al Mualim");
		rv3.updateDefeat(gameStub,"Al Mualim");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertFalse(listenerStub3.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub3.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertTrue(listenerStub2.pickMessageReceived() instanceof NetGaming);
		assertTrue(listenerStub3.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_DEFEATED);
		assertEquals(listenerStub2.pickMessageReceived().message, Constants.GENERAL_DEFEATED);
		assertEquals(listenerStub3.pickMessageReceived().message, Constants.GENERAL_DEFEATED);

		assertEquals(listenerStub3.getGamePhase(),NetworkPhase.OBSERVER);
	}

	@Test
	public void updateWinner() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateWinner(gameStub,"Alexios");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_WINNER);
	}

	@Test
	public void updateQuit() {
		rv1.updateQuit(gameStub,"Al Mualim");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_PLAYER_DISCONNECTED);
	}

	@Test
	public void updatePhaseChange() {
		// tests for color phase that the update is sent correctly
		listenerStub1.setGamePhase(NetworkPhase.LOBBY);
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetLobbyPreparation);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.COLORS);
		assertEquals(((NetLobbyPreparation)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetColorPreparation)listenerStub1.extractMessageReceived()).message,Constants.COLOR_YOU);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();

		// correctly sent the message to the challenger
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetColorPreparation)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetDivinityChoice)listenerStub1.extractMessageReceived()).message,Constants.GODS_CHALLENGER);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();

		// correctly sent the messages to other players
		listenerStub2.setGamePhase(NetworkPhase.COLORS);
		rv2.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub2.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetColorPreparation)listenerStub2.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetDivinityChoice)listenerStub2.extractMessageReceived()).message,Constants.GODS_OTHER);
		assertEquals(listenerStub2.getRemainingMessages(),0);
		listenerStub2.resetCalls();

		// correctly changed the turn for the challenger from challenger choice to god choice
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetDivinityChoice)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();

		// correctly changed the turn for the players from challenger choice to god choice
		listenerStub2.setGamePhase(NetworkPhase.GODS);
		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		rv2.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub2.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetDivinityChoice)listenerStub2.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(listenerStub2.getRemainingMessages(),0);
		listenerStub2.resetCalls();

		// correctly changed the turn for the challenger from god choice to starter choice
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		gameStub.setPhase(GodsPhase.STARTER_CHOICE);
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetDivinityChoice)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetDivinityChoice)listenerStub1.extractMessageReceived()).message,Constants.GODS_CHOOSE_STARTER);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();

		// correctly changed the turn for the players from god choice to starter choice
		listenerStub2.setGamePhase(NetworkPhase.GODS);
		gameStub.setPhase(GodsPhase.STARTER_CHOICE);
		rv2.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub2.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.GODS);
		assertEquals(((NetDivinityChoice)listenerStub2.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(listenerStub2.getRemainingMessages(),0);
		listenerStub2.resetCalls();

		// correctly changed the turn for all the players from starter choice to setup worker positions
		listenerStub2.setGamePhase(NetworkPhase.GODS);
		gameStub.setPhase(Phase.SETUP);
		rv2.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub2.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub2.getGamePhase(),NetworkPhase.SETUP);
		assertEquals(((NetDivinityChoice)listenerStub2.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(listenerStub2.getRemainingMessages(),0);
		listenerStub2.resetCalls();

		// correctly changed phase from positioning workers to game phase for all players
		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		gameStub.setPhase(Phase.PLAYERTURN);
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGameSetup);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.OTHERTURN);
		assertEquals(((NetGameSetup)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();

		// phase calls for a player without prometheus
		// correctly gone to before phase without prometheus
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.BEFOREMOVE);
		gameStub.setActivePlayer("Desmond Miles");
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertFalse(listenerStub1.isSendMessageCalled());
		assertTrue(controllerStub.isUpdatePassCalled());
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// correctly gone to move phase without prometheus
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.MOVE);
		gameStub.setActivePlayer("Desmond Miles");
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.PLAYERTURN);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.PLAYER_MOVE);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// correctly gone to build phase without prometheus
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.BUILD);
		gameStub.setActivePlayer("Desmond Miles");
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.PLAYERTURN);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.PLAYER_BUILD);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// player's turn calls for a player that has prometheus
		// correctly gone to before phase with prometheus
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.BEFOREMOVE);
		gameStub.setActivePlayer("Desmond Miles");
		gameStub.changeAGod("Desmond Miles","PROMETHEUS");
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertFalse(controllerStub.isUpdatePassCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.PLAYERTURN);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.PLAYER_ACTIONS);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// correctly gone to move phase with prometheus (if he hasn't built before moving is important not to send available positions for the phase change)
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.MOVE);
		// having build before the update phase is called
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertFalse(listenerStub1.isSendMessageCalled());
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// correctly gone to move phase with prometheus (if he has built before moving is important to send available positions for the phase change)
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.MOVE);
		// it has built before
		rv1.handleBuildRequest(new NetGaming(Constants.PLAYER_IN_BUILD),false);
		// having build before the update phase is called
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertTrue(controllerStub.isUpdateBuildCalled());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.PLAYERTURN);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.PLAYER_MOVE);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();

		// correctly gone to build phase with prometheus (no difference from a player without prometheus)
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		gameStub.setPhase(GamePhase.BUILD);
		gameStub.setActivePlayer("Desmond Miles");
		rv1.updatePhaseChange(gameStub,gameStub.getPhase());
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getGamePhase(),NetworkPhase.PLAYERTURN);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.GENERAL_PHASE_UPDATE);
		assertEquals(((NetGaming)listenerStub1.extractMessageReceived()).message,Constants.PLAYER_BUILD);
		assertEquals(listenerStub1.getRemainingMessages(),0);
		listenerStub1.resetCalls();
		controllerStub.resetCalls();
	}

	@Test
	public void updateActivePlayer() {
		// tests for each phase that when its the turn of the player it sends the correct message
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateActivePlayer(gameStub,"Desmond Miles");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(((NetColorPreparation)listenerStub1.pickMessageReceived()).message,Constants.COLOR_YOU);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateActivePlayer(gameStub,"Desmond Miles");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(((NetDivinityChoice)listenerStub1.pickMessageReceived()).message,Constants.GODS_YOU);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updateActivePlayer(gameStub,"Desmond Miles");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGameSetup);
		assertEquals(((NetGameSetup)listenerStub1.pickMessageReceived()).message,Constants.GAMESETUP_PLACE);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.OTHERTURN);
		rv1.updateActivePlayer(gameStub,"Desmond Miles");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(((NetGaming)listenerStub1.pickMessageReceived()).message,Constants.OTHERS_TURN);
		listenerStub1.resetCalls();

		// tests for each phase when it's not the player turn that the message is correct
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateActivePlayer(gameStub,"Alexios");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(((NetColorPreparation)listenerStub1.pickMessageReceived()).message,Constants.OTHERS_TURN);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateActivePlayer(gameStub,"Alexios");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(((NetDivinityChoice)listenerStub1.pickMessageReceived()).message,Constants.OTHERS_TURN);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updateActivePlayer(gameStub,"Alexios");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGameSetup);
		assertEquals(((NetGameSetup)listenerStub1.pickMessageReceived()).message,Constants.OTHERS_TURN);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateActivePlayer(gameStub,"Alexios");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(((NetGaming)listenerStub1.pickMessageReceived()).message,Constants.PLAYER_FINISHED_TURN);
		listenerStub1.resetCalls();

		listenerStub1.setGamePhase(NetworkPhase.OTHERTURN);
		rv1.updateActivePlayer(gameStub,"Alexios");
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGaming);
		assertEquals(((NetGaming)listenerStub1.pickMessageReceived()).message,Constants.OTHERS_TURN);
		listenerStub1.resetCalls();
	}

	@Test
	public void updateGameFinished() {
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);

		setupTest();
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);

		setupTest();
		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.pickMessageReceived() instanceof NetGameSetup);
		assertEquals(listenerStub1.pickMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);
	}*/
}
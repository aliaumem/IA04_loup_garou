package utc.wolf.model.agent;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import utc.wolf.gui.MTWolfScene;
import utc.wolf.main.StartWolf;
import utc.wolf.model.Constants;
import utc.wolf.model.Player;
import utc.wolf.model.agent.action.SelectOptionAction;
import utc.wolf.model.menu.DefaultMenuModel;
import utc.wolf.model.menu.IMenuModel;
import utc.wolf.model.menu.MenuItem;
import jade.core.behaviours.OneShotBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class WolfGuiAgent extends GuiAgent {
	MTWolfScene scene;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	int state = Constants.TO_DAY;
	ArrayList<Player> players = new ArrayList<Player>();
    Player selectedPlayer;
	@Override
	protected void setup() {
		super.setup();
		scene = (MTWolfScene) getArguments()[0];
		pcs.addPropertyChangeListener(scene);
		StartWolf.getInstance().setGuiagent(this);
		defaulParticipants();
		addBehaviour(new LaunchOptionBehaviour());
		System.out.println(getLocalName() + "--> Installed");
	}

	@Override
	protected void onGuiEvent(GuiEvent evt) {
		if (evt.getType() == Constants.TO_DAY) {
			state = Constants.TO_DAY;
			scene.getMTApplication().registerPreDrawAction(
					new SelectOptionAction(pcs, Constants.IMAGE,
							Constants.DAYLIGHT_IMAGE));
		}
		if (evt.getType() == Constants.TO_NIGHT) {
			state = Constants.TO_DAY;
			scene.getMTApplication().registerPreDrawAction(
					new SelectOptionAction(pcs, Constants.IMAGE,
							Constants.NIGHT_IMAGE));
		}
		if (evt.getType() == Constants.PLAYER_SELECTED) {
			Object value = evt.getParameter(0);
			if (value instanceof Player) {
				selectedPlayer = (Player) value;
				System.out.println(selectedPlayer);
			}
			else {
				System.out.println(getLocalName() + " unknown element");
			}
		}
	}
private void defaulParticipants() {
	String[] participants = new String[]{"Audrey","Matthieu","Aliaume", "Yiyan"};
	for(String s : participants) {
		players.add(new Player(s));
	}
}
	private IMenuModel createListModel() {
		DefaultMenuModel listModel = new DefaultMenuModel();
		int i = 0;
		for (Player p : players) {
			MenuItem item = new MenuItem(i, p);
			listModel.add(item);
			i++;
		}
		return listModel;
	}

	private class LaunchOptionBehaviour extends OneShotBehaviour {
		/**
		 * Ouvre le menu options
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			scene.getMTApplication().registerPreDrawAction(
					new SelectOptionAction(pcs, Constants.OPEN_OPTION,
							createListModel()));
		}

	}
}

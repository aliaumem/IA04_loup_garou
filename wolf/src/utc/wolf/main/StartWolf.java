package utc.wolf.main;

import org.mt4j.MTApplication;

import utc.wolf.gui.MTWolfScene;
import utc.wolf.model.agent.WolfGuiAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class StartWolf extends MTApplication {
	private static final long serialVersionUID = 1L;
	private static StartWolf startWolf;

	public static String SECOND_PROPERTIES_FILE = "rsc/config/second.properties";
	WolfGuiAgent guiagent = null;

	public static void main(String[] args) {
		initialize();
	}

	@Override
	public void startUp() {
		StartWolf.startWolf = this;
		MTWolfScene scene = new MTWolfScene(this, "Loup Garou");
		launchAgentContainer(scene);
		addScene(scene);
		
	}

	public static StartWolf getInstance() {
		return startWolf;
	}
   public void tellAgent(int n) {
	   tellAgent(n, null);
   }
   public void tellAgent(int n,Object value) {
	   GuiEvent evt = new GuiEvent(null,n);
	   evt.addParameter(value);
	   guiagent.postGuiEvent(evt);
   }
	public void setGuiagent(WolfGuiAgent guiagent) {
	this.guiagent = guiagent;
}

	private void launchAgentContainer(MTWolfScene scene) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		ContainerController cc;
		try {
			/**
			 * host : null value means use the default (i.e. localhost) port -
			 * is the port number. A negative value should be used for using the
			 * default port number. platformID - is the symbolic name of the
			 * platform, isMain
			 */

			p = new ProfileImpl(SECOND_PROPERTIES_FILE);

			cc = rt.createAgentContainer(p);

			AgentController ac = cc
					.createNewAgent("gui-agent",
							"utc.wolf.model.agent.WolfGuiAgent",
							new Object[] { scene });
			ac.start();

		} catch (Exception ex) {
			System.out.println("Main container not started");
		}
	}
}

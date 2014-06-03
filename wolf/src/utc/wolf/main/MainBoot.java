package utc.wolf.main;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import utc.wolf.model.MessageFactory;

public class MainBoot {
	public static String MAIN_PROPERTIES_FILE = "rsc/config/main.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//boot_gui();

        /*Test Singleton*/

        MessageFactory Facto1 = MessageFactory.getInstance();
        MessageFactory Facto2 = MessageFactory.getInstance();
        System.out.println(Facto1.hashCode() +"== "+Facto2.hashCode());



        /*Test Factory*/

	}
    /*
	public static void boot_gui() {
		// open main console gui
		// properties: main=true; gui = true;
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			rt.createMainContainer(p);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	} */
}

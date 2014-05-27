package utc.wolf.model.agent.action;

import java.beans.PropertyChangeSupport;

import org.mt4j.sceneManagement.IPreDrawAction;
import utc.wolf.model.Constants;

public class SelectOptionAction implements IPreDrawAction {
	PropertyChangeSupport pcs;
	Object value;
	String propertyName;

	public SelectOptionAction(PropertyChangeSupport pcs, String propertyName,Object value) {
		super();
		this.pcs = pcs;
		this.value = value;
		this.propertyName = propertyName;
	}

	@Override
	public void processAction() {
		pcs.firePropertyChange(propertyName, null,value);
	}

	@Override
	public boolean isLoop() {
		return false;
	}

}

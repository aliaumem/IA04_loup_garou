package utc.wolf.gui.menu;

import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import utc.wolf.main.StartWolf;
import utc.wolf.model.Constants;
import utc.wolf.model.menu.DefaultMenuModel;
import utc.wolf.model.menu.IMenuModel;
import utc.wolf.model.menu.MenuItem;
import utc.wolf.util.PositionSequencer;
import utc.wolf.util.PositionSequencer.Orientation;
import utc.wolf.gui.menu.ListMenu.ChoiceListener;

public class OptionChooser extends ListMenu implements ChoiceListener {
	private MTImageButton parentButton;
	private MTImageButton nightButton;
	private MTImageButton dayButton;

	public OptionChooser(PApplet applet) {
		this(applet, 0, 0, 300, Constants.NB_PLAYERS, new DefaultMenuModel());
	}

	public OptionChooser(PApplet applet, int x, int y, int width, int nbItem,
			IMenuModel model) {
		super(applet, x, y, width, nbItem, model);
		addChoiceListener(this);
		setCloseVisible(true);
		PositionSequencer position = new PositionSequencer(new Vector3D(x
				+ getSpacing(), y + getSpacing()), getSpacing(),
				Orientation.HORIZONTAL);
		ButtonListener listener = new ButtonListener();
		parentButton = createIconButton(position.getPosition(),
				"parent-icon.png", listener);
		position.nextPosition(parentButton);
		Vector3D savedPosition = position.getPosition();
		nightButton = createIconButton(savedPosition, "night-icon.png",
				listener);
		nightButton.setWidthXYGlobal(70);
		nightButton.setHeightXYGlobal(30);
		position.nextPosition(nightButton);
		dayButton = createIconButton(savedPosition, "day-icon.png", listener);
		addChild(parentButton);
		addChild(nightButton);
		addChild(dayButton);
		addChoiceListener(new ListMenuChoiceListener(this));
	}

	@Override
	public void choiceSelected(ChoiceEvent choiceEvent) {
		Object choice = choiceEvent.getChoosedObject();
		Object p = null; 
		if (choice instanceof MenuItem) {
			p = ((MenuItem) choice).getValue();
		}
		StartWolf.getInstance().tellAgent(Constants.PLAYER_SELECTED,p);
	}

	@Override
	public void choiceCancelled(ChoiceEvent choiceEvent) {
	}

	protected class ButtonListener implements IGestureEventListener {
		public boolean processGestureEvent(MTGestureEvent ge) {
			if (ge instanceof TapEvent) {
				TapEvent tapEvent = (TapEvent) ge;
				if (tapEvent.isTapped()) {
					if (tapEvent.getTarget() == nightButton) {
						setNight();
					} else if (tapEvent.getTarget() == dayButton) {
						setDaylight();
					}
				}
			}

			return false;
		}
	}

	private void setDaylight() {
		StartWolf.getInstance().tellAgent(Constants.TO_DAY);
	}

	private void setNight() {
		StartWolf.getInstance().tellAgent(Constants.TO_NIGHT);
	}
}

package utc.wolf.gui.menu;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTList;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import utc.wolf.gui.Theme;
import utc.wolf.gui.Theme.StyleID;
import utc.wolf.model.menu.DefaultMenuModel;
import utc.wolf.model.menu.IMenuModel;
import utc.wolf.util.ImageManager;
import utc.wolf.util.PropertyManager;

public class ListMenu extends MTRectangle implements IGestureEventListener {
	private static final String CHOICE = "CHOICE";
	private static int spacing = 5;
	public static int choiceViewHeight = 40;
	public static int iconWidth = 40;
	public static int iconHeight = 40;

	public static int getSpacing() {
		return spacing;
	}

	public static int getSpacingX2() {
		return spacing * 2;
	}

	public static void setSpacing(int spacing) {
		ListMenu.spacing = spacing;
	}

	public static int getSpacedIconWidth() {
		return iconWidth + getSpacingX2();
	}

	public static int getSpacedIconHeight() {
		return iconHeight + getSpacingX2();
	}

	public static float calcHeight(int visibleChoiceCount) {
		return (float) ((visibleChoiceCount * choiceViewHeight) + (getSpacedIconHeight() * 2));
	}

	/**
	 * @uml.property name="menuModel"
	 * @uml.associationEnd
	 */
	private IMenuModel menuModel;
	/**
	 * @uml.property name="mustBeDestroy"
	 */
	private boolean mustBeDestroy;
	/**
	 * @uml.property name="list"
	 * @uml.associationEnd
	 */
	private MTList list;
	/**
	 * @uml.property name="closeButton"
	 * @uml.associationEnd
	 */
	private MTImageButton closeButton;
	/**
	 * @uml.property name="backButton"
	 * @uml.associationEnd
	 */
	private MTColor defaultFillColor = new MTColor(140, 210, 210, 240);
	MTListCell currentCell = null;
	private MTImageButton backButton;
	/**
	 * @uml.property name="listeners"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="utc.tatinpic.gui.widgets.menu.ListMenu$ChoiceListener"
	 */
	private HashSet<ChoiceListener> listeners;

	public ListMenu(PApplet applet, int x, int y, int width, int nbItem,
			IMenuModel model) {
		super(applet, x, y, width, calcHeight(nbItem));
		float height = calcHeight(nbItem);
		// Theme.getTheme().applyStyle(StyleID.DEFAULT, this);
		Theme.getTheme().applyStyle(StyleID.STROKE_ONLY, this);
		this.setAnchor(PositionAnchor.UPPER_LEFT);

		this.menuModel = model;
		this.mustBeDestroy = true;

		Vector3D closeButtonPosition = new Vector3D(x + width
				- (iconWidth + getSpacing()), y + getSpacing());
		this.closeButton = createIconButton(closeButtonPosition, "close.png",
				new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						if (ge instanceof TapEvent) {
							TapEvent tapEvent = (TapEvent) ge;

							if (tapEvent.isTapped()
									&& tapEvent.getTarget() == closeButton) {
								ChoiceEvent choiceEvent = new ChoiceEvent(
										ListMenu.this, null);
								for (ChoiceListener listener : ListMenu.this.listeners)
									listener.choiceCancelled(choiceEvent);
								// if (ListMenu.this.mustBeDestroy())
								ListMenu.this.destroy();
							}
						}

						return false;
					}
				});
		this.closeButton.setNoStroke(true);
		addChild(this.closeButton);

		this.backButton = createIconButton(closeButtonPosition,
				"parent-icon.png", new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						if (ge instanceof TapEvent) {
							TapEvent tapEvent = (TapEvent) ge;

							if (tapEvent.isTapped()
									&& tapEvent.getTarget() == backButton) {
								Object parent = menuModel
										.getParentMenu(menuModel
												.getCurrentMenu());
								if (parent != null) {
									menuModel.setCurrentMenu(parent);
									updateList();
								}
							}
						}

						return false;
					}
				});
		addChild(this.backButton);

		this.backButton.setVisible(false);

		this.list = new MTList(applet, x + getSpacing(), y
				+ getSpacedIconHeight(), width - getSpacingX2(), height
				- getSpacedIconHeight() * 2, 0);
		Theme.getTheme().applyStyle(StyleID.STROKE_ONLY, list);
		updateList();
		addChild(this.list);

		this.listeners = new HashSet<ListMenu.ChoiceListener>();
	}

	public ListMenu(PApplet applet, int x, int y, int width, int nbItem,
			Object... choices) {
		this(applet, x, y, width, nbItem, new DefaultMenuModel(null, choices));
	}

	public ListMenu(PApplet applet, int x, int y, int width, int nbItem,
			Collection<Object> choices) {
		this(applet, x, y, width, nbItem, choices.toArray());
	}

	protected MTImageButton createIconButton(Vector3D position,
			String imageFilename, IGestureEventListener listener) {
		MTImageButton imageButton = new MTImageButton(this.getRenderer(),
				ImageManager.getInstance().load(imageFilename));
		imageButton.setAnchor(PositionAnchor.UPPER_LEFT);
		imageButton.setWidthXYGlobal(iconWidth);
		imageButton.setHeightXYGlobal(choiceViewHeight);
		imageButton.setPositionGlobal(new Vector3D(position.x, position.y));
		Theme.getTheme()
				.applyStyle(Theme.TRANSPARENT_IMAGE_BUTTON, imageButton);
		imageButton.addGestureListener(TapProcessor.class, listener);

		return imageButton;
	}

	protected void updateList() {
		list.removeAllListElements();

		Object[] choices = menuModel.getChoices(menuModel.getCurrentMenu());

		if (menuModel.onlyStartMenuCanCancel()) {
			if (menuModel.getCurrentMenu() == menuModel.getStartMenu()) {
				closeButton.setVisible(closeButton.isEnabled());
				backButton.setVisible(false);
			} else {
				closeButton.setVisible(false);
				backButton.setVisible(true);
			}
		}

		if (choices != null) {
			for (final Object choice : choices) {
				MTListCell cell = new MTListCell(getRenderer(),
						getWidthXYGlobal() - getSpacingX2(), choiceViewHeight);
				Theme.getTheme().applyStyle("LIST_CHOICE", cell);
				cell.setFillColor(defaultFillColor);
				cell.setUserData(CHOICE, choice);
				cell.addChild(makeChoiceView(choice));
				cell.registerInputProcessor(new TapProcessor(getRenderer()));
				cell.addGestureListener(TapProcessor.class, this);

				list.addListElement(cell);
			}
		}
	}

	protected MTComponent makeChoiceView(Object choice) {
		MTTextArea textArea = new MTTextArea(getRenderer(), getSpacing(), 0,
				getWidthXYGlobal() - getSpacingX2(), choiceViewHeight);
		String fontname = PropertyManager.getInstance().getProperty(
				PropertyManager.LIST_FONT);
		textArea.setFont(FontManager.getInstance().createFont(getRenderer(),
				fontname, 20, MTColor.BLACK, true));
		textArea.setText(choice.toString());
		textArea.setNoFill(true);
		textArea.setNoStroke(true);

		return textArea;
	}

	public MTImageButton getBackButton() {
		return backButton;
	}

	public IMenuModel getModel() {
		return menuModel;
	}

	public void setModel(IMenuModel menuModel) {
		this.menuModel = menuModel;
		this.menuModel.setCurrentMenu(menuModel.getStartMenu());
		updateList();
	}

	public void setStart() {
		menuModel.setCurrentMenu(menuModel.getStartMenu());
		updateList();
	}

	public void addChoiceListener(ChoiceListener listener) {
		this.listeners.add(listener);
	}

	public void removeChoiceListener(ChoiceListener listener) {
		this.listeners.remove(listener);
	}

	public boolean mustBeDestroy() {
		return mustBeDestroy;
	}

	/**
	 * @param mustBeDestroy
	 * @uml.property name="mustBeDestroy"
	 */
	public void setMustBeDestroy(boolean mustBeDestroy) {
		this.mustBeDestroy = mustBeDestroy;
	}

	public boolean isCloseVisible() {
		return this.closeButton.isVisible();
	}

	public void setCloseVisible(boolean visible) {
		this.closeButton.setVisible(visible);
		this.closeButton.setEnabled(visible);
	}

	public void restoreListColor() {
		for (int i = 0; i < list.getChildCount(); i++) {
			MTComponent cell = list.getChildByIndex(i);
			if (cell instanceof MTListCell) {
				((MTListCell) cell).setFillColor(defaultFillColor);
				System.out.println("----------- .");
			}
			System.out.println("----------- :");
		}

	}

	public boolean processGestureEvent(MTGestureEvent ge) {
		super.processGestureEvent(ge);

		if (ge instanceof TapEvent) {
			if (((TapEvent) ge).isTapped()) {
				MTListCell listCell = (MTListCell) ge.getTarget();
				Object choice = listCell.getUserData(CHOICE);

				if (this.menuModel.hasChoices(choice)) {
					this.menuModel.setCurrentMenu(choice);
					updateList();
				} else {
					if (currentCell != null) {
						currentCell.setFillColor(defaultFillColor);
					}
					listCell.setFillColor(new MTColor(MTColor.BLUE));
					currentCell = listCell;
					for (ChoiceListener listener : ListMenu.this.listeners)
						listener.choiceSelected(new ChoiceEvent(ListMenu.this,
								choice));
					if (ListMenu.this.mustBeDestroy())
						ListMenu.this.destroy();
				}
			}
		}

		return false;
	}

	/**
	 * @author claude
	 */
	public class ChoiceEvent {
		/**
		 * @uml.property name="listMenu"
		 * @uml.associationEnd
		 */
		private ListMenu listMenu;
		private Object choice;

		public ChoiceEvent(ListMenu menu, Object choice) {
			this.listMenu = menu;
			this.choice = choice;
		}

		/**
		 * @return
		 * @uml.property name="listMenu"
		 */
		public ListMenu getListMenu() {
			return this.listMenu;
		}

		/**
		 * @return
		 * @uml.property name="choice"
		 */
		public String getChoice() {
			if (choice == null)
				return "";
			return this.choice.toString();
		}

		public Object getChoosedObject() {
			Object ret = choice;
			if (choice instanceof DefaultMenuModel)
				ret = ((DefaultMenuModel) choice).getUserObject();
			return ret;
		}
	}

	public interface ChoiceListener {
		public void choiceSelected(ChoiceEvent choiceEvent);

		public void choiceCancelled(ChoiceEvent choiceEvent);
	}
}

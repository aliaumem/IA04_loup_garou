package utc.wolf.gui.menu;

import utc.wolf.gui.menu.ListMenu.ChoiceEvent;
import utc.wolf.gui.menu.ListMenu.ChoiceListener;

public class ListMenuChoiceListener implements ChoiceListener {
	ListMenu listmenu;

	public ListMenuChoiceListener(ListMenu listmenu) {
		super();
		this.listmenu = listmenu;
	}

	@Override
	public void choiceSelected(ChoiceEvent choiceEvent) {
		//listmenu.restoreListColor();
	}

	@Override
	public void choiceCancelled(ChoiceEvent choiceEvent) {
		// TODO Auto-generated method stub

	}

}

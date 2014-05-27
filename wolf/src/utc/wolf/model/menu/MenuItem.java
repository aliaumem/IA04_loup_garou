package utc.wolf.model.menu;
/*
 * A menu item is defined by a row in a menu and a label
 * Its row avoid to test its label, which is painful in internationalisation
 */

public class MenuItem {
  private int row;
  private Object value;
public MenuItem(int row, Object value) {
	super();
	this.row = row;
	this.value = value;
}
public int getRow() {
	return row;
}

public Object getValue() {
	return value;
}
/*
 * Its label is displayed in a menu
 */
  public String toString() {
	  return value.toString();
  }
}

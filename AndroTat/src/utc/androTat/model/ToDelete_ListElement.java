package utc.androTat.model;

public class ToDelete_ListElement {
	
		private String name = null;
		private boolean selected = false;
		
		ToDelete_ListElement(){
			this.name=null;
			this.selected=false;
		}
		ToDelete_ListElement(String s){
			this.name=s;
			this.selected=false;
		}
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isSelected() {
			return selected;
		}
		public void switchSelected() {
			this.selected = !this.selected;
		}
}

package utc.tatinpic.semantics;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

/*
 * Class used for serializing a brainstorming model without images
 */
public class BSJason {
	String id;
	String type;
	String value;
	ArrayList<String> categories;
	HashMap<String, String> categoryProperties;
	ArrayList<BSJason> children;

	public BSJason() {
		super();
		this.categoryProperties = new HashMap<String, String>() ;
	}

	public BSJason(String type, String value, ArrayList<BSJason> children, HashMap<String, String> categoryProperties) {
		super();
		this.type = type;
		this.value = value;
		this.children = children;
		this.categoryProperties = categoryProperties;
	}

	public String jason() {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sw.toString();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<BSJason> getChildren() {
		return children;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	public void addCategory(utc.tatinpic.model.Klonk.Category category) {
		  categories.add(category.toString());
	    }
	public void addCategoryProperties(String p, String v) {
		categoryProperties.put(p, v);
	}

	public HashMap<String, String> getCategoryProperties() {
		return categoryProperties;
	}

	public void setCategoryProperties(HashMap<String, String> categoryProperties) {
		this.categoryProperties = categoryProperties;
	}

	public void setChildren(ArrayList<BSJason> children) {
		this.children = children;
	}

	public String toString() {
		String s = "BSJason object => [\n";
		s += "type = " + type + "\n";
		s += "id = " + id + "\n";
		s += "value = " + value + "\n";
		s += "]";
		

		return s;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

package utc.tatinpic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import utc.tatinpic.semantics.BSJason;
import utc.tatinpic.semantics.Constants;
import utc.tatinpic.semantics.ContentOntology;


/**
 * 
 * @class Klonk
 * 
 *        Kind of Large Object with New Knowledge
 */
public class Klonk implements Serializable {
	/**
	 * @author claude
	 */
	public enum Category {
		//GROUP, 
		IDEA_NOTE, 
		TASK, 
		CAUSE, 
		EFFECT, 
		RISK, 
		PROBLEM
	}

	/**
	 * @author claude
	 */
	public enum Property {
		
		// Klonk Attributes
		IDENTIFICATION,
		AUTHOR,
		CREATION_DATE,
		TEXT, 
		IMAGES, 
		CATEGORY,
		
		// Task Attributes
		START_DATE(Category.TASK),
		END_DATE(Category.TASK),
		DURATION(Category.TASK),
		FREE_MARGIN(Category.TASK),
		TOTAL_MARGIN(Category.TASK), 
		RESOURCES(Category.TASK), 
		PARTICIPANTS(Category.TASK);

		public final Category category;

		Property() {
			this.category = null;
		}

		Property(Category category) {
			this.category = category;
		}

		static public LinkedList<Property> valuesOf(Category category) {
			LinkedList<Property> properties = new LinkedList<Property>();

			for (Property property : Property.values()) {
				if (property.category != null
						&& property.category.equals(category))
					properties.add(property);
			}

			return properties;
		}
	}

	private Identification identification;
	private ArrayList<Category> categories;
	private String text;

	public Klonk(Identification identification) {
		super();
		this.identification = identification;
		categories = new ArrayList<Category>() ;
		categoryProperties = new HashMap<Property, Object>() ;
		text = "";
	}

	public Klonk() {
		this(null);
	}

	/**
	 * Getter of the property <tt>text</tt>
	 * 
	 * @return Returns the text.
	 * @uml.property name="text"
	 */
	public String getText() {
		return text;
	}


	/**
	 * Setter of the property <tt>text</tt>
	 * 
	 * @param text
	 *            The text to set.
	 * @uml.property name="text"
	 */
	public void setText(String text) {
		String oldValue = this.text;
		this.text = text;
	}

	/**
	 * Getter of the property <tt>identification</tt>
	 * 
	 * @return Returns the idData.
	 * @uml.property name="identification"
	 */
	public Identification getIdentification() {
		return identification;
	}

	/**
	 * Setter of the property <tt>identification</tt>
	 * 
	 * @param identification
	 *            The identification to set.
	 * @uml.property name="identification"
	 */
	public void setIdentification(Identification identification) {
		Identification oldValue = this.identification;
		this.identification = identification;
	}

	/**
	 * Getter of the property <tt>category</tt>
	 * 
	 * @return Returns the category.
	 * @uml.property name="category"
	 */
	

	/**
	 * Setter of the property <tt>category</tt>
	 * 
	 * @param category
	 *            The category to set.
	 * @uml.property name="category"
	 */
	public void addCategory(Category category) {
		Category oldValue = null;
		categories.add(category);
		initPropertiesOf(category);
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	/**
	 * @uml.property name="categoryProperties"
	 */
	private HashMap<Property, Object> categoryProperties;

	/**
	 * @uml.property name="categoryProperties"
	 */
	public HashMap<Property, Object> getCategoryProperties() {
		return categoryProperties;
	}

	/**
	 * Setter of the property <tt>categoryProperties</tt>
	 * 
	 * @param categoryProperties
	 *            The categoryProperties to set.
	 * @uml.property name="categoryProperties"
	 */
	public void setCategoryProperties(
			HashMap<Property, Object> categoryProperties) {
		this.categoryProperties = categoryProperties;
	}

	public HashMap<Property, Object> getPropertiesOf(Category category) {
		HashMap<Property, Object> result = new HashMap<Property, Object>();

		LinkedList<Property> properties = Property.valuesOf(category);
		for (Property property : properties)
			result.put(property, this.categoryProperties.get(property));

		return result;
	}
	public void initPropertiesOf(Category category) {
		LinkedList<Property> properties = Property.valuesOf(category);
		for (Property property : properties)
			categoryProperties.put(property, "0");
	}
	public void setProperties(HashMap<String, String> prop) {
		for (String s : prop.keySet()) {
			categoryProperties.put(Property.valueOf(s), prop.get(s));
		}
	}
	/*
	 *  give a BSJason object of a klonk
	 *  useful for transfering values of properties
	 */
	public BSJason getJasonFormat() {
		BSJason bsj = new BSJason();
		bsj.setValue(getText());
		bsj.setId(getIdentification().getId());
		for (Category category : getCategories()) {
			bsj.addCategory(category);
		}
		for (Klonk.Property p : getCategoryProperties().keySet()) {
			bsj.addCategoryProperties(p.toString(), getCategoryProperties().get(p).toString());
		}
		bsj.setChildren(null);
		return bsj;
	}
}

/**
 * 
 */
package utc.tatinpic.model;

import jade.util.leap.Serializable;

import java.util.Calendar;

public class Identification implements Serializable 
{
	private static String DEFAULT_AUTHOR = "default-author";
	/**
	 * @uml.property  name="id"
	 */
	private String id;

	/**
	 * @param id
	 * @param author
	 * @param creationData
	 */
	public Identification(String id, String author, Calendar  creationDate)
	{
		this.id = id;
		this.author = author;
		this.creationDate = creationDate;
	}
	/**
	 * @param id
	 * @param author
	 */
	public Identification(String id, String author)
	{
		this.id = id;
		this.author = author;
		this.creationDate = Calendar.getInstance();
	}
	/**
	 * @param id
	 */
	public Identification(String id)
	{
		this.id = id;
		this.author = DEFAULT_AUTHOR;
		this.creationDate = Calendar.getInstance();
	}
	/**
	 * Getter of the property <tt>id</tt>
	 * @return Returns the id.
	 * @uml.property name="id"
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @uml.property  name="author"
	 */
	private String author;

	/**
	 * Getter of the property <tt>author</tt>
	 * @return Returns the author.
	 * @uml.property name="author"
	 */
	public String getAuthor()
	{
		return author;
	}

	/**
	 * Setter of the property <tt>author</tt>
	 * @param author The author to set.
	 * @uml.property name="author"
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}

	/**
	 * @uml.property  name="creationDate"
	 */
	private Calendar creationDate;

	/**
	 * Getter of the property <tt>creationDate</tt>
	 * @return Returns the creationDate.
	 * @uml.property name="creationDate"
	 */
	public Calendar  getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Setter of the property <tt>creationData</tt>
	 * @param creationData The creationData to set.
	 * @uml.property name="creationData"
	 */
	public void setCreationDate(Calendar  creationDate)
	{
		this.creationDate = creationDate;
	}

	/**
	 * Setter of the property <tt>id</tt>
	 * @param id The id to set.
	 * @uml.property name="id"
	 */
	public void setId(String id)
	{
		this.id = id;
	}

}

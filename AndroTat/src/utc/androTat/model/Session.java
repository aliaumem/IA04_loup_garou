package utc.androTat.model;

import java.io.Serializable;

public class Session implements Serializable {

	private static final long serialVersionUID = 4217822798531986809L;
	private long id;
	private String name ="";
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

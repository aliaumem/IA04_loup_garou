package utc.tatinpic.agent.exchange;

import java.io.Serializable;

/**
 * @author  claude
 */
public abstract class ExchangeObject implements Serializable {
  
/**
 * @uml.property  name="ext"
 * @uml.associationEnd  multiplicity="(1 1)"
 */
private ExchangeObjectCategory ext;

public ExchangeObject(ExchangeObjectCategory ext) {
	super();
	this.ext = ext;
}

  /**
 * @return
 * @uml.property  name="ext"
 */
public ExchangeObjectCategory getExt() {
	return ext;
}

/**
 * @uml.property  name="content"
 */
public abstract Object getContent();
}

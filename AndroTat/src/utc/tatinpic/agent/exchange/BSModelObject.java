package utc.tatinpic.agent.exchange;

import utc.tatinpic.model.BrainstormingModel;

public class BSModelObject extends ExchangeObject {
	BrainstormingModel bsm;
	public BSModelObject(BrainstormingModel bsm) {
		super(ExchangeObjectCategory.bs);
		this.bsm = bsm;
	}

	@Override
	public BrainstormingModel getContent() {
		return bsm;
	}

}

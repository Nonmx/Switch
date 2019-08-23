package sselab.switchy.property;

public class StmtProperty extends Property{

	public StmtProperty(String condition, int location) {
		super(condition, location);
	}

	@Override
	public String getStatement() {
		// TODO Auto-generated method stub
		return this.getCondition() + ";";
	}

}

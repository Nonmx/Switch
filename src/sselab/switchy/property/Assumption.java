package sselab.switchy.property;

public class Assumption extends Property{

	public Assumption(String condition, int location) {
		super(condition, location);
	}

	@Override
	public String getStatement() {
		return "__CPROVER_assume("+getCondition()+");";
	}

}

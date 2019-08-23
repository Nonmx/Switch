package sselab.switchy.property;

public class Assertion extends Property
{

	public Assertion(String condition, int location) {
		super(condition, location);
		// TODO Auto-generated constructor stub
	}
	
	public Assertion(String condition){
		setCondition(condition);
	}

	@Override
	public String getStatement() {
		return "assert("+getCondition()+");\n";
	}

	
}

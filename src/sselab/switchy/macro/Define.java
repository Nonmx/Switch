package sselab.switchy.macro;

public class Define implements Preprocessor{

	private String original;
	private String definition;
	
	public Define() {
		// TODO Auto-generated constructor stub
	}
	
	public Define(String original, String definition){
		this.original = original;
		this.definition = definition;
	}

	@Override
	public String getString() {
		String val = "#define " + original + " " + definition + "\n";
		return val;
	}

}

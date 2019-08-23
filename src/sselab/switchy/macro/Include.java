package sselab.switchy.macro;

public class Include implements Preprocessor {
	private String headerFileName;
	private int kind = 0;
	public static final int USER_DEFINED = 0;
	public static final int STD_LIB = 1;
	public Include() {
		// TODO Auto-generated constructor stub
	}
	
	public Include(String header){
		this.headerFileName = header;
	}
	
	public Include(String header, int kind){
		this.headerFileName = header;
		this.kind = kind;
	}

	@Override
	public String getString() {
		String val = null;
		if(this.kind == USER_DEFINED){
			val = "#include " + "\"" + headerFileName + "\"\n";
		} else if(this.kind == STD_LIB){
			val = "#include " + "<" + headerFileName + ">\n";
		} else{
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return val;
	}

}

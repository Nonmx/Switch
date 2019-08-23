/**
 * 
 */
package sselab.switchy.property;

/**
 * @author SSELAB-129
 *
 */
public abstract class Property {
	public static final int PRE = 1;
	public static final int POST = 2;
	
	private String condition;
	private int propertyKind;
	
	
	public Property(){
	}
	public Property(String condition, int kind){
		this.condition = condition;
		this.propertyKind = kind;
	}
	
	public void setCondition(String condition){
		this.condition = condition;
	}
	
	public String getCondition(){
		return this.condition;
	}
	
	public int getLocation(){
		return this.propertyKind;
	}
	
	public abstract String getStatement();

}

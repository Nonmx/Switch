package sselab.switchy.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Config {
	private int taskid;
	private String arrayName;
	private HashMap<String, String> attributes;
	
	public Config(int taskid, String arrayName){
		this.taskid = taskid;
		this.arrayName = arrayName;
		this.attributes = new HashMap<String, String>();
	}
	
	public String getValue(String name){
		return attributes.get(name);
	}
	/**
	 * add attribute to hashmap. 
	 * - nondeterministic assignment.
	 * @param statAttr
	 */
	public void addAttribute(String statAttr){
		attributes.put(statAttr, "nondet_uchar()");
	}
	/**
	 * add attribute to hashmap
	 * @param name
	 * @param val
	 */
	public void addAttribute(String name, String val){
		if(val.equals("AUTO")){
			attributes.put(name, "nondet_uchar()");
		} else{
			attributes.put(name, val);
		}
	}
	
	public String getConfigAssingments(){
		Iterator<String> keys = attributes.keySet().iterator();
		String strVal = "";
		while(keys.hasNext()){
			String key = keys.next();
			strVal += "\t"+this.arrayName+"["+taskid+"]"+
			"."+key+" = "+attributes.get(key)+";\n";
		}
		return strVal;
	}
}

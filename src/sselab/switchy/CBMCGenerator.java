package sselab.switchy;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;

import sselab.ant.AnalyzedResult;
import sselab.ant.CodeAnalyzer;
import sselab.ant.CodeAnt;
import sselab.cfg.node.BranchNode;
import sselab.cfg.node.NormalNode;
import sselab.cfg.node.specifiednode.HeadNode;
import sselab.cfg.node.specifiednode.IfNode;
import sselab.cfg.node.specifiednode.LoopNode;
import sselab.cfg.parser.CFGConstructor;
import sselab.cfg.parser.CFGNode;
import sselab.cfg.parser.FunctionCFG;
import sselab.code.Function;
import sselab.code.header.Header;
import sselab.code.printer.CodePrinter;
import sselab.code.printer.CodePrinterForSlicedCode;
import sselab.code.variable.Variable;
import sselab.graph.Node;
import sselab.graph.visitor.LineOrderPrinter;
import sselab.graph.visitor.LineOrderVisitor;
import sselab.nusek.csl.Csl;
import sselab.nusek.csl.CslParser;
import sselab.switchy.simplenode.ProgramCounter;
import sselab.switchy.simplenode.SimpleNode;
import sselab.switchy.simplenode.SimplePseudoNode;
import sselab.switchy.simplenode.Task;
import sselab.switchy.simplenode.factory.ConcreteSimpleNodeFactory;
import sselab.switchy.simplenode.visitor.SimpleNodeAnnotator;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

public class CBMCGenerator {
	
	String inFileName;
	List<Task> tasks;
	List<Csl> constraints;
	
	
	public String GetInFileName() {
		return inFileName;
	}
	public void setInFileName(String inFileName)
	{
		this.inFileName = inFileName;
	}
	
	String tempFileName;
	public String getTempFileName()
	{
		return tempFileName;
	}
	
	public CBMCGenerator()
	{
		tasks =new ArrayList<Task>();
	};
	
	public CBMCGenerator(String inFileName)
	{
		this.inFileName = inFileName;
		this.tempFileName = "temp_"+inFileName;
		tasks = new ArrayList<Task>();
	}
	
	public final static void main(String[] args)
	{
		if(args.length != 0)
		{
			CBMCGenerator CBMC  = new CBMCGenerator(args[0]);
			CBMC.preprocessInFile();
			if(args.length == 2)
			{
				CBMC.constraints = CBMC.parseCSL(args[1]);
			}
			FunctionCFG[] functions = CBMC.getCodeAntFunctionCFG();
			CBMC.printHeader();
			CBMC.printCode(functions);
			CBMC.printApp();
			
		}
		else
		{
			System.err.println("Wrong usage.\n How to use : java -cp Switchy_new.jar sselab.switchy.CBMCGenerator <filename>\n");
		}
	}
	
	public void preprocessInFile()
	{
		CBMCSup proproc = new CBMCSup(inFileName);
		proproc.preprocess();
	}
	
	private boolean contains(List<SimpleNode>list,CFGNode n)
	{
		for(SimpleNode l : list)
		{
			if(l.node.equals(n))
				return true;
		}
		return false;
	}
	
	private int indexOf(List<SimpleNode>list,CFGNode n)
	{
		for(SimpleNode l:list)
		{
			if(l.node.equals(n))
				return list.indexOf(l);
		}
		return -1;
	}
	
	/*
	 * DFS printing of CFG(CodeAnt CFG)
	 * @param node
	 */
	
	@Deprecated
	private void printCFG(CFGNode node)
	{
		if(node == null)
			return;
		else
		{
			if(node instanceof NormalNode || node instanceof BranchNode)
				System.out.println(node.getStatement().getCode());
			if(node.getNextNode()!= null && node.getNextNodes().length > 0)
			{
				for(CFGNode n: node.getNextNodes())
					printCFG(n);
			}
		}
	}
	
	public SimpleNode[] getModifiableFunctionCFG()
	{
		SimpleNode [] functionNodes = null;
		FunctionCFG[] functions = getCodeAntFunctionCFG();
		if(functions.length > 0)
		{
			functionNodes = new SimpleNode[functions.length];
		}
		int idx = 0;
		for(FunctionCFG c : functions)
		{
			System.out.println(c.getFunctionNameWithParameter());
			idx++;
		}
		
		return functionNodes;
	}
	
	public FunctionCFG[] getCodeAntFunctionCFG()
	{
		CodeAnt codeAnt = new CodeAnt();
		String procFileName = this.inFileName.replaceAll(".c", "_cbmc.c");
		codeAnt.analyze(procFileName, CodeAnt.ANALYZE_WITHOUTSLICE);
		return codeAnt.getAnalyzeResult().getFunctions();
	}
	
	public void printHeader()
	{
		CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
		String procFileName  = this.inFileName.replace(".c", "_cbmc.c");
		codeAnalyzer.makeCFGFromFile(procFileName);
		CodePrinterForSlicedCode printer = new CodePrinterForSlicedCode(procFileName);
		codeAnalyzer.printCFGInFile(printer,true);
	}
	
	public void printApp() {
		String procFileName = this.inFileName.replace(".c", "_cbmc.c");
		
		String str = "void main(){\n";
		str += "\tStartOS(1);\n";
		str += "}\n\n";
		
		str += "int alarm_checker(){\n";
		str += "\tif(alarm_state[testalarm] == SET)\n";
		str += "\t\treturn 1;\n";
		str += "\telse\n";
		str += "\t\treturn 0;\n";
		str += "}\n";
		
		str += "void alarm(){\n";
		str += "\tint nondet;\n";
		str += "\tnondet = nondet_int();\n";
		str += "\t__CPROVER_assume(nondet >=50);\n";
		str += "\tassert(nondet>=50);\n";
		str += "\tif(nondet>=50){\n";
		str += "\t\tEE_pit_handler();\n";
		str += "\t}\n";
		str += "}\n";
		
		str += "void app(){\n";
		str += "\tint checker;\n";
		str += "\tchecker = nondet_int();\n";
		str += "\t__CPROVER_assume(checker >= 1 && checker <= 2);\n";
		str += "\tassert(checker >= 1 && checker <= 2);\n";
		str += "\twhile(current_tid >= 0){\n";
		str += "\tif(checker == 1){\n";
		for(Task t : tasks)
		{
			str += "\t\tif(current_tid == "+t.getTaskID()+")\n";
			str += "\t\t\tTASK("+t.getTaskID()+");\n";
		}
		str += "\t}\n";
		str += "\tif(checker == 2){\n";
		str += "\t\t\tif(alarm_checker()==1)\n";
		str += "\t\t\talarm();\n";
		str += "\t\t}\n\t}\n}";
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(procFileName,true));
			bw.write(str);
			bw.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Print annotated code from the CFG constructed by CodeAnt.
	 * @param functions
	 */
	public void printCode(FunctionCFG[] functions){
		String procFileName = this.inFileName.replace(".c", "_cbmc.c");
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = 
					new BufferedWriter(new FileWriter(procFileName, true));
			bufferedWriter.write("int flag;\nint task0_counter;");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(FunctionCFG f : functions){
			//If the task is not a task, don't annotate it.
			if(!f.getFunctionName().startsWith("TASK")){
				Task task = new Task();
				task.setTaskID("main");
				task.generateCFG(f);
				//get control flow graph.
				SimpleNode functionHead = task.getCFG();
				SimpleNodeVisitor visitor = new SimpleNodePrinter(bufferedWriter);
				Set<SimpleNode> visited = new HashSet<SimpleNode>();
				functionHead.accept(visitor, visited);
			}else{
				//Generate task object.
				Task task = new Task();
				task.generateCFG(f);
				//get control flow graph.
				SimpleNode functionHead = task.getCFG();
				//annotate first.
				SimpleNodeVisitor visitor = new SimpleNodeAnnotator();
				if(this.constraints != null){
					((SimpleNodeAnnotator)visitor).setCslList(this.constraints);
				}
				Set<SimpleNode> visited = new HashSet<SimpleNode>();
				functionHead.accept(visitor, visited);
				task.setSwitchingPointNum(ProgramCounter.maxPCVal);
				//generate jump macro
				try {
					bufferedWriter.write(task.getJumpMacro());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ProgramCounter.maxPCVal = 0;
				visitor = new SimpleNodePrinter(bufferedWriter);
				visited = new HashSet<SimpleNode>();
				functionHead.accept(visitor, visited);
				this.tasks.add(task);
			}
			System.out.println();
		}
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Csl> parseCSL(String fileName){
		List<Csl> cslList = null;
		try {
			CslParser parser = new CslParser(fileName);
			cslList = parser.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cslList;
	}
	

}

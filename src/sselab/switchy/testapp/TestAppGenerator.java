package sselab.switchy.testapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * main class of test app code generator
 * @author SSELAB-129
 *
 */
public class TestAppGenerator {

	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println("Wrong usage. Add options.(-help)");
		} else if(args[0].equals("-help")){
			System.out.println("Usage: Program -option");
			System.out.println("Options: ");
			System.out.println("-unit <XML file name>");
		} else if(args.length == 2){
			if(args[0].equals("-unit")){
				PropertyFileProcessor generator =
					new PropertyFileProcessor(args[1]) ;
				generator.generateAppFiles();
			}else if(args[0].equals("-random")){
				Scanner sc = new Scanner(System.in);
				System.out.print("Number of Tasks: ");
				int taskNum = sc.nextInt();
				System.out.print("Number of API calls per each task: ");
				int length = sc.nextInt();
				RandomCallAppGenerator generator = new RandomCallAppGenerator(taskNum, length);
				generator.generateCode();
				System.out.print(generator.getCode());
			}
		}

	}

}

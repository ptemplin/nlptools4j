package main;
import java.io.IOException;
import java.util.Scanner;


public class Application {

	public static void main(String[] args) {
		Parser parser;
		try {
			parser = new Parser();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		while(!"quit".equals(input)) {
			parser.processInput(input);
			input = scanner.nextLine();
		}
	}
	
}

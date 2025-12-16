/**
 * 
 */
package emailRecognizerTest;

import java.util.Scanner;

/**
 * 
 */
public class EmailRecognizerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner keyboard = new Scanner(System.in);
		
		System.out.print("Enter email: ");
		
		String email = keyboard.nextLine();
		
		String error = EmailRecognizer.checkForValidEmail(email);
		
		if(error == "") {
			System.out.println("Email is valid! Congratulations!");
		} else {System.out.println("\nError: "+error+"\n");}
		
		keyboard.close();
	}

}

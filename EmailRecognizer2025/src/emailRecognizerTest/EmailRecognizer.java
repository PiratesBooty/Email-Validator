package emailRecognizerTest;

/**
 * 
 * Email address verification (state machine)
 * 
 * @author PiratesBooty
 * 
 * 
 */

public class EmailRecognizer {
	
	
	
	public static String emailErrorMessage;					// returned error message; if blank, then email is valid
	public static String emailRecognizerInput;				// input string to be evaluated
	public static String inputLine;							// the input line
	public static int userNameRecognizerIndexofError;		// The index of error location
	private static int state;								// current state in the FSM		
	private static int nextState;							// the state to transition to
	private static boolean finalState = false;				// is the current state a final state? false by default
	private static int currentCharIndex;					// index of current char
	private static int emailSize;
	private static char currentChar;						// the current char
	private static boolean hasDomain = false;				// to make sure that the email has a domain. false by default
	private static boolean hasUnsupportedCharacter; 		// detect unsupported characters. Relevant for returning appropriate error message
	private static boolean lastSpecialIsDot;				/** to make sure that a dot is the final special char in the domain 
															*	(Acceptable TLDs: '.com', 'io', etc)
															*/ 
	private static boolean isRunning;						// is the FSM running?
	
	
	private static void DebugPrint() {
		if (currentCharIndex < inputLine.length()) {
			System.out.println(" | Current Char: "+currentChar+" | State: "+state+" | Next State "+nextState+" | Index: "
			+currentCharIndex+ " | Current Size: " +emailSize);
		}
	}
	
	private static void MoveToNextChar() {
		currentCharIndex++;
		if (currentCharIndex < inputLine.length()) {
			currentChar = inputLine.charAt(currentCharIndex);
		} else {
			currentChar = ' ';
			isRunning = false;
		}
		
	}
	
	private static boolean IsAlphanumeric(char charInQuestion) {
		if ((charInQuestion >= 'A' && charInQuestion <= 'Z' ) ||	// Check for A-Z
			(charInQuestion >= 'a' && charInQuestion <= 'z' ) ||	// Check for a-z
			(charInQuestion >= '0' && charInQuestion <= '9' )) {    // Check for 0-9
			return true; }
		
		return false;
	}
	
	public static String checkForValidEmail(String input) {
		// set error message to nothing
		emailErrorMessage = "";
		
		// begin by checking if input is an empty string
		if(input.length() == 0) {
			userNameRecognizerIndexofError = 0;
			return "***ERROR*** there is no input";
		}
		
		
		// variables to keep track of in order to move through the input string
		state = 0;
		inputLine = input;
		currentCharIndex = 0;
		currentChar = inputLine.charAt(currentCharIndex);
		emailSize = 0;
		
		emailRecognizerInput = input;		// save copy of input
		isRunning = true;					// Start the loop
		nextState = -1;						// There is no next state yet
		
		hasDomain = false;					// will be found later
		lastSpecialIsDot = false;			// will be determined later
		
		while(isRunning) {
			
			// perform appropriate action of current state, otherwise stop running
			switch (state) {
			
				case 0: // visited only once
					// state 0 can transition to state 1
					// Action: if an alphaneumeric char is found --> move to state 1
					
					if (IsAlphanumeric(currentChar)) {	// Check alphaneumericism
						
						nextState = 1;									// Go to Next State
						emailSize++;									// Increment Size
						
					} else {isRunning = false;} // fail
					
					
					break;
					
				case 1:
					// state 1 can transition to itself and states 2 and 3
					/**
					 * Actions:
					 * 		Find alphameumeric char --> stay in this state
					 * 		Find special char --> go to state 2
					 * 		Find @ --> go to state 3
					 */
					
					//Stay int this state
					if (IsAlphanumeric(currentChar)) {	// Check for alphaneumericsm
							
							nextState = 1;								// Go to Next State
							emailSize++;								// Increment Size
							
					}
					//Go to State 2
					else if (currentChar == '+' || currentChar == '-' || currentChar == '_' || currentChar == '.') {	// Check for allowed special chars
							
							nextState = 2;							// Go to Next State
							emailSize++;							// Increment Size
							
						}
					//Go to State 3
					else if (currentChar == '@') { // Check for @ -- What is after @ will be the domain
						
						nextState = 3;
						emailSize++;
						
					} else { hasUnsupportedCharacter = true; isRunning = false; } // fail, due to unsupported character in email username
					
					break;
					
				case 2:
					// state 2 can transition to state 1 
					// Action: find alphaneumeric char --> move back to state 1
					if (IsAlphanumeric(currentChar)) {	// Check alphaneumericism
						
						nextState = 1;									// Go to Next State
						emailSize++;									// Increment Size
						
					} else {isRunning = false;} // fail
					
					break;
					
				case 3: // visited only once
					// state 3 can transtition to state 4
					// Action: find alphaneumric character --> move to state 4 (domain has been found)
					if (IsAlphanumeric(currentChar)) {	// Check for alphaneumericsm
							
							nextState = 4;									// Go to Next State
							emailSize++;									// Increment Size
							hasDomain = true;								// If something's after the '@', there is a domain
							
					} else {isRunning = false;} // fail
					
					break;
					
				case 4:
					// state 4 is the final state
					// state 4 can transition to itself and state 5 and 6
					/**
					 * Actions:
					 * 		Find alphaneumeric char --> stay in this state
					 * 		Find dot --> move to state 5
					 * 		Find dash --> move to state 6
					 * 		
					 */
					if (IsAlphanumeric(currentChar)) {	// Check for alphaneumericsm
						
						nextState = 4;									// Go to Next State
						emailSize++;
					}
					
					else if (currentChar == '.') { // Check for a dot
						nextState = 5; //next state
						emailSize++;
						lastSpecialIsDot = true; // last special (so far) is a dot
					}
					
					else if (currentChar == '-') { // Check for a dot
						nextState = 6; //next state
						emailSize++;
						lastSpecialIsDot = false; // last special (so far) is not a dot
					} else { hasUnsupportedCharacter = true; isRunning = false; } // fail, due to unsupported character in domain name
					
					
					break;
					
				case 5:
					// state 5 can transtition to state 4
					// Action: find alphaneumric character --> move back to state 4
					if (IsAlphanumeric(currentChar)) {	// Check for alphaneumericsm
						
						nextState = 4;									// Go to Next State
						emailSize++;
					} else { isRunning = false; }
					
					break;
					
				case 6:
					// state 6 can transition to state 4
					// Action: find alphaneumric character --> move back to state 4
					if (IsAlphanumeric(currentChar)) {	// Check for alphaneumericsm
						
						nextState = 4;									// Go to Next State
						emailSize++;
					} else { isRunning = false; }
					
					break;
				
			}	
			
			// go to next state based on next state var and if machine hasn't stopped running
			if(isRunning) {
				DebugPrint();
				MoveToNextChar();
				state = nextState;
				
				if(state == 4) {
					finalState = true;
				} else {finalState = false; }
			
			}
		}
		/** assess any potential errors once machine is no longer running
		 * 
		 * determine what the exact error is based on the state that the machine was stopped in
		 * (using yet another switch statement)
		 * 
		 * add the errors to the error message string
		 * 
		 * if there are none, then the error message will be blank
		 */
		if (!finalState) { 
			if(emailRecognizerInput.contains("@")) { // check for @ in case state 3 is never reached
				hasDomain = true;
			}
			
			switch (state) {
				// pre-domain states
				case 0:
					emailErrorMessage += "Email username must begin with alphaneumeric character; ";
					break;
				case 1:
					if(hasUnsupportedCharacter) { // if this is true while stopped in state 1, unsupported char is in username
						emailErrorMessage += "Email username contains an unsupported character; ";
					}
					break;
				case 2:
					emailErrorMessage += "Email username contains invalid character sequence; ";
					break;
				case 3:
					emailErrorMessage += "Email domain name must begin with alphaneumeric character; ";
					break;
				
				// post-domain states
				case 5:
					emailErrorMessage += "Email domain contains invalid character sequence; ";
					break;
				case 6:
					emailErrorMessage += "Email domain contains invalid character sequence; ";
					break;
			}
			
			if(!hasDomain) {
				emailErrorMessage += "Email has no domain; ";
			}
			
		} else { // if final state is reached, test for these possible faults. these error messages can all stack
			if(!lastSpecialIsDot) { // ex: '.co.uk' is valid but '.co-uk' is not. checking this only makes sense if a domain is confirmed to be there
				emailErrorMessage += "Email has invalid domain; ";
			}
			if(hasUnsupportedCharacter) { //if this is true while stopped in state 1, unsupported char is in domain name
				emailErrorMessage += "Email domain name contains an unsupported character; ";
			}
			if(emailSize > 254) { // email must meet char limit. only tell user this if their email is valid in all other ways (to keep error message short)
				emailErrorMessage += "Email is too long; ";
			}
		}
		
		return emailErrorMessage;
	}

}
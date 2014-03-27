/**
  PatrimonioException
  https://github.com/ParleyMartins/Tecnicas/blob/estiloDesign/src
  /exception/PatrimonioException.java
 */
package exception;

@SuppressWarnings("serial")
public class PatrimonioException extends Exception {

	// Constructor creates a PatrimonioExcpetion without any argument.
	public PatrimonioException() {
		super();
	}
	// Constructor creates a PatrimonioExcpetion with a message.
	public PatrimonioException(String msg) {
		super(msg);
	}

}

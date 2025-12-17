package manohar.munnur.sqp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailAlreadyExistException extends RuntimeException {
	public EmailAlreadyExistException(String message) {
		super(message);
	}
}

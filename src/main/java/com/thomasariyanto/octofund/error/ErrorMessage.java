package com.thomasariyanto.octofund.error;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ErrorMessage {
	private int status;
    private Date timestamp;
    private List<String> errors;
 
    public ErrorMessage(HttpStatus status, List<String> errors) {
        super();
        this.timestamp = new Date();
        this.status = status.value();
        this.errors = errors;
    }
    
    public ErrorMessage(HttpStatus status, String error) {
        super();
        this.timestamp = new Date();
        this.status = status.value();
        this.errors = Arrays.asList(error);
    }

	public int getStatus() {
		return status;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public List<String> getErrors() {
		return errors;
	}
    
}

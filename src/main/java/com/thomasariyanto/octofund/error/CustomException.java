package com.thomasariyanto.octofund.error;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomException {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorMessage handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
	    List<String> errors = new ArrayList<String>();
	    
	    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
	        errors.add(error.getDefaultMessage());
	    }
	    
	    ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST, errors);
	    
	    return err;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorMessage handleConstraintViolationExceptions(ConstraintViolationException ex) {
	    List<String> errors = new ArrayList<String>();
	    
	    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
	    	errors.add(violation.getMessage());
	    }
	    
	    ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST, errors);
	    
	    return err;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorMessage handleDataIntegrityViolationExceptions(DataIntegrityViolationException ex) {
	    String error = ex.getMostSpecificCause().getMessage();
	    String message = "Data integrity constraint!";
	    if (error.toLowerCase().contains("duplicate entry")) {
	    	Pattern pattern = Pattern.compile("'(.*?)'");
	    	Matcher matcher = pattern.matcher(error);
	    	if (matcher.find())
	    	{
	    		message = "Data " + matcher.group(1) + " sudah terdapat di dalam sistem!";
	    	}
	    }
	    ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST, message);
	    return err;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NoSuchElementException.class)
	public ErrorMessage handleNoSuchElementExceptions(NoSuchElementException ex) {
	    ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST, "Data tidak ditemukan!");
	    return err;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	public ErrorMessage handleRuntimeExceptions(RuntimeException ex) {
	    String error = ex.getMessage();
	    ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST, error);
	    return err;
	}
}

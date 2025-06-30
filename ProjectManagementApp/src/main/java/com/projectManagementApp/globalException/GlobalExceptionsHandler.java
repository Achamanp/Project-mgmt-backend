package com.projectManagementApp.globalException;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse.ResponseInfo;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionsHandler {
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> resourceNotFoundExceptionHandler(
	        HttpServletRequest request,
	        ResourceNotFoundException ex) {

	    ErrorResponse errorResponse = new ErrorResponse(
	            LocalDateTime.now(),
	            HttpStatus.NOT_FOUND.value(),
	            "Resource Not Found!!",
	            ex.getMessage(),
	            request.getRequestURI()
	    );

	    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> invalidTokenExceptionHandler(
			 HttpServletRequest request,
		        InvalidTokenException ex){
		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(),
				"Invalid token please provide another token!!",
				ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
	}
	
	@ExceptionHandler(EmailAllreadyExist.class)
	public ResponseEntity<ErrorResponse> handleEmailAllreadyExist(
	    HttpServletRequest request,
	    EmailAllreadyExist ex) {  // Changed parameter type from ResourceNotFoundException

	    ErrorResponse err = new ErrorResponse(
	        LocalDateTime.now(), 
	        HttpStatus.CONFLICT.value(),  // 409 status code for conflict
	        "Email Already Exists", 
	        ex.getMessage(), 
	        request.getRequestURI()
	    );
	    
	    return new ResponseEntity<>(err, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<ErrorResponse> handleOtpExpiredException(
			HttpServletRequest request,
			OtpExpiredException ex
			){
		ErrorResponse err = new ErrorResponse(LocalDateTime.now(),
				HttpStatus.FORBIDDEN.value(),
				"Otp Has Been Expired", ex.getMessage(), request.getRequestURI());
		return new ResponseEntity<ErrorResponse>(err, HttpStatus.FORBIDDEN);
		
	}
	
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<ErrorResponse> handleInvalidOtpException(
	        HttpServletRequest request,
	        InvalidOtpException ex) {
	    
	    ErrorResponse errorResponse = new ErrorResponse(
	            LocalDateTime.now(),
	            400,
	            "Invalid OTP",
	            ex.getMessage(),
	            request.getRequestURI()
	    );
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	

}

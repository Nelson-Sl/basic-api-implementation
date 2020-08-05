package com.thoughtworks.rslist.components;

import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({InvalidIndexInputException.class, MethodArgumentNotValidException.class})
    public ResponseEntity exceptionHandler(Exception iie) {
        String errorMessage;
        CommonError commonError = new CommonError();
        if(iie instanceof InvalidIndexInputException) {
            errorMessage = "invalid index";
        }else{
            errorMessage = "invalid param";
        }

        commonError.setError(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}

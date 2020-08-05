package com.thoughtworks.rslist.components;

import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import com.thoughtworks.rslist.exception.InvalidRequestParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({InvalidIndexInputException.class, MethodArgumentNotValidException.class,
            InvalidRequestParamException.class})
    public ResponseEntity exceptionHandler(Exception iie) {
        String errorMessage;
        CommonError commonError = new CommonError();
        if(iie instanceof MethodArgumentNotValidException){
            errorMessage = "invalid param";
        }else{
            errorMessage = iie.getMessage();
        }

        commonError.setError(errorMessage);
        logger.error(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}

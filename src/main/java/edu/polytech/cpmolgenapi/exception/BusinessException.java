package edu.polytech.cpmolgenapi.exception;

import org.springframework.web.ErrorResponseException;

public class BusinessException extends ErrorResponseException {

    /**
     * Constructor accepting an exception reason.
     *
     * @param reason the reason of the exception
     */
    public BusinessException(final BusinessExceptionReason reason) {
        super(reason.getStatusCode(), reason.getBody(), null);
    }

}

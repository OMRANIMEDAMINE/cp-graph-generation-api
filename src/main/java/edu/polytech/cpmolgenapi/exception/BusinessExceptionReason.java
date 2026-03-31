package edu.polytech.cpmolgenapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

/**
 * Defines the business exception reasons.
 */
public enum BusinessExceptionReason implements ErrorResponse {
    MATRIX_GENERATION_EXCEPTION(HttpStatus.CONFLICT, "Failed generation. Try again later !"),
    INVALID_MOLECULE_CONFIG_ATOM_SET_ISEMPTY_EXCEPTION(HttpStatus.BAD_REQUEST, "Empty atom set !"),
    INVALID_MOLECULE_CONFIG_INDEXRANGE_EXCEPTION(HttpStatus.BAD_REQUEST, "Atom index out of valid range !"),
    INVALID_MOLECULE_CONFIG_INDEXDUPLICATED_EXCEPTION(HttpStatus.BAD_REQUEST, "Duplicate atom index !"),
    INVALID_MOLECULE_CONFIG_INDEXMISSSING_EXCEPTION(HttpStatus.BAD_REQUEST, "Missing atom index !"),
    INVALID_MOLECULE_CONFIG_HYBRIDIZATION_EXCEPTION(HttpStatus.BAD_REQUEST, "Invalid atom hybridization state !"),
    INVALID_MOLECULE_CONFIG_MULTIPLICITY_EXCEPTION(HttpStatus.BAD_REQUEST, "Invalid atom multiplicity state !"),

    INVALID_MOLECULE_CONFIG_CORRELATION_EXCEPTION(HttpStatus.BAD_REQUEST, "With BOND, COSY or HMBC, atomSet1 and atomSet2 need to be of equal size !");

    private final HttpStatusCode status;
    private final ProblemDetail body;

    BusinessExceptionReason(HttpStatusCode status, String detail) {
        this.status = status;
        this.body = ProblemDetail.forStatusAndDetail(status, detail);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return status;
    }

    @Override
    public ProblemDetail getBody() {
        return body;
    }
}

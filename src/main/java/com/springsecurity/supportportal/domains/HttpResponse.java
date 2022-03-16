package com.springsecurity.supportportal.domains;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.http.HttpStatus;

public class HttpResponse {
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern= "MM-dd-yyyy hh:mm:ss")
    private Date timeStamp;

    // e.g 200 = OK
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String massage;

    public HttpResponse() {
    }

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String massage) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.massage = massage;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMassage() {
        return this.massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

}

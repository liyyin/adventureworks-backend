package com.refresh.customer_api.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String resource, Object id){
        super(resource + " dengan id " + id +" tidak ditemukan ");
    }
}

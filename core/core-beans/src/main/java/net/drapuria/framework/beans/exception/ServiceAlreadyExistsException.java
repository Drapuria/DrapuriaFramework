package net.drapuria.framework.beans.exception;

public class ServiceAlreadyExistsException extends IllegalArgumentException {

    public ServiceAlreadyExistsException(String serviceName) {
        super("The service with name " + serviceName + " already exists!");
    }

}

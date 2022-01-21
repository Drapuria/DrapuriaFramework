/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.jackson.libraries;

public class LibraryDownloadException extends Exception {

    public LibraryDownloadException() {

    }

    public LibraryDownloadException(String message) {
        super(message);
    }

    public LibraryDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryDownloadException(Throwable cause) {
        super(cause);
    }
}
package co.ohmygoods.file.exception;

import co.ohmygoods.global.exception.DomainException;

public class FileException extends DomainException {

    public static final FileException NOT_FOUND_FILE = new FileException(FileError.NOT_FOUND_FILE);

    public static final FileException FAILED_CREATE_DIRECTORY = new FileException(FileError.FAILED_CREATE_DIRECTORY);
    public static final FileException FAILED_CREATE_FILE = new FileException(FileError.FAILED_CREATE_FILE);
    public static final FileException FAILED_FILE_UPLOAD = new FileException(FileError.FAILED_FILE_UPLOAD);
    public static final FileException FAILED_FILE_DOWNLOAD = new FileException(FileError.FAILED_FILE_DOWNLOAD);

    public static final FileException INVALID_FILE = new FileException(FileError.INVALID_FILE);
    public static final FileException INVALID_CLOUD_PROVIDER = new FileException(FileError.INVALID_CLOUD_PROVIDER);

    public FileException(FileError fileError) {
        super(fileError);
    }
}

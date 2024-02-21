package src.utils.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XMLProcessingRequest implements Request {

    private final String fname;
    private final String body;

    public XMLProcessingRequest(String fname) throws IOException {
        this.fname = fname;
        this.body = readFile(fname);
    }

    private String readFile(String fname) throws IOException {
        return Files.readString(Path.of(fname));
    }

    @Override
    public byte getID() {
        return RequestID.REQUEST_XML_PROCESSING;
    }

    @Override
    public int getLength() {
        return body.length();
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "XMLProcessingRequest{" +
                "fname='" + fname + '\'' +
                '}';
    }
}

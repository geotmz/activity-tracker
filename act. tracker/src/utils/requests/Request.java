package src.utils.requests;

import java.io.Serializable;

public interface Request extends Serializable {
    byte getID();

    int getLength();

    String getBody();

    class RequestID {
        public final static byte REQUEST_XML_PROCESSING = 1;
        public final static byte REQUEST_USER_DATA = 2;
    }
}

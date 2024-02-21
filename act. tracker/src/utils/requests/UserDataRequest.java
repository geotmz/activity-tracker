package src.utils.requests;

public class UserDataRequest implements Request {
    private final String body;

    public UserDataRequest(String username) {
        this.body = username;
    }

    @Override
    public byte getID() {
        return RequestID.REQUEST_USER_DATA;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String getBody() {
        return body;
    }
}

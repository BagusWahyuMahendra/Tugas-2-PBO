import java.util.HashMap;
import java.util.Map;

public class Response {
    private int statusCode;
    private String message;
    private Map<String, Object> data;

    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new HashMap<>();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
}

package net.danygames2014.nyaview;

public class ActionResult {
    public int code;
    public String message;

    public ActionResult(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public boolean successful() {
        return code == 0;
    }
}

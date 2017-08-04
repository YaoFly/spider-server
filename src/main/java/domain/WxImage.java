package domain;

/**
 * Created by ooopic on 2017/8/2.
 */
public class WxImage {
    private String name;
    private byte[] bytes;

    public WxImage(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}


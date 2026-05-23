import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {
    private final InputStream in;
    private int currentByte;
    private int bitsRemaining;

    public BitInputStream(InputStream in) {
        this.in = in;
    }

    public int readBit() throws IOException {
        if (bitsRemaining == 0) {
            currentByte = in.read();
            if (currentByte == -1) {
                return -1;
            }
            bitsRemaining = 8;
        }

        bitsRemaining--;
        return (currentByte >> bitsRemaining) & 1;
    }
}

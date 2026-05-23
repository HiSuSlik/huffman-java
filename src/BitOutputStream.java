import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {
    private final OutputStream out;
    private int currentByte;
    private int bitCount;

    public BitOutputStream(OutputStream out) {
        this.out = out;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Бит должен быть 0 или 1");
        }

        currentByte = (currentByte << 1) | bit;
        bitCount++;

        if (bitCount == 8) {
            flushCurrentByte();
        }
    }

    public void writeCode(String code) throws IOException {
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '0') {
                writeBit(0);
            } else if (c == '1') {
                writeBit(1);
            } else {
                throw new IllegalArgumentException("Код Хаффмана должен состоять только из 0 и 1");
            }
        }
    }

    private void flushCurrentByte() throws IOException {
        out.write(currentByte);
        currentByte = 0;
        bitCount = 0;
    }

    public void flush() throws IOException {
        if (bitCount > 0) {
            currentByte <<= (8 - bitCount);
            flushCurrentByte();
        }
        out.flush();
    }
}

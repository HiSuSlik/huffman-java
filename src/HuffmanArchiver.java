import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PriorityQueue;

public class HuffmanArchiver {
    private static final byte[] MAGIC = new byte[] {'H', 'U', 'F', '1'};
    private static final int ALPHABET_SIZE = 256;

    public static void encode(Path inputPath, Path outputPath) throws IOException {
        byte[] data = Files.readAllBytes(inputPath);
        long[] frequencies = countFrequencies(data);
        HuffmanNode root = buildTree(frequencies);
        String[] codes = buildCodes(root);

        createParentDirectory(outputPath);

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(outputPath)))) {
            writeHeader(out, data.length, frequencies);

            if (data.length == 0 || root.isLeaf()) {
                out.flush();
                return;
            }

            BitOutputStream bitOut = new BitOutputStream(out);
            for (byte b : data) {
                int value = b & 0xFF;
                bitOut.writeCode(codes[value]);
            }
            bitOut.flush();
        }
    }

    public static void decode(Path inputPath, Path outputPath) throws IOException {
        createParentDirectory(outputPath);

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(inputPath)));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(outputPath))) {

            long originalLength = readAndCheckHeader(in);
            long[] frequencies = readFrequencies(in);
            checkFrequencies(originalLength, frequencies);

            HuffmanNode root = buildTree(frequencies);

            if (originalLength == 0) {
                return;
            }

            if (root == null) {
                throw new IOException("Некорректный архив: нет дерева Хаффмана");
            }

            if (root.isLeaf()) {
                for (long i = 0; i < originalLength; i++) {
                    out.write(root.value);
                }
                return;
            }

            BitInputStream bitIn = new BitInputStream(in);
            HuffmanNode current = root;
            long written = 0;

            while (written < originalLength) {
                int bit = bitIn.readBit();
                if (bit == -1) {
                    throw new IOException("Некорректный архив: данные закончились раньше времени");
                }

                current = (bit == 0) ? current.left : current.right;

                if (current.isLeaf()) {
                    out.write(current.value);
                    written++;
                    current = root;
                }
            }
        }
    }

    private static long[] countFrequencies(byte[] data) {
        long[] frequencies = new long[ALPHABET_SIZE];
        for (byte b : data) {
            frequencies[b & 0xFF]++;
        }
        return frequencies;
    }

    private static HuffmanNode buildTree(long[] frequencies) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                queue.add(new HuffmanNode(i, frequencies[i]));
            }
        }

        if (queue.isEmpty()) {
            return null;
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            queue.add(new HuffmanNode(left, right));
        }

        return queue.poll();
    }

    private static String[] buildCodes(HuffmanNode root) {
        String[] codes = new String[ALPHABET_SIZE];
        if (root == null) {
            return codes;
        }
        fillCodes(root, "", codes);
        return codes;
    }

    private static void fillCodes(HuffmanNode node, String code, String[] codes) {
        if (node.isLeaf()) {
            codes[node.value] = code.isEmpty() ? "0" : code;
            return;
        }

        fillCodes(node.left, code + "0", codes);
        fillCodes(node.right, code + "1", codes);
    }

    private static void writeHeader(DataOutputStream out, long originalLength, long[] frequencies) throws IOException {
        out.write(MAGIC);
        out.writeLong(originalLength);

        int uniqueCount = 0;
        for (long frequency : frequencies) {
            if (frequency > 0) {
                uniqueCount++;
            }
        }

        out.writeInt(uniqueCount);

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                out.writeByte(i);
                out.writeLong(frequencies[i]);
            }
        }
    }

    private static long readAndCheckHeader(DataInputStream in) throws IOException {
        for (byte expected : MAGIC) {
            byte actual = in.readByte();
            if (actual != expected) {
                throw new IOException("Это не файл формата HUF1");
            }
        }

        long originalLength = in.readLong();
        if (originalLength < 0) {
            throw new IOException("Некорректный архив: отрицательная длина исходного файла");
        }
        return originalLength;
    }

    private static long[] readFrequencies(DataInputStream in) throws IOException {
        int uniqueCount = in.readInt();
        if (uniqueCount < 0 || uniqueCount > ALPHABET_SIZE) {
            throw new IOException("Некорректный архив: неверное количество символов в словаре");
        }

        long[] frequencies = new long[ALPHABET_SIZE];

        for (int i = 0; i < uniqueCount; i++) {
            int value = in.readUnsignedByte();
            long frequency = in.readLong();

            if (frequency <= 0) {
                throw new IOException("Некорректный архив: частота должна быть положительной");
            }
            if (frequencies[value] != 0) {
                throw new IOException("Некорректный архив: символ в словаре повторяется");
            }

            frequencies[value] = frequency;
        }

        return frequencies;
    }

    private static void checkFrequencies(long originalLength, long[] frequencies) throws IOException {
        long sum = 0;
        for (long frequency : frequencies) {
            sum += frequency;
            if (sum < 0) {
                throw new IOException("Некорректный архив: переполнение суммы частот");
            }
        }

        if (sum != originalLength) {
            throw new IOException("Некорректный архив: сумма частот не совпадает с длиной исходного файла");
        }
    }

    private static void createParentDirectory(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}

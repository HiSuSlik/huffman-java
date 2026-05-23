import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || isHelp(args[0])) {
            printHelp();
            return;
        }

        String mode = args[0].toLowerCase();

        try {
            switch (mode) {
                case "encode":
                case "e":
                    runEncode(args);
                    break;
                case "decode":
                case "d":
                    runDecode(args);
                    break;
                default:
                    System.out.println("Неизвестный режим: " + args[0]);
                    printHelp();
                    System.exit(1);
            }
        } catch (IOException ex) {
            System.err.println("Ошибка работы с файлами: " + ex.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException ex) {
            System.err.println("Ошибка аргументов: " + ex.getMessage());
            System.exit(3);
        }
    }

    private static void runEncode(String[] args) throws IOException {
        checkArgumentCount(args, 3);

        Path inputPath = Path.of(args[1]);
        Path outputPath = Path.of(args[2]);
        checkInputFile(inputPath);

        HuffmanArchiver.encode(inputPath, outputPath);
        System.out.println("Файл закодирован:");
        System.out.println(inputPath + " -> " + outputPath);
    }

    private static void runDecode(String[] args) throws IOException {
        checkArgumentCount(args, 3);

        Path inputPath = Path.of(args[1]);
        Path outputPath = Path.of(args[2]);
        checkInputFile(inputPath);

        HuffmanArchiver.decode(inputPath, outputPath);
        System.out.println("Файл декодирован:");
        System.out.println(inputPath + " -> " + outputPath);
    }

    private static void checkArgumentCount(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("ожидалось аргументов: " + expected + ", получено: " + args.length);
        }
    }

    private static void checkInputFile(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("входной файл не найден: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("это не обычный файл: " + path);
        }
    }

    private static boolean isHelp(String value) {
        return value.equals("help") || value.equals("--help") || value.equals("-h");
    }

    private static void printHelp() {
        System.out.println("Программа кодирования и декодирования по методу Хаффмана");
        System.out.println();
        System.out.println("Запуск после компиляции:");
        System.out.println("  java -cp out Main encode <входной файл> <выходной файл>");
        System.out.println("  java -cp out Main decode <входной файл> <выходной файл>");
        System.out.println();
        System.out.println("Запуск через jar:");
        System.out.println("  java -jar huffman.jar encode <входной файл> <выходной файл>");
        System.out.println("  java -jar huffman.jar decode <входной файл> <выходной файл>");
        System.out.println();
        System.out.println("Короткие режимы тоже работают:");
        System.out.println("  e вместо encode");
        System.out.println("  d вместо decode");
    }
}

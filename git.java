import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.nio.file.Files;
import java.nio.file.Path;

public class git {

    public static void main(String[] args) {
        // init();

        // hashfile test
        // try {
        // FileWriter helloWriter = new FileWriter("Hello.txt");
        // helloWriter.write("Hello World\n");
        // helloWriter.close();

        // BufferedReader helloReader = new BufferedReader(new FileReader("Hello.txt"));
        // String hello = helloReader.readLine();
        // helloReader.close();
        // System.out.println("hello.txt: " + hello);

        // System.out.println(hashFile("hello.txt"));
        // } catch (IOException e) {
        // System.out.println("File error: " + e.getMessage());
        // }

        try {
            FileWriter helloWriter = new FileWriter("Hello.txt");
            helloWriter.write("Hello World\n");
            helloWriter.close();
            createBlob("Hello.txt");
        } catch (Exception e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void init() {
        try {
            File gitDir = new File("git");
            if (gitDir.exists())
                throw new IOException("Git directory exists");
            gitDir.mkdirs();

            File objectsDir = new File("git/Objects");
            objectsDir.mkdirs();

            File index = new File("git/index");
            if (!index.exists())
                index.createNewFile();

            File head = new File("git/HEAD");
            if (!head.exists())
                head.createNewFile();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String hashFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path))
            throw new IOException("No such files: " + filePath);

        byte[] fileBytes = Files.readAllBytes(path);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-1 is not avaliable", e);
        }
        byte[] hash = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hash);
    }

    public static void createBlob(String filePath) {
        try {
            String hash = hashFile(filePath);
            System.out.println(hash); // test line
            FileWriter blobWriter = new FileWriter("git/objects/" + hash);
            BufferedReader OGFileReader = new BufferedReader(new FileReader(filePath));
            String blob = OGFileReader.readLine();
            System.out.println(blob); // test line
            OGFileReader.close();

            blobWriter.write(blob);
            blobWriter.close();
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
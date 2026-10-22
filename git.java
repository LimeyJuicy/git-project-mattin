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
        init();

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
            FileWriter byeWriter = new FileWriter("Bye.txt");
            byeWriter.write("Bye World\n");
            byeWriter.close();
            createBlob("Bye.txt");

            // taj's tree test (TTT)
            String workingList = "blob 1111111111111111111111111111111111111111 myProgram/docs/Hello.txt\n"
                    + "blob 2222222222222222222222222222222222222222 myProgram/docs/World.txt\n"
                    + "blob 3333333333333333333333333333333333333333 myProgram/README.md";
            String treeHash = createTree(workingList, "myProgram/docs");
            System.out.println("Tree hash: " + treeHash);
            // Call it again to check that the hash is the same
            String secondHash = createTree(workingList, "myProgram/docs");
            System.out.println("Second tree hash: " + secondHash);

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

            File objectsDir = new File("git/objects");
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
            // System.out.println(hash); // test line
            FileWriter blobWriter = new FileWriter("git/objects/" + hash);
            BufferedReader OGFileReader = new BufferedReader(new FileReader(filePath));
            String blob = OGFileReader.readLine();
            // System.out.println(blob); // test line
            OGFileReader.close();

            blobWriter.write(blob);
            blobWriter.close();
            updateIndex(filePath);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateIndex(String filePath) {
        try {
            String hash = hashFile(filePath);
            FileWriter indexWriter = new FileWriter("git/index", true);
            System.out.println(hash + " " + filePath);
            BufferedReader indexReader = new BufferedReader(new FileReader("git/index"));
            if (indexReader.readLine() != null)
                indexWriter.write("\n" + hash + " " + filePath);
            else
                indexWriter.write(hash + " " + filePath);

            indexReader.close();
            indexWriter.close();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String createTree(String workingList, String dirPath) {
        try {
            String treeFileContents = "";
            String[] listOfEntries = workingList.split("\n");

            for (int i = 0; i < listOfEntries.length; i++) {
                String currentEntry = listOfEntries[i];

                if (currentEntry.equals("") == false) {

                    int firstSpace = currentEntry.indexOf(" ");
                    int secondSpace = currentEntry.indexOf(" ", firstSpace + 1);

                    String fileType = currentEntry.substring(0, firstSpace);
                    String fileHash = currentEntry.substring(firstSpace + 1, secondSpace);
                    String fullFilePath = currentEntry.substring(secondSpace + 1);

                    int lastSlash = fullFilePath.lastIndexOf("/");

                    if (lastSlash != -1) {
                        String parentDirectory = fullFilePath.substring(0, lastSlash);
                        String fileOrFolderName = fullFilePath.substring(lastSlash + 1);

                        if (parentDirectory.equals(dirPath)) {
                            if (treeFileContents.equals("") == false) {
                                treeFileContents = treeFileContents + "\n";
                            }

                            treeFileContents = treeFileContents
                                    + fileType + " " + fileHash + " " + fileOrFolderName;
                        }
                    }
                }
            }

            FileWriter temporaryFileWriter = new FileWriter("git/treeTemp");
            temporaryFileWriter.write(treeFileContents);
            temporaryFileWriter.close();

            String treeHash = hashFile("git/treeTemp");

            File treeFile = new File("git/objects/" + treeHash);

            if (treeFile.exists() == false) {
                FileWriter treeFileWriter = new FileWriter(treeFile);
                treeFileWriter.write(treeFileContents);
                treeFileWriter.close();
            }

            File temporaryFile = new File("git/treeTemp");
            temporaryFile.delete();

            return treeHash;

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

}
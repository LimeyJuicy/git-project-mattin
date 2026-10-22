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
            FileWriter helloWriter = new FileWriter("myProgram/Hello.txt");
            helloWriter.write("Hello World\n");
            helloWriter.close();
            createBlob("Hello.txt");
            FileWriter byeWriter = new FileWriter("myProgram/Bye.txt");
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

            // tajs other test
            String rootHash = buildRootTree();
            System.out.println("Root tree hash: " + rootHash);

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
            String relativePath = filePath;
            String entry = hash + " " + filePath;
            boolean hasEntries = false;
            boolean duplicate = false;

            BufferedReader indexReader = new BufferedReader(new FileReader("git/index"));
            if (indexReader.readLine() != null)
                indexWriter.write("\n" + hash + " " + filePath);
            else
                indexWriter.write(hash + " " + filePath);

            while (line != null) {
                line = indexReader.readLine();

                if (line != null) {
                    hasEntries = true;

                    if (line.equals(entry)) {
                        duplicate = true;
                    }
                    if (line.endsWith(" " + relativePath) == false) { 
                        if (newIndex.equals("") == false) {
                            newIndex = newIndex + "\n"; 
                        }
                        newIndex = newIndex + line;
                    }
                }
            }

            indexReader.close();

            if (duplicate == false) {
                FileWriter indexWriter = new FileWriter("git/index", true);
                System.out.println(entry);

                if (hasEntries == true) {
                    indexWriter.write("\n");
                }

                indexWriter.write(entry);
                indexWriter.close();
            }

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

    public static String buildRootTree() {
        try {
            // Step 1: Read the index into a working list.
            String[] workingList = new String[1000];
            int count = 0;

            BufferedReader reader = new BufferedReader(new FileReader("git/index"));

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                int space = line.indexOf(' ');
                String hash = line.substring(0, space);
                String path = line.substring(space + 1).trim();

                workingList[count] = "blob " + hash + " " + path;
                count++;
            }

            reader.close();

            // Step 2: Sort the working list alphabetically by path.
            for (int i = 0; i < count - 1; i++) {
                for (int j = i + 1; j < count; j++) {
                    String path1 = getPath(workingList[i]);
                    String path2 = getPath(workingList[j]);

                    if (path1.compareTo(path2) > 0) {
                        String temp = workingList[i];
                        workingList[i] = workingList[j];
                        workingList[j] = temp;
                    }
                }
            }

            // Step 3: Keep processing directories until
            // every entry belongs directly to the root.
            while (true) {
                String deepestDirectory = "";
                int deepestLevel = 0;

                for (int i = 0; i < count; i++) {
                    String path = getPath(workingList[i]);
                    int slash = path.lastIndexOf('/');

                    if (slash != -1) {
                        String parent = path.substring(0, slash);

                        int level = 1;
                        for (int j = 0; j < parent.length(); j++) {
                            if (parent.charAt(j) == '/') {
                                level++;
                            }
                        }

                        if (level > deepestLevel) {
                            deepestLevel = level;
                            deepestDirectory = parent;
                        }
                    }
                }

                // No directories remain to process.
                if (deepestDirectory.equals("")) {
                    break;
                }

                // Create the tree for the deepest directory.
                String treeHash = createTree(
                        makeWorkingListString(workingList, count),
                        deepestDirectory);

                if (treeHash.equals("")) {
                    throw new IOException(
                            "Could not create tree: " + deepestDirectory);
                }

                // Step 4: Remove direct children of this directory.
                int i = 0;

                while (i < count) {
                    String path = getPath(workingList[i]);
                    int slash = path.lastIndexOf('/');

                    String parent = "";

                    if (slash != -1) {
                        parent = path.substring(0, slash);
                    }

                    if (parent.equals(deepestDirectory)) {
                        // Shift all later entries one position left.
                        for (int j = i; j < count - 1; j++) {
                            workingList[j] = workingList[j + 1];
                        }

                        count--;
                    } else {
                        i++;
                    }
                }

                // Add the new tree entry.
                workingList[count] = "tree " + treeHash + " " + deepestDirectory;
                count++;

                // Sort again after collapsing the directory.
                for (int a = 0; a < count - 1; a++) {
                    for (int b = a + 1; b < count; b++) {
                        String path1 = getPath(workingList[a]);
                        String path2 = getPath(workingList[b]);

                        if (path1.compareTo(path2) > 0) {
                            String temp = workingList[a];
                            workingList[a] = workingList[b];
                            workingList[b] = temp;
                        }
                    }
                }
            }

            // Step 5: Build the root tree.
            String rootHash = createTree(
                    makeWorkingListString(workingList, count), "");

            return rootHash;

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    // Gets the path portion of a working-list entry.
    public static String getPath(String entry) {
        int firstSpace = entry.indexOf(' ');
        int secondSpace = entry.indexOf(' ', firstSpace + 1);

        return entry.substring(secondSpace + 1);
    }

    // Converts the array into the newline-separated string
    // expected by createTree().
    public static String makeWorkingListString(
            String[] workingList, int count) {

        String result = "";

        for (int i = 0; i < count; i++) {
            if (i > 0) {
                result += "\n";
            }

            result += workingList[i];
        }

        return result;
    }

}
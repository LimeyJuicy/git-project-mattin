This is Mattin's git project, found inside git-project-mattin. This code was peer reviewed by Taj.

The README.md file claims the following methods exist: - init() - hashFile(String filePath) - createBlob(String filePath) - updateIndex(String filePath)

Here is the commit history. All 4 GP.X are present, although named kinda loosely:
201eb20 Gp 2.4.2 update index (#4)
9d5b9df Gp 2.3 blobs (#3)
2473a3c Created hash function (#2)
260ec60 Coded init and added File Hasher for reference (#1)
738d521 Update Gitignore
3a34527 Initial commit

There is in fact a main method. It calls init(), then creates two files named Hello.txt and Bye.txt. Then it calls
createBlob(String fileName) for each file created. The index gets properly updated with the hash, then the name of the
file.

Feature Map:
-The initialize git method is called init().
-The file hashing method is called hashFile(String filePath).
-The blob creation method is called createBlob(String filePath).
-The index updating method is called updateIndex(String filePath).

Following one path:
        createBlob(filePath)
        - hashFile(filePath)
        - read file contents as byte[]
        - SHA-1 hash the byte[]
        - convert hash byte[] to hex String
        - return hash

        - build object path
                - "git/objects/" + hash

        - read original file
                - read first line as String

        - write String to git/objects/<hash>
                - if object already exists:
                overwrite it

        - updateIndex(filePath)
                - hash file again
                - open git/index in append mode
                - if index already has a line:
                add "\n" before new entry
                - write "<hash> <filePath>"

        Data shapes

        file contents - byte[]
        SHA-1 result - byte[]
        hash - String
        blob content - String
        index entry - String


Line 112 of git.java could technically be a fileWriter instead of a println. 


Final Checks:

Did init() build the structure?
        -Yes
What exactly is in the index?
        -648a6a6ffffdaa0badb23b8baf90b6168dd16b3a Hello.txt
        -362650b7b0530b585859f70b7f86ef00c1fd29c5 Bye.txt
Does the index end in a new line?
        -No
Is the blob a faithful copy?
        -Yes
Is the hash right?
        -Yes
How many blobs exist?
        -2

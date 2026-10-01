# git-project-mattin

# void init() function initializes git folder with HEAD file, index file, and Objects subfolder

# String hashFile(String filePath) returns hash of input file using SHA-1 hash

# void createBlob(String filepath) creates a blob file git/Objects/hash. 
original file (OG file) = file we're turning into a blob
hash is the SHA-1 hash of the OG file's contents by calling hashFile method. the blob file content is the OG file's content
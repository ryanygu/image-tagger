package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;

/**
 * Represents a directory in the user's computer, which contains images and other directories
 * Its methods are called by DirectoryManager
 */
public class Directory implements Serializable {

    /**
     * Stores the directory this Directory is located under, if one exists.
     */
    private Directory parentDirectory;

    /**
     * Stores the Images and/or Directories that is within this Directory.
     */
    private ArrayList<Object> contents = new ArrayList<>();

    /**
     * Stores this Directory's file representation.
     */
    private File file;

    /**
     * Creates a Directory instance based on the given path.
     *
     * @param parentDirectory   the parent directory of this directory
     * @param path              the path where this directory is located
     */
    public Directory(String path, Directory parentDirectory) {
        this.file = new File(path);
        this.parentDirectory = parentDirectory;
    }

    /**
     * Returns the name of this directory
     *
     * @return      the name of this directory.
     */
    public String getName() {
        return this.file.getName();
    }

    /**
     * Returns the directory's parent directory
     *
     * @return      this directory's parent directory.
     */
    Directory getParentDirectory() {
        return this.parentDirectory;
    }

    /**
     * Returns the contents of this directory
     *
     * @return      the contents of this Directory
     */
    ArrayList<Object> getContents() {
        return this.contents;
    }

    /**
     * Adds an Image or Directory to the contents of this Directory.
     *
     * @param obj       the object to add
     */
    void addContents(Object obj) {
        this.contents.add(obj);
    }

    /**
     * Removes an Image or Directory from the contents of this Directory.
     *
     * @param obj       the object to remove
     */
    void removeContents(Object obj) {
        this.contents.remove(obj);
    }

    /**
     * Example as follows: "C:/.../directoryName".
     *
     * @return      returns the path of this directory
     */
    String getPath() {
        return this.file.getPath();
    }

    /**
     * Returns whether or not this Directory contains any other Directories.
     *
     * @return      true iff this directory contains other directories
     */
     boolean containsDirectories() {
        for (Object obj: this.contents) {
            if (obj instanceof Directory) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the file associated with this directory
     *
     * @return this directory's associated file.
     */
    public File getFile() {
        return this.file;
    }
}

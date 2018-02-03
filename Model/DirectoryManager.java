package Model;

import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;

/**
 * Takes method calls from GeneralManager and manipulates Directories in the system accordingly
 */
class DirectoryManager implements Serializable {
    /**
     * Keeps track of every directory under and including root directory
     */
    private ArrayList<Directory> allDirectories = new ArrayList<>();

    /**
     * Creates file if one does not exist, else reads allDirectories from when program was most recently run
     */
    DirectoryManager() {}

    /**
     * Returns all the images under the current directory, including its sub-directories.
     *
     * @param currentDirectory the current directory
     * @return list of images in all sub directories
     */
    ArrayList<Image> getAllImages(Directory currentDirectory) {

        ArrayList<Image> allImages = new ArrayList<>();

        if (!currentDirectory.containsDirectories()) {
            // Base case: if this directory does not contain any other directories
            allImages.addAll(getImages(currentDirectory));
        } else {
            for (Object obj : currentDirectory.getContents()) {
                if (obj instanceof Image) {
                    allImages.add((Image) obj);
                } else {
                    allImages.addAll(getAllImages((Directory) obj));
                }
            }
        }
        return allImages;
    }

    /**
     * Returns images directly under the current directory.
     *
     * @param currentDirectory the current directory
     * @return list of all images in current directory
     */
    ArrayList<Image> getImages(Directory currentDirectory) {
        ArrayList<Image> images = new ArrayList<>();
        for (Object obj : currentDirectory.getContents()) {
            if (obj instanceof Image) {
                images.add((Image) obj);
            }
        }
        return images;
    }

    /**
     * Add the currentImage to the targetDirectory.
     *
     * @param currentImage    the current image
     * @param targetDirectory the directory to be added to
     */
    void addContents(Image currentImage, Directory targetDirectory) {
        targetDirectory.addContents(currentImage);
    }

    /**
     * Removes the currentImage from the targetDirectory.
     *
     * @param currentImage    the current image
     * @param targetDirectory the directory to be removed from
     */
    void removeContents(Image currentImage, Directory targetDirectory) {
        targetDirectory.removeContents(currentImage);
    }

    /**
     * Initializes a directory which is located in the parent directory.
     * Only called when initializing the program and since the system checks for
     * duplicate names, we do not need to check it here.
     *
     * @param path             the path of the directory
     * @param parentDirectory   the parent directory of this directory
     * @return the newly initialized directory
     */
    Directory createDirectory(String path, Directory parentDirectory) {
        Directory directory = new Directory(path, parentDirectory);
        if (parentDirectory != null) {
            parentDirectory.addContents(directory);
        }
        allDirectories.add(directory);
        return directory;
    }

    /**
     * Return a list of all Directories under the current directory.
     *
     * @param currentDirectory the current directory
     * @return list of all directories in current directory
     */
    ArrayList<Directory> getDirectories(Directory currentDirectory) {
        ArrayList<Directory> directories = new ArrayList<>();
        for (Object obj : currentDirectory.getContents()) {
            if (obj instanceof Directory) {
                directories.add((Directory) obj);
            }
        }
        return directories;
    }

    /**
     * Opens the given file in the OS's file viewer.
     *
     * @param file the file to open
     */
    void openDirectory(File file) {
        //Code used from:
        //https://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Returns the Directory instance given a path to the Directory.
     *
     * @param path the path which the directory is from
     * @return the directory given from path
     */
    Directory getDirectoryFromPath(String path) {
        ArrayList<Directory> directories = allDirectories;
        for (Directory directory : directories) {
            if (directory.getPath().equals(path)) {
                return directory;
            }
        }
        return null;
    }

    /**
     * Saves the current state of all directories.
     */
    void serialize() {
        try {
            FileOutputStream fos = new FileOutputStream("directories.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allDirectories);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the past state of all directories.
     */
    void deserialize() {
        try {
            FileInputStream fis = new FileInputStream("directories.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            allDirectories = (ArrayList<Directory>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the File associated with the root directory of the system
     */
    Directory getRootDirectory() {
        for (Directory directory: allDirectories) {
            if (directory.getParentDirectory() == null) {
                return directory;
            }
        }
        return null;
    }

}
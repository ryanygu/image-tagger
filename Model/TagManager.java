package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that manages the functions of the Tag class
 */
public class TagManager extends Observable implements Serializable {

    /**
     * Observers for allTags
     */
    private ArrayList<Observer> observers = new ArrayList<>();

    /**
     * List which stores all the tags
     */
    private ArrayList<Tag> allTags = new ArrayList<>();

    /**
     * Initializes an instance of TagManager.
     */
    public TagManager() {}

    /**
     * Adds an observer to this tag manager's list of observers for allTags instance variable
     *
     * @param o: the observer to be added
     */
    @Override
    public void addObserver(Observer o){
        this.observers.add(o);
    }

    /**
     * Updates the state of observers of allTags
     */
    @Override
    public void notifyObservers(){
        for (Observer o : observers){
            o.update(this, allTags);
        }
    }

    /**
     * Creates a new tag with given name and add to the list of all the tags.
     *
     * @param name      the name of the new tag
     * @return newTag   the new tag that has been created
     */
    public Tag createTag(String name) {
        for (Tag tag: allTags) {
            if (tag.getName().equals(name)) {
                return null;
            }
        }
        Tag newTag = new Tag(name);
        allTags.add(newTag);
        notifyObservers();

        return newTag;
    }

    /**
     * Delete a tag by removing it from list of all tags
     *
     * @param tags the tag which is to be deleted
     */
    public void deleteTags(ArrayList<Tag> tags) {
        allTags.removeAll(tags);
        notifyObservers();
    }

    /**
     * Adds the given image to all given Tags
     *
     * @param tags list of tags to be added
     * @param image the image which the tags are added to
     */
    public void addImageToTags(Image image, ArrayList<Tag> tags){
        for (Tag tag : tags){
            tag.addImage(image);
        }
    }

    /**
     * Removes the current image from all of the tags given.
     *
     * @param currentImage the current image
     * @param tags the tags which the image is deleted from
     */
    public void removeImageFromTags(Image currentImage, ArrayList<Tag> tags) {
        for (Tag tag : tags) {
            tag.removeImage(currentImage);
        }
    }

    /**
     * @return returns a list of tags currently in the system.
     */
    public ArrayList<Tag> getAllTags() {
        return this.allTags;
    }

    /**
     * Returns a list of existing Tags in the given name.
     * "initialName @tag @tag2 @tag3.png" --> [tag, tag2, tag3]
     *
     * @param name      the name of an image
     * @return stringToTags     list of newly created tags
     */
    public ArrayList<Tag> stringToTags(String name) {
        ArrayList<Tag> stringToTags = new ArrayList<>();
        String[] tags = name.split("[@.]");
        if (tags.length == 2) {
            //is original name
            return new ArrayList<>();
        } else {
            ArrayList<String> tagList = new ArrayList<>(Arrays.asList(tags));
            tagList.remove(0);
            tagList.remove(tagList.size() - 1);
            for (String tagName: tagList) {//whitespace
                for (Tag tag: allTags) {
                    if (tagName.trim().equals(tag.getName())) {
                        stringToTags.add(tag);
                    }
                }
            }
            // Ensure that the amount of Tags returned is valid
            if (stringToTags.size() != tagList.size()) {
                return null;
            }
        }
        return stringToTags;
    }

    /**
     * Returns a list of new Tags in the given name.
     * Called only when creating the tree structure.
     * "initialName @tag @tag2 @tag3.png" --> [tag, tag2, tag3]
     *
     * @param name  the name of an image
     * @return      a list of newly created Tag objects based on the image name
     */
    public ArrayList<Tag> stringToNewTags(String name) {
        ArrayList<Tag> newTags = new ArrayList<>();
        String[] tags = name.split("[@.]"); // "name @tag1 @tag2.png" -> [name /tag1 /tag2/png]
        ArrayList<String> tagsList = new ArrayList<>();//[name/tag1/tag2/png]
        for (String tag: tags) {
            tagsList.add(tag.trim());
        }
        if (tags.length == 2) {
            //is original name
            return new ArrayList<>();
        } else {
            for (int i = 1; i < tagsList.size() - 1; i++) {
                //check if already a tag
                boolean exists = false;
                for (Tag tag: allTags) {
                    if (tag.getName().equals(tagsList.get(i))) {
                        exists = true;
                        newTags.add(tag);
                        break;
                    }
                }
                if (!exists) {
                    //create the tag and add it to newTags
                    Tag tag = createTag(tagsList.get(i));
                    newTags.add(tag);
                }
            }
        }
        return newTags;
    }

    /**
     * Saves the current state of all tags.
     */
    void serialize() {
        try {
            FileOutputStream fos = new FileOutputStream("tags.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allTags);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the past state of all tags.
     */
    void deserialize() {
        try {
            FileInputStream fis = new FileInputStream("tags.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            allTags = (ArrayList<Tag>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the given tag to the list of all tags.
     *
     * @param tag       the tag to add to the list of all tags
     */
    void addToAllTags(Tag tag) {
        this.allTags.add(tag);
        notifyObservers();
    }

}

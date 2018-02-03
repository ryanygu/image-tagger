package Tests;

import Model.Image;
import Model.Tag;
import Model.TagManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TagManagerTest {
    @Test
    void testTagCreation() {
        TagManager tagManager = new TagManager();
        assertEquals("Name", tagManager.createTag("Name").getName());
    }

    @Test
    void testTagDeletion() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        Tag tag3 = tagManager.createTag("Name3");

        ArrayList<Tag> tagsToRemove = new ArrayList<>();
        tagsToRemove.add(tag1);
        tagsToRemove.add(tag2);

        ArrayList<Tag> tagsLeft = new ArrayList<>();
        tagsLeft.add(tag3);

        tagManager.deleteTags(tagsToRemove);

        assertEquals(tagsLeft, tagManager.getAllTags());

    }

    @Test
    void testAddImageToTags() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        Image image = new Image();
        tagManager.addImageToTags(image, tags);

        assertEquals(image, tag1.getImages().get(0));
        assertEquals(image, tag2.getImages().get(0));

    }

    @Test
    void testRemoveImageFromTags() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");

        ArrayList<Tag> allTags = new ArrayList<>();
        allTags.add(tag1);
        allTags.add(tag2);

        Image image = new Image();
        tagManager.addImageToTags(image, allTags);

        ArrayList<Tag> tagsToRemove = new ArrayList<>();
        tagsToRemove.add(tag2);
        tagManager.removeImageFromTags(image, tagsToRemove);

        assertEquals(image, tag1.getImages().get(0));
        assertEquals(0, tag2.getImages().size());
    }

    @Test
    void testAllTagsAttribute() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");

        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        assertEquals(tags, tagManager.getAllTags());
    }

    @Test
    void testStringToExistingTags() {
        String newString = "imgName @Name1 @Name2.jpg";
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");

        ArrayList<Tag> manualTags = new ArrayList<>();
        manualTags.add(tag1);
        manualTags.add(tag2);


        assertEquals(manualTags, tagManager.stringToTags(newString));
    }

    @Test
    void testStringToNewTags() {
        String newString = "imgName @Name1 @Name2.jpg";
        TagManager tagManager = new TagManager();
        ArrayList<Tag> newTags = tagManager.stringToNewTags(newString);

        assertEquals("Name1", newTags.get(0).getName());
        assertEquals("Name2", newTags.get(1).getName());
    }

}
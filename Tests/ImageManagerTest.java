package Tests;

import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ImageManagerTest {
    private ImageManager imageManager;

    private Image image1;
    private Path image1Path;

    private Path image2Path;

    private Directory directory;
    private Path directoryPath;

    @BeforeEach
    void setUp() throws IOException {
        this.imageManager = new ImageManager();

        FileAttribute[] fileAttributes = new FileAttribute[0];
        this.directoryPath = Files.createTempDirectory("directory", fileAttributes);
        this.directory = new Directory(directoryPath.toString(), null);

        this.image1Path = Files.createTempFile(directoryPath, "image1", ".jpg", fileAttributes);
        this.image1 = imageManager.createImage(image1Path.toString(), directory);

        this.image2Path = Files.createTempFile(directoryPath, "image2", ".jpg", fileAttributes);

    }

    @AfterEach
    void tearDown() throws IOException {}

    @Test
    void addTagsToImage() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        imageManager.addTagsToImage(image1, tags);

        assertEquals(tags, image1.getTags());

    }

    @Test
    void removeTagsFromImage() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        imageManager.addTagsToImage(image1, tags);

        ArrayList<Tag> removeTags = new ArrayList<>();
        removeTags.add(tag1);

        imageManager.removeTagsFromImage(image1, removeTags);

        assertEquals(tag2, image1.getTags().get(0));

    }


    @Test
    void updateImageTags() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        imageManager.addTagsToImage(image1, tags);

        ArrayList<Tag> updateTags = new ArrayList<>();
        updateTags.add(tag1);

        imageManager.updateImageTags(image1, updateTags);

        assertEquals(1, image1.getTags().size());
        assertEquals(tag1, image1.getTags().get(0));

    }

    @Test
    void getPastNames() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");

        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        imageManager.addTagsToImage(image1, tags);

        tags.clear();
        tags.add(tag2);
        imageManager.addTagsToImage(image1, tags);

        String[] parts = image1Path.toString().split("\\"+File.separator);
        String imageName = parts[parts.length - 1];

        ArrayList<String> imageNames = new ArrayList<>();
        imageNames.add(imageName);
        imageNames.add(imageName.substring(0, imageName.length()-4) + " @Name1.jpg");
        imageNames.add(imageNames.get(1).substring(0, imageNames.get(1).length()-4) + " @Name2.jpg");

        ArrayList<String> pastNames = imageManager.getPastNames(image1);

        for (int i = 0 ; i < 3 ; i++){
            assertEquals(imageNames.get(i), pastNames.get(i));
        }

    }

    @Test
    void createImageWithTags() {
        TagManager tagManager = new TagManager();
        Tag tag1 = tagManager.createTag("Name1");
        Tag tag2 = tagManager.createTag("Name2");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        Image newImage = imageManager.createImageWithTags(image2Path.toString(), directory, tags);

        assertEquals(tags, newImage.getTags());
    }

}
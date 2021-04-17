package dev.majek.hexnicks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Used for JSON configuration files.
 */
public class JsonConfig {

    private final File configFile;
    private final File pluginDataFolder;
    private final String name;

    /**
     * Please notice that the constructor does not yet create the JSON configuration file.
     * To create the file on the disk, use {@link JsonConfig#createConfig()}.
     *
     * @param pluginDataFolder The plugin's data directory, accessible with JavaPlugin#getDataFolder();
     * @param name The name of the config file excluding file extensions.
     */
    public JsonConfig(File pluginDataFolder, String name) {
        this.name = name + ".json";
        this.configFile = new File(pluginDataFolder, this.name);
        this.pluginDataFolder = pluginDataFolder;
    }

    /**
     * This creates the configuration file. If the data folder is invalid, it will be created along with the config file.
     */
    public void createConfig() {
        if (! configFile.exists()) {
            if (! this.pluginDataFolder.exists())
                IGNORE_RESULT(this.pluginDataFolder.mkdir());

            try {
                IGNORE_RESULT(this.configFile.createNewFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return The configuration file's directory. To get its name, use {@link JsonConfig#getName()} instead.
     */
    public File getDirectory() {
        return this.pluginDataFolder;
    }

    /**
     * This returns the name of the configuration file with the .json extension.
     * To get the file's directory, use {@link JsonConfig#getDirectory()}.
     *
     * @return The name of the configuration file, including file extensions.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This returns the actual File object of the config file.
     *
     * @return The config file.
     */
    public File getFile() {
        return this.configFile;
    }

    /**
     * This deletes the config file.
     */
    public void deleteFile() {
        IGNORE_RESULT(this.configFile.delete());
    }

    /**
     * This deletes the config file's directory and all it's contents.
     */
    public void deleteParentDir() {
        IGNORE_RESULT(this.getDirectory().delete());
    }

    /**
     * This deletes and recreates the file, wiping all its contents.
     */
    public void reset() {
        this.deleteFile();
        try {
            IGNORE_RESULT(configFile.createNewFile());
            JSONObject obj = new JSONObject();
            PrintWriter write = new PrintWriter(configFile);
            write.write(obj.toJSONString());
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wipe the config file's directory, including the file itself.
     */
    public void wipeDirectory() {
        IGNORE_RESULT(this.getDirectory().delete());
        IGNORE_RESULT(this.pluginDataFolder.mkdir());
    }

    /**
     * This will create a sub-directory in the plugin's data folder, which can be accessed with
     * {@link JsonConfig#getDirectory()}. If the entered name is not a valid name for a directory or the
     * sub-directory already exists or the data folder does not exist, an IOException will be thrown.
     *
     * @param name The sub directory's name.
     * @throws IOException If the entered string has a file extension or already exists.
     */
    public void createSubDirectory(String name) throws IOException {
        if (!pluginDataFolder.exists())
            throw new IOException("Data folder not found.");

        File subDir = new File(pluginDataFolder, name);

        if (subDir.exists())
            throw new IOException("Sub directory already existing.");

        IGNORE_RESULT(subDir.mkdir());
    }

    /**
     * Checks whether or not the plain text inside the file may be parsed as a valid
     * {@link org.json.simple.JSONObject#JSONObject()}. If the file contain a
     * {@link org.json.simple.JSONArray#JSONArray()} , false will be returned.
     * If the content does not contain a valid JSON structure, the function will return false as well.
     *
     * @throws IOException If the file is invalid.
     * @return true if the content of the file may be parsed as a {@link org.json.simple.JSONObject#JSONObject()}. If not, false will be returned.
     */
    public boolean isJSONObject() throws IOException {
        try {
            new JSONParser().parse(new FileReader(configFile));
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether or not the plain text inside the file may be parsed as a valid
     * {@link org.json.simple.JSONArray#JSONArray()}. If the file contain a
     * {@link org.json.simple.JSONObject#JSONObject()}, false will be returned.
     * If the content does not contain a valid JSON structure, the function will return false as well.
     *
     * @throws IOException If the file is invalid.
     * @return true if the content of the file may be parsed as a {@link org.json.simple.JSONArray#JSONArray()}. If not, false will be returned.
     */
    @SuppressWarnings("unused")
    public boolean isJSONArray() throws IOException {
        try {
            JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(configFile));
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * This creates and returns a new {@link org.json.simple.JSONObject#JSONObject()}, with the contents of the file.
     *
     * @return the contents of the file as a {@link org.json.simple.JSONObject#JSONObject()}.
     * @throws ParseException , IOException if the file does not contain a valid {@link org.json.simple.JSONObject#JSONObject()} or cannot be found.
     */
    public JSONObject toJSONObject() throws IOException, ParseException {
        return (JSONObject) new JSONParser().parse(new FileReader(configFile));
    }

    /**
     * This creates and returns a new {@link org.json.simple.JSONArray#JSONArray()}, with the contents of the file.
     *
     * @throws FileNotFoundException If the file is invalid.
     * @throws ParseException If the file does not contain a valid {@link org.json.simple.JSONArray#JSONArray()}.
     * @return the contents of the file as a {@link org.json.simple.JSONArray#JSONArray()}.
     */
    public JSONArray toJSONArray() throws IOException, FileNotFoundException, ParseException {
        return (JSONArray) new JSONParser().parse(new FileReader(configFile));
    }

    /**
     * Adds a new key-value to the JSONObject inside {@link #getFile()}.
     *
     * @param k The key.
     * @param v The value.
     * @throws IOException Thrown when the file cannot be found.
     * @throws ParseException Thrown when the file does not contain a valid {@link org.json.simple.JSONObject#JSONObject()}.
     */
    @SuppressWarnings("unchecked")
    public void putInJSONObject(Object k, Object v) throws IOException, ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(configFile));
        obj.put(k, v);
        PrintWriter write = new PrintWriter(configFile);
        write.write(obj.toJSONString());
        write.close();
    }

    /**
     * Adds all the keys and values of the Map to the JSONObject inside {@link #getFile()}.
     *
     * @param values The Map with all the values.
     * @throws IOException Thrown when the file cannot be found.
     * @throws ParseException Thrown when the file does not contain a valid {@link org.json.simple.JSONObject#JSONObject()}.
     */
    @SuppressWarnings("unchecked")
    public void putInJSONObject(Map<Object, Object> values) throws IOException, ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(configFile));
        obj.putAll(values);
        PrintWriter write = new PrintWriter(configFile);
        write.write(obj.toJSONString());
        write.close();
    }

    /**
     * Adds a new key-value to the JSONArray inside {@link #getFile()}.
     *
     * @param obj The object to add to the JSONArray.
     * @throws IOException Thrown when the file cannot be found.
     * @throws ParseException Thrown when the file does not contain a valid {@link org.json.simple.JSONArray#JSONArray()}.
     */
    @SuppressWarnings("unchecked")
    public void putInJSONArray(Object obj) throws IOException, ParseException {
        JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(configFile));
        arr.add(obj);
        PrintWriter write = new PrintWriter(configFile);
        write.write(arr.toJSONString());
        write.close();
    }

    /**
     * Adds all the values of the collection to the JSONArray inside {@link #getFile()}.
     *
     * @param c A collection which holds the objects to add to the JSONArray.
     * @throws IOException Thrown when the file cannot be found.
     * @throws ParseException Thrown when the file does not contain a valid {@link org.json.simple.JSONArray#JSONArray()}.
     */
    @SuppressWarnings("unchecked")
    public void putInJSONArray(Collection<Object> c) throws IOException, ParseException {
        JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(configFile));
        arr.addAll(c);
        PrintWriter write = new PrintWriter(configFile);
        write.write(arr.toJSONString());
        write.close();
    }

    /**
     * Used to ignore the annoying "Result of ___ is ignored." warnings.
     * @param b boolean value.
     */
    @SuppressWarnings("unused")
    private void IGNORE_RESULT(boolean b) {

    }
}
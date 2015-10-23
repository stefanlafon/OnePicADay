package soleilcode.onepicaday;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.soleilcode.onepicaday.Configs.AppConfig;
import com.soleilcode.onepicaday.Configs.AppConfig.ProjectSummary;
import com.soleilcode.onepicaday.Projects.Project;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for file I/O.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final String APP_CONFIG_NAME = "app.config";
    private static final String PROJECT_EXTENSION = ".project";

    private static FileUtils sInstance = null;
    private final Context mApplicationContext;

    public static FileUtils getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new FileUtils(context.getApplicationContext());
        return sInstance;
    }

    private FileUtils(Context context) {
        mApplicationContext = context;
    }

    public String getProjectFileName(String projectId) {
        return projectId + PROJECT_EXTENSION;
    }

    public void deleteAllFiles() {
        String[] filenames = mApplicationContext.fileList();
        for (String filename : filenames) {
            mApplicationContext.deleteFile(filename);
        }
    }

    /**
     * Loads and returns the app config file.
     * If the config does not exist or is unparsable, regenerates it.
     */
    public synchronized AppConfig loadAppConfig() {
        AppConfig config = new AppConfig();
        byte[] bytes = loadBytes(APP_CONFIG_NAME);
        boolean regenerateConfig = false;
        if (bytes == null) {
            regenerateConfig = true;
        } else {
            try {
                config = MessageNano.mergeFrom(config, bytes);
            } catch (InvalidProtocolBufferNanoException e) {
                Log.e(TAG, "Unable to decode app config", e);
                regenerateConfig = true;
            }
        }

        if (regenerateConfig) {
            // Re-create a fresh new config.
            config = new AppConfig();
            saveConfig(config);
        }
        return config;
    }

    /** Saves the app config. Returns true on success. */
    public synchronized boolean saveConfig(AppConfig config) {
        return saveBytes(MessageNano.toByteArray(config), APP_CONFIG_NAME);
    }

    /**
     * Loads a project's {@link Project} proto in serialized form
     * or {@code null} if it can't be found.
     */
    @Nullable
    public synchronized byte[] loadProjectBytes(String projectId) {
        byte[] bytes = loadBytes(getProjectFileName(projectId));
        if (bytes == null){
            Log.e(TAG, "Error loading project file");
        }
        return bytes;
    }

    /** Same as {@link #loadProjectBytes(String)}, but also deserializes the proto. */
    @Nullable
    public synchronized Project loadProject(String projectId) {
        byte[] bytes = loadProjectBytes(projectId);
        if (bytes == null) {
            return null;
        }
        Project project = new Project();
        try {
            project = MessageNano.mergeFrom(project, bytes);
        } catch (InvalidProtocolBufferNanoException e) {
            Log.e(TAG, "Unable to decode project proto buff", e);
        }
        return project;
    }

    /** Saves a project. Returns true on success. */
    public synchronized boolean saveProject(Project project) {
        if (!saveBytes(MessageNano.toByteArray(project), getProjectFileName(project.id))) {
            // Unable to save the project.
            return false;
        }
        // Obtain the latest app config.
        AppConfig config = loadAppConfig();
        // Check if the project is already in the config. If not, add it and save the config.
        boolean hasProject = false;
        for (ProjectSummary summary : config.projects) {
            if (summary.id.equals(project.id)) {
                hasProject = true;
                break;
            }
        }
        if (hasProject) {
            return true;  // we're done
        }
        ProjectSummary[] summaries = new ProjectSummary[config.projects.length + 1];
        System.arraycopy(config.projects, 0, summaries, 0, config.projects.length);
        ProjectSummary summary = new ProjectSummary();
        summary.id = project.id;
        summary.name = project.name;
        summaries[config.projects.length] = summary;
        config.projects = summaries;
        return saveConfig(config);
    }

    @Nullable
    private byte[] loadBytes(String filename)  {
        int size = (int) mApplicationContext.getFileStreamPath(filename).length();
        byte[] bytes = new byte[size];
        try {
            FileInputStream inputStream = mApplicationContext.openFileInput(filename);
            inputStream.read(bytes, 0, bytes.length);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
            return null;
        }
        return bytes;
    }

    /** Saves bytes. Returns true on success. */
    private boolean saveBytes(byte[] bytes, String filename) {
        FileOutputStream fos;
        try {
            fos = mApplicationContext.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing file", e);
            return false;
        }
        return true;
    }
}

package soleilcode.onepicaday;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.soleilcode.onepicaday.Projects.Project;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utilities for file I/O.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    private static final String DIR_NAME = "OnePicADay";
    private static final FileUtils sInstance = new FileUtils();

    public static FileUtils getInstance() {
        return sInstance;
    }

    private FileUtils() {}

    /** Loads a project. */
    @Nullable
    public Project loadProject(String projectName) {
        File file = getFile(getInfoPath() + getProjectFileName(projectName), false);
        if (file == null){
            Log.e(TAG, "Error loading project file");
            return null;
        }
        byte[] bytes = loadBytes(file);
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

    /** Saves a project. */
    public void saveProject(Project project) {
        File file = getFile(getInfoPath() + getProjectFileName(project.name), true);
        if (file == null){
            Log.e(TAG, "Error creating project file");
            return;
        }
        saveBytes(MessageNano.toByteArray(project), file);
    }

    @Nullable
    private byte[] loadBytes(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
            return null;
        }
        return bytes;
    }

    private void saveBytes(byte[] data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            Log.d(TAG, "File saved to: " + file.getPath());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file", e);
        }
    }

    /** Creates a {@link File} where to save or load data. */
    @Nullable
    private File getFile(String filename, boolean createDirIfNeeded) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "External storage is not mounted: " + Environment.getExternalStorageState());
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), DIR_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!createDirIfNeeded) {
                return null;
            }
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + filename);
    }

    private String getProjectFileName(String projectName) {
        return projectName + ".project";
    }

    /** Returns the directory path to the project metadata. */
    private String getInfoPath() {
        return DIR_NAME;
    }

    /** Returns the directory path to the image files for a given project. */
    private String getProjectImagePath(String projectName) {
        return DIR_NAME + "/" + projectName + "/";
    }
}

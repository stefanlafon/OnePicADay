package soleilcode.onepicaday;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.soleilcode.onepicaday.Projects.Project;

/**
 * {@link Activity} used to edit a project's information, e.g. its name.
 */
public class ProjectInfoActivity extends ActionBarActivity {

    public static final String EXTRA_PROJECT = "soleilcode.onepicaday.PROJECT";
    private static final String TAG = "ProjectInfoActivity";
    private FileUtils mfileutFileUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);

        mfileutFileUtils = FileUtils.getInstance(this);

        Project tmpProject = null;
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra(EXTRA_PROJECT);
        if (bytes != null) {
            try {
                tmpProject = Project.parseFrom(bytes);
            } catch (InvalidProtocolBufferNanoException e) {
                Log.e(TAG, "Unable to parse Project proto", e);
            }
        }
        if (tmpProject == null) {
            finish();
        }

        final Project project = tmpProject;

        // Clicking on this button saves the project.
        Button startProjectButton = (Button) findViewById(R.id.start);
        startProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateProject(project)) {
                    mfileutFileUtils.saveProject(project);
                    startActivity(new Intent(ProjectInfoActivity.this, CameraActivity.class));
                }
            }
        });
    }

    /**
     * Returns whether the project's information on the screen is valid (name is non-empty etc...).
     * If valid, then {@code project} is updated with that information.
     */
    private boolean validateProject(Project project) {
        String name = getName();
        if (name.isEmpty()) {
            return false;
        }
        project.name = name;
        return true;
    }

    private String getName() {
        return ((EditText) findViewById(R.id.name)).getText().toString();
    }
}

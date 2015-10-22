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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);

        Project project = null;
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra(EXTRA_PROJECT);
        if (bytes != null) {
            try {
                project = Project.parseFrom(bytes);
            } catch (InvalidProtocolBufferNanoException e) {
                Log.e(TAG, "Unable to parse Project proto", e);
            }
        }
        if (project == null) {
            finish();
        }

        // Clicking on this button saves the project.
        Button startProjectButton = (Button) findViewById(R.id.start);
        startProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Project project = createProject();
                if (project != null) {
                    FileUtils.getInstance().saveProject(project);
                    startActivity(new Intent(ProjectInfoActivity.this, CameraActivity.class));
                }
            }
        });
    }

    /**
     * Validates the project's information (name is non-empty etc...) and returns the corresponding
     * {@link Project}. If the information is invalid, returns {@code null}.
     */
    @Nullable
    private Project createProject() {
        Project project = new Project();
        String name = getName();
        if (name.isEmpty()) {
            return null;
        }
        project.name = name;
        return project;
    }

    private String getName() {
        return ((EditText) findViewById(R.id.name)).getText().toString();
    }
}

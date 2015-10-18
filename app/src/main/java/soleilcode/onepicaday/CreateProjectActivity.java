package soleilcode.onepicaday;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.soleilcode.onepicaday.Projects.Project;

public class CreateProjectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        // Clicking on this button creates the project.
        Button startProjectButton = (Button) findViewById(R.id.start);
        startProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Project project = createProject();
                if (project != null) {
                    FileUtils.getInstance().saveProject(project);
                    startActivity(new Intent(CreateProjectActivity.this, CameraActivity.class));
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

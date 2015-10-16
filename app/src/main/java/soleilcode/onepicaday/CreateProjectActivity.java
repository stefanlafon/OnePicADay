package soleilcode.onepicaday;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                if (validateProjectInfo()) {
                    // TODO: Save project.
                    startActivity(new Intent(CreateProjectActivity.this, CameraActivity.class));
                }
            }
        });
    }

    /**
     * Validates the project's information (name is non-empty etc...).
     *
     * @return true if the information is valid
     */
    private boolean validateProjectInfo() {
        // Project name must be non-empty.
        return !getName().isEmpty();
    }

    private String getName() {
        return ((EditText) findViewById(R.id.name)).getText().toString();
    }
}

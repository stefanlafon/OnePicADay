package soleilcode.onepicaday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.protobuf.nano.MessageNano;
import com.soleilcode.onepicaday.Configs.AppConfig;
import com.soleilcode.onepicaday.Configs.AppConfig.ProjectSummary;
import com.soleilcode.onepicaday.Projects.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private FileUtils mfileutFileUtils;
    private ArrayAdapter<String> mProjectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mfileutFileUtils = FileUtils.getInstance(this);

        // Set up Button Click listeners.
        // Button for creating a new project.
        Button newProjectButton = (Button) findViewById(R.id.new_project);
        newProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewProject();
            }
        });

        // TODO: Remove the line below.
        // FileUtils.getInstance().deleteAllFiles();

        populateProjectList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewProject() {
        // Create a new Project proto with creation timestamp and id.
        Project project = new Project();
        project.id = UUID.randomUUID().toString();
        project.creationTimeMillis = System.currentTimeMillis();
        // Launch the ProjectInfoActivity with that new project.
        Intent intent = new Intent(MainActivity.this, ProjectInfoActivity.class);
        intent.putExtra(ProjectActivity.EXTRA_PROJECT, MessageNano.toByteArray(project));
        startActivity(intent);
    }

    private void populateProjectList() {
        AppConfig config = mfileutFileUtils.loadAppConfig();
        final List<String> projectIds = new ArrayList<String>();
        for (ProjectSummary summary : config.projects) {
            projectIds.add(summary.id);
        }
        mProjectsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                projectIds);

        ListView listView = (ListView) findViewById(R.id.project_list);
        listView.setAdapter(mProjectsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String projectId = mProjectsAdapter.getItem(position);
                byte[] projectBytes = mfileutFileUtils.loadProjectBytes(projectId);
                if (projectBytes == null) {
                    Log.e(TAG, "Unable to load project \"" + projectId + "\"");
                    Toast.makeText(getApplicationContext(),
                            "Unable to load project \"" + projectId + "\"", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                    intent.putExtra(ProjectActivity.EXTRA_PROJECT, projectBytes);
                    startActivity(intent);
                }
            }
        });

    }
}

package soleilcode.onepicaday;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.soleilcode.onepicaday.Projects.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends ActionBarActivity {

    public static final String EXTRA_PROJECT = "soleilcode.onepicaday.PROJECT";

    private static final String TAG = "ProjectActivity";
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

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
        TextView projectName = (TextView) findViewById(R.id.project_name);
        projectName.setText(project.name);
        Button addPhotoCameraButton = (Button) findViewById(R.id.add_photo_camera);
        addPhotoCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectActivity.this, CameraActivity.class));
                // TODO: Really get the result.
            }
        });
        Button addExistingPhotoButton = (Button) findViewById(R.id.add_existing_photo);
        addExistingPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickExistingPhoto();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
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

    private void pickExistingPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case SELECT_PICTURE:
                    List<Uri> imageUris = new ArrayList<Uri>();
                    if (data.getData() != null) {
                        imageUris.add(data.getData());
                    } else if (data.getClipData() != null) {
                        Log.e(TAG, "MULTIPLE");
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            imageUris.add(item.getUri());
                        }
                    }
                    for (Uri uri : imageUris) {
                        Log.e(TAG, "Path:" + uri.getPath());
                    }
                    break;
            }
        }
    }

}

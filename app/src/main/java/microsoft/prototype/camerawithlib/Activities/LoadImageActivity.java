package microsoft.prototype.camerawithlib.Activities;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import microsoft.prototype.braodcastsupport.LoadImage;
import microsoft.prototype.camerawithlib.R;

public class LoadImageActivity extends AppCompatActivity {
    private static final String TAG = LoadImageActivity.class.getSimpleName();

    private ImageView imageView;
    private Button loadButton;
    private TextView textView;
    private EditText editText;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_image);

        this.imageView = (ImageView) findViewById(R.id.image_view);

        this.textView = (TextView) findViewById(R.id.image_folder_text);
        final String folderPath = Environment.getExternalStorageDirectory().toString();
        this.textView.setText(folderPath);

        this.editText = (EditText) findViewById(R.id.load_image_from_text);
        this.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(folderPath) && TextUtils.isEmpty(editText.getText())){}
                editText.setText(folderPath);
                editText.setSelection(editText.getText().length());
            }
        });

        this.loadButton = (Button) findViewById(R.id.load_image_button);
        this.loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    filePath = editText.getText().toString();
                    if (!TextUtils.isEmpty(filePath)) {
                        Bitmap loadedImg = LoadImage.loadBitmap(filePath);
                        if (loadedImg != null) {
                            imageView.setImageBitmap(LoadImage.loadBitmap(filePath));
                        }
                    }
                }
            }
        });
    }
}

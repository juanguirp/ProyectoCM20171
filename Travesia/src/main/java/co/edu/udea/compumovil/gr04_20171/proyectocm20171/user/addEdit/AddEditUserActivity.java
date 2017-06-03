package co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.addEdit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist.Cyclist;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.login.LoginActivity;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.utils.Cifrar;

public class AddEditUserActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_USER = 1;
    private final static int SELECT_PHOTO = 12345;
    // TODO: Rename and change types of parameters
    private String mUsername;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;


    private Button mSaveButton;
    private ImageView mAvatarImage;
    private TextView linkLogin;
    private TextInputEditText mUsernameField;
    private TextInputEditText mPasswordField;
    private TextInputEditText mEmailField;
    private TextInputLayout mUsernameLabel;
    private TextInputLayout mPasswordLabel;
    private TextInputLayout mEmailLabel;


    private String username = null;
    private String password = null;
    private String email = null;
    private String pictureUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        //String userUsername = getIntent().getStringExtra(UsersActivity.EXTRA_USER_ID);
        //String userUsername = null;


        //setTitle(userUsername == null ? "Añadir usuario" : "Editar usuario");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias UI
        mAvatarImage = (ImageView) findViewById(R.id.avatar);
        linkLogin = (TextView) findViewById(R.id.link_login);
        mPasswordField = (TextInputEditText) findViewById(R.id.et_password);
        mEmailField = (TextInputEditText) findViewById(R.id.et_email);
        mUsernameField = (TextInputEditText) findViewById(R.id.et_username);
        mUsernameLabel = (TextInputLayout) findViewById(R.id.til_username);
        mPasswordLabel = (TextInputLayout) findViewById(R.id.til_password);
        mEmailLabel = (TextInputLayout) findViewById(R.id.til_email);
        mSaveButton = (Button) findViewById(R.id.btn_signup);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addEditUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mAvatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //showScreenLogin();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri selectedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            //cerrar el cursor
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
           // mAvatarImage.setBackground(bitmapDrawable);
            //pictureUri = picturePath;

        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void loadUser() {

    }

    private void addEditUser() throws JSONException {
        boolean error = false;

        username = mUsernameField.getText().toString();
        password = mPasswordField.getText().toString();
        email = mEmailField.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        final Cyclist cyclist = new Cyclist();
        Map<String,Boolean> user = new HashMap<String,Boolean>();
        String keyEmail = email.split("@")[0];
        user.put(keyEmail,true);
        cyclist.setUsername(username);
        cyclist.setUser(keyEmail);

        //Cifrar cifrar = new Cifrar();
        //password = cifrar.cifrar(password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Created", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            showSignupError("No se pudo realizar la operación");
                        }

                        mDatabase.child("cyclist").child(email.split("@")[0]).setValue(cyclist,  new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    showSignupError("No se pudo guardar el usuario");
                                } else {
                                    FirebaseMessaging.getInstance().subscribeToTopic("events");
                                    showSignupSuccess("Registro exitoso");
                                    showScreenLogin();
                                }
                            }
                        });

                    }
                });
    }


    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void showSignupSuccess(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void showSignupError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void showScreenLogin()  {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}


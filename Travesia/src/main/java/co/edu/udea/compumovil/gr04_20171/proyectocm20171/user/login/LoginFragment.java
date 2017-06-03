package co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.MapsActivity;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.home.HomeActivity;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.user.addEdit.AddEditUserActivity;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.utils.Cifrar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_LOGIN = 0;
    private static Context cntxofParent;

    //private Session session;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mLoginButton;
    private TextView mRegister;
    private Button mHomeButton;
    private TextInputEditText mMailField;
    private TextInputEditText mPasswordField;
    private ProgressDialog progressDialog;
    private boolean isLogin;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cntxofParent=getActivity();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        // Instancia del FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //instancia de la session
        //session = new Session(getActivity());
        // Referencias UI

        //mHomeButton = (Button) root.findViewById(R.id.homeButton);
        mLoginButton = (Button) root.findViewById(R.id.loginButton);
        mRegister = (TextView) root.findViewById(R.id.registerButton);
        mMailField = (TextInputEditText) root.findViewById(R.id.et_emailLogin);
        mPasswordField = (TextInputEditText) root.findViewById(R.id.et_passwordLogin);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                showRegisterScreen();
            }
        });
    /*
        if (session.loggedin()){
            startActivity(new Intent(getActivity(),HomeActivity.class));
            getActivity().finish();
        }*/

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("signed", "onAuthStateChanged:signed_in:" + user.getUid());
                    showAppointmentsScreen();
                } else {
                    // User is signed out
                    Log.d("signedOut", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        return root;
    }

    private void showRegisterScreen() {
        Log.d("show register form", String.valueOf(AddEditUserActivity.REQUEST_ADD_USER));
        Intent intent = new Intent(getActivity(), AddEditUserActivity.class);
        intent.putExtra("parentActivity",getActivity().getClass().getSimpleName());
        startActivityForResult(intent, AddEditUserActivity.REQUEST_ADD_USER);
    }

    private void loginUser() {

        Log.d(TAG, "Login");

        // Store values at the time of the login attempt.
        String email = mMailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (!validate(email,password)) {
            return;
        }

        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Login);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        if (isLogin){
                            onLoginSuccess();
                        }else {
                            onLoginFailed();
                        }
                    }
                }, 3000);

        //password = Cifrar.cifrar(password);
        Log.d("password ingres cifrada", password);
        // logica para loguearse
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            showLoginError("Error al intentar iniciar sesión");
                        }

                        // ...
                    }
                });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void showLoginError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();

    }

    private void showAppointmentsScreen() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void onLoginFailed() {
//        progressDialog.dismiss();
        // Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mLoginButton.setEnabled(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        mLoginButton.setEnabled(true);
        getActivity().finish();
    }

    public boolean validate(String username, String password) {

        boolean valid = true;

        if (username.isEmpty() ) { //validar si es email : !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            mMailField.setError("Nombre de usuario requerido");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordField.setError("Campo requerido entre 4 y 10 caracteres alfanumericos");
            valid = false;
        }
        return valid;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

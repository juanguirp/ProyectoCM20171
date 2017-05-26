package co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatButton mUpdateBtn;
    private TextInputEditText mUsernameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPhonenumberField;
    private TextInputEditText mCityField;
    private TextInputEditText mAgeField;

    private TextInputLayout mUsernameLabel;
    private TextInputLayout mEmailLabel;
    private TextInputLayout mPhonenumberLabel;
    private TextInputLayout mCityLabel;
    private TextInputLayout mAgeLabel;
    private OnFragmentInteractionListener mListener;

    private String phonenumber = null;
    private String city = null;
    private String age = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mUsernameField = (TextInputEditText) root.findViewById(R.id.et_username);
        mEmailField = (TextInputEditText) root.findViewById(R.id.et_email);
        mPhonenumberField = (TextInputEditText) root.findViewById(R.id.et_phonenumber);
        mCityField = (TextInputEditText) root.findViewById(R.id.et_city);
        mAgeField = (TextInputEditText) root.findViewById(R.id.et_age);

        mUsernameLabel = (TextInputLayout) root.findViewById(R.id.til_username);
        mEmailLabel = (TextInputLayout) root.findViewById(R.id.til_email);
        mPhonenumberLabel = (TextInputLayout) root.findViewById(R.id.til_phonenumber);
        mCityLabel = (TextInputLayout) root.findViewById(R.id.til_city);
        mAgeLabel = (TextInputLayout) root.findViewById(R.id.til_age);

        mUpdateBtn = (AppCompatButton) root.findViewById(R.id.btn_update);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = getUid();
        final String userEmail = getUserEmail();
        ValueEventListener cyclistListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Cyclist cyclist = dataSnapshot.getValue(Cyclist.class);
                if(cyclist.getUsername() != null) {
                    mUsernameField.setText(cyclist.getUsername());
                }
                if (cyclist.getAge() != null){
                    mAgeField.setText(cyclist.getAge());
                }
                if(cyclist.getPhonenumber() != null){
                    mPhonenumberField.setText(cyclist.getPhonenumber());
                }
                if(cyclist.getCity() != null){
                    mCityField.setText(cyclist.getCity());
                }
                mEmailField.setText(userEmail);
                Log.d("email de perfil", cyclist.getUsername());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
           //     Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("cyclist").child(userEmail.split("@")[0]).addValueEventListener(cyclistListener);

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        return root;
    }

    private void updateData() {
        boolean error = false;

        phonenumber = mPhonenumberField.getText().toString();
        city = mCityField.getText().toString();
        age = mAgeField.getText().toString();

        if (TextUtils.isEmpty(phonenumber)) {
            mUsernameLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(city)) {
            mCityLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(age)) {
            mAgeLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        //Cyclist cyclist = new Cyclist();
        //cyclist.setPhonenumber(phonenumber);
        //cyclist.setCity(city);
        //cyclist.setAge(age);
        Map<String, Object> cyclistValues = new HashMap<>();
        cyclistValues.put("phonenumber", phonenumber);
        cyclistValues.put("age", age);
        cyclistValues.put("city", city);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cyclist/" + getUserEmail().split("@")[0], cyclistValues);
        mDatabase.updateChildren(childUpdates);
    }

    private String getUserEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

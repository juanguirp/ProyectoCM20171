package co.edu.udea.compumovil.gr04_20171.proyectocm20171.group;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist.Cyclist;
import co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist.CyclistListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText tvSearch = null;
    CheckableImageButton cimgButton = null;
    private DatabaseReference mDatabase;
    private TextInputEditText mCyclistUsername;
    public ArrayList<Cyclist> cyclists;
    private RecyclerView recyclerView;
    private TextView tvRemove = null;

    String username = null;

    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(String param1, String param2) {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        View root = inflater.inflate(R.layout.fragment_create_group, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.rv_cyclist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCyclistUsername = (TextInputEditText) root.findViewById(R.id.et_cyclistname);

        cyclists = new ArrayList<Cyclist>();

        cimgButton = (CheckableImageButton) root.findViewById(R.id.img_click_search);
        tvRemove = (TextView) root.findViewById(R.id.tv_remove);

        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab_create_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final Group cyclist = new Cyclist();
                Map<String,Boolean> user = new HashMap<String,Boolean>();
                String keyEmail = email.split("@")[0];
                user.put(keyEmail,true);
                cyclist.setUsername(username);
                cyclist.setUser(user);*/
                /*mDatabase.child("group").push().setValue(event, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            showCreateError("Los datos pudieron no haberse guardado.");
                        } else {
                            showCreateSuccess("Se ha guardado exitosamente");
                        }

                    }
                });*/
            }
        });

        cimgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mCyclistUsername.getText().toString();
                ValueEventListener cyclistListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                        if(dataSnapshot.getChildrenCount() == 0){
                            Toast.makeText(getActivity(),"El ciclista no existe", Toast.LENGTH_LONG).show();
                        }else {

                            Map<String, Object> mapCyclist = (Map<String, Object>) dataSnapshot.getValue();
                            String username = (String) mapCyclist.get("username");
                            if(isNotAdded(username)){

                                Cyclist cyclist = new Cyclist();
                                cyclist.setUsername(username);
                                cyclists.add(cyclist);
                                //id = (int) dataSnapshot.getChildrenCount();
                                initializeAdapter();
                                mCyclistUsername.setText("");
                            }else {
                                Toast.makeText(getActivity(),"El ciclista ya se ha añadido", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getActivity(),"No se pudo obtener el ciclista",Toast.LENGTH_LONG).show();
                        System.out.println("No se pudo obtener el ciclista: " + databaseError.getCode());
                    }
                };
                mDatabase.child("cyclist").child(username).addValueEventListener(cyclistListener);
            }
        });

        return root;
    }

    private void showCreateSuccess(String s) {
    }

    private void showCreateError(String s) {
    }

    private boolean isNotAdded(String username) {
        boolean isAdded = true;
        for(Cyclist cyclist: cyclists){
            if(cyclist.getUsername() == username){
                isAdded = false;
                break;
            }
        }
        return isAdded;
    }

    private void initializeAdapter() {
        final CyclistListAdapter cyclistListAdapter = new CyclistListAdapter(cyclists);
        cyclistListAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DemoRecView", "Pulsado el elemento " + recyclerView.getChildAdapterPosition(v));
                int currentId = recyclerView.getChildAdapterPosition(v) + 1;
                //showDetailScreen(currentId);
            }
        });
        recyclerView.setAdapter(cyclistListAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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

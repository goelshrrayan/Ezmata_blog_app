package com.example.vamsi.blogpost.feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class  AccountFragment extends Fragment {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private TextView setupName, setUpMobile;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_id;


    private Button setupBtn;

    //Account Fragment will show the details of the user who was loggedin ,the photo of
    //the user along the mane
    //This feature was not implemented as it was not a part of this assignment
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        setupImage = view.findViewById(R.id.fragment_setup_image);
        setupName = view.findViewById(R.id.fragment_setup_name);
        setUpMobile = view.findViewById(R.id.fragment_setup_mobile);
        setupBtn = view.findViewById(R.id.fragment_setup_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String mobile = task.getResult().getString("mobile");
                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        setUpMobile.setText(mobile);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(getActivity()).
                                setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getActivity(), "firestore Retrieval Error " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

}
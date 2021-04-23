package com.jdcompany.jdmessenger.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.jdcompany.jdmessenger.R;
import com.jdcompany.jdmessenger.data.InfoLoader;
import com.jdcompany.jdmessenger.data.InternetService;
import com.jdcompany.jdmessenger.data.User;
import com.jdcompany.jdmessenger.data.callbacks.CallBackFindUser;

public class FindUserFragment extends Fragment implements View.OnClickListener {

    EditText etFindUserTag;
    Button btnFindUser;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFindUserTag = view.findViewById(R.id.etFindUserTag);
        btnFindUser = view.findViewById(R.id.btnFindUser);

        btnFindUser.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onClick(View v) {
        if(!etFindUserTag.getText().toString().isEmpty()) {
            btnFindUser.setEnabled(false);
            InternetService.getInstance().findUser(etFindUserTag.getText().toString(), new CallBackFindUser() {
                @Override
                public void onUserFound(User user) {
                    InfoLoader.getInstance().addUser(user);
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
                }

                @Override
                public void onUserDoesNotExist() {
                    btnFindUser.setEnabled(true);
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    btnFindUser.setEnabled(true);
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
package com.jdcompany.jdmessenger.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.jdcompany.jdmessenger.R;
import com.jdcompany.jdmessenger.data.network.IncomeMessagesChecker;
import com.jdcompany.jdmessenger.data.network.InternetService;
import com.jdcompany.jdmessenger.data.objects.User;
import com.jdcompany.jdmessenger.data.callbacks.CallBackFindUser;
import com.jdcompany.jdmessenger.database.AppDatabase;
import com.jdcompany.jdmessenger.domain.ImageConverter;
import com.jdcompany.jdmessenger.ui.HomeActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FindUserFragment extends BaseFragment implements View.OnClickListener {

    EditText etFindUserTag;
    Button btnFindUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //find views
        etFindUserTag = view.findViewById(R.id.etFindUserTag);
        btnFindUser = view.findViewById(R.id.btnFindUser);

        //config views
        btnFindUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(!etFindUserTag.getText().toString().isEmpty()) {
            btnFindUser.setEnabled(false);
            InternetService.getInstance().findUser(etFindUserTag.getText().toString(), new CallBackFindUser() {
                @Override
                public void onUserFound(User user) {
                    if(user.getPhoto() != null && !user.getPhoto().equals("")) {
                        ImageConverter imageConverter = new ImageConverter();
                        String photoPath = ((HomeActivity) requireActivity()).storeImage(imageConverter.stringToBitmap(user.getPhoto()));
                        user.setPhoto(photoPath);
                    }
                    compositeDisposable.add(AppDatabase.getInstance(getContext()).userDao().insert(user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() ->{
                                hideKeyboard();
                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();},
                            e-> {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                btnFindUser.setEnabled(true);
                            }));
                }

                @Override
                public void onUserDoesNotExist() {
                    btnFindUser.setEnabled(true);
                    showToastMessage("User not found");
                }

                @Override
                public void onFailure() {
                    btnFindUser.setEnabled(true);
                    showToastMessage("Something went wrong");
                }
            });
        }
    }
}

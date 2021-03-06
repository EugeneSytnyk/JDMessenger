package com.jdcompany.jdmessenger.data.callbacks;

import com.jdcompany.jdmessenger.data.objects.User;

public interface CallBackFindUser {

    void onUserFound(User user);

    void onUserDoesNotExist();

    void onFailure();
}

package com.jdcompany.jdmessenger.data.callbacks;

import com.jdcompany.jdmessenger.data.objects.Message;

import java.util.List;

public interface CallBackMessagesReceived {
    void updateMessages(List<Message> messageList);
}

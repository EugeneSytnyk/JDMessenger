package com.jdcompany.jdmessenger.data;

import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InternetService {
    private InternetApi internetApi;
    private static InternetService internetService;
    private static String BASE_URL = "http://192.168.3.111:8001";
    private ScheduledExecutorService scheduledExecutorService;
    private CallBackUpdateMessages callBackUpdateMessages;
    private User currentUser;

    private InternetService(){}

    public static InternetService getInstance(){
        return internetService;
    }

    public static synchronized void startService(CallBackUpdateMessages callBackUpdateMessages){
        if(internetService == null) {
            internetService = new InternetService();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            internetService.internetApi = retrofit.create(InternetApi.class);
            internetService.currentUser = InfoLoader.getInstance().getCurrentUser();
            internetService.scheduledExecutorService = Executors.newScheduledThreadPool(1);
            internetService.scheduledExecutorService.scheduleWithFixedDelay(internetService::updateMessages, 0, 1, TimeUnit.SECONDS);
            internetService.callBackUpdateMessages = callBackUpdateMessages;
        }
        else throw new IllegalStateException("InternetService is already running");
    }

    public Message sendTextMessage(String text, User destination){
        Message message = new Message();
        message.setBody(text);
        message.setTime(System.currentTimeMillis());
        message.setFromId(currentUser.getId());
        message.setAction("text");
        message.setToId(destination.getId());
        internetApi.sendMessage(message).enqueue(new Callback<CallBackInfo>() {
            @Override
            public void onResponse(Call<CallBackInfo> call, Response<CallBackInfo> response) {
                if(response.body() != null)
                message.setId(response.body().getId());
            }

            @Override
            public void onFailure(Call<CallBackInfo> call, Throwable t) {
                Log.d("EXCEPTION", t.toString());
            }
        });
        return message;
    }

    void updateMessages(){
        try {
            Response<List<Message>> response = internetApi.getMessages(currentUser.getId()).execute();
            List<Message> messages = response.body();
            if(messages != null && messages.size() > 0) callBackUpdateMessages.updateMessages(messages);
        }
        catch(Exception e) {}
    }

}
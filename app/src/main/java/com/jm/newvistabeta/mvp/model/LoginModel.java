package com.jm.newvistabeta.mvp.model;

import android.util.Log;

import com.jm.newvistabeta.bean.UserEntity;
import com.jm.newvistabeta.mvp.base.BaseModel;
import com.jm.newvistabeta.mvp.dao.IDao;
import com.jm.newvistabeta.mvp.dao.UserDao;
import com.jm.newvistabeta.util.NetworkUtil;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Johnny on 1/18/2018.
 */

public class LoginModel extends BaseModel {
    private MyOkHttp myOkHttp;

    public interface LoginCallbackListener {
        void onFinish(String responseMessage);

        void onError(String errorMessage);
    }

    public LoginModel() {
        this.myOkHttp = NetworkUtil.myOkHttp;
    }

    public boolean hasUser() {
        UserDao userDao = new UserDao();
        if (!userDao.getAll().isEmpty()) {
            return true;
        }
        return false;
    }

    public void login(UserEntity userEntity, String serverIp, final LoginCallbackListener loginCallbackListener) {
        final HashMap params = new HashMap();
        params.put("email", userEntity.getEmail());
        params.put("password", userEntity.getPassword());

        final String url = "http://" + serverIp + NetworkUtil.LOG_IN_URL;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myOkHttp.post().url(url).params(params).tag(this).enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        loginCallbackListener.onFinish(response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        loginCallbackListener.onError(error_msg);
                    }
                });
            }
        }).start();
    }

    public void saveUser(UserEntity userEntity) {
        UserDao userDao = new UserDao();
        if (DataSupport.count("userentity") != 0) {
            userDao.deleteAll();
            userDao.save(userEntity);
        } else {
            userDao.save(userEntity);
        }
    }

    public void deleteAllUser() {
        UserDao userDao = new UserDao();
        userDao.deleteAll();
    }

    public UserEntity loadUser() {
        UserDao userDao = new UserDao();
        return userDao.getAll().get(0);
    }

    @Override
    public void cancel() {
        Log.v("cancel()", getClass() + ": Cancel login.");
        myOkHttp.cancel(this);
    }
}

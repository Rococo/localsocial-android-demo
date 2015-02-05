/*
 * Copyright (c) 2001 - 2015 Rococo Software Ltd., 3 Lincoln Place,
 * Dublin 2 Ireland. All Rights Reserved.
 *
 * This software is distributed under licenses restricting its use,
 * copying, distribution, and decompilation. No part of this
 * software may be reproduced in any form by any means without prior
 * written authorization of Rococo Software Ltd. and its licensors, if
 * any.
 *
 * This software is the confidential and proprietary information
 * of Rococo Software Ltd. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of
 * the license agreement you entered into with Rococo Software Ltd.
 * Use is subject to license terms.
 *
 * Rococo Software Ltd. has intellectual property rights relating
 * to the technology embodied in this software. In particular, and
 * without limitation, these intellectual property rights may include
 * one or more patents, or pending patent applications.
 */
package com.localsocial.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.localsocial.LocalSocial;
import com.localsocial.LocalSocialFactory;
import com.localsocial.LoggerFactory;
import com.localsocial.Platform;
import com.localsocial.config.SimpleAppConfiguration;
import com.localsocial.oauth.AccessToken;
import com.localsocial.oauth.OAuthConsumer;
import com.localsocial.oauth.RequestToken;
import com.localsocial.oauth.Verifier;
import com.localsocial.remote.exception.UnauthorizedException;

public abstract class InitLocalSocialActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // *** LocalSocial Config ***
        Platform platform = new Platform();
        platform.setContext(getApplication());
        LoggerFactory.load(platform);
        getSimpleAppConfig().setPlatformContext(platform);
        LocalSocialFactory.setDefaultConfig(getSimpleAppConfig());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() ");
        super.onStart();
        initLocalSocial();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() ");
        setIntent(intent);
        initLocalSocial();
    }

    private static final int DIALOG_FINISH_AUTH_ERROR = 1;

    /**
     * Implement this to do whatever you want after LocalSocial has been initialised
     */
    protected abstract void finishInitLocalSocial();

    /**
     * Implemenmt this to return you LocalSocial app config.
     *
     * @return a simple app config for your app
     */
    protected abstract SimpleAppConfiguration getSimpleAppConfig();

    private void initLocalSocial() {
        Log.d(TAG, "initLocalSocial");
        LocalSocialFactory.setDefaultConfig(getSimpleAppConfig());
        m_localsocial = LocalSocialFactory.getLocalSocial();
        try {
            m_localsocial.loadAccessToken();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }
        if (!authorised()) {
            new AuthoriseTask().execute();
        } else {
            Log.d(TAG, "Authorised, finishing initLocalSocial");
            finishInitLocalSocial();
        }
    }

    private boolean authorised() {
        try {
            return m_localsocial.getAccessToken() != null;
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    private class AuthoriseTask extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected Integer doInBackground(Object... objs) {
            int resp = 1;
            try {
                OAuthConsumer m_consumer = m_localsocial.getOAuthConsumer();
                RequestToken rt = m_consumer.generateRequestToken();

                Verifier v = m_consumer.authorise(rt);
                AccessToken m_access = m_consumer.exchange(rt, v);

                LocalSocial.Credentials creds = LocalSocialFactory.createCredentials(m_access);
                m_localsocial.saveAccessToken(m_access);
                m_localsocial.getConfig().setCredentials(creds);
            } catch (Exception e) {
                Log.e(TAG, "error : " + e.getMessage());
            }
            return resp;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (authorised()) {
                Log.d(TAG, "Auth success");
                initLocalSocial();
            } else {
                Log.d(TAG, "Auth failure");
                showDialog(DIALOG_FINISH_AUTH_ERROR);
            }
        }
    }

    private LocalSocial m_localsocial;
    private static final String TAG = "LocalSocialCustomer/" + LoggerFactory.getClassName(InitLocalSocialActivity.class);
}
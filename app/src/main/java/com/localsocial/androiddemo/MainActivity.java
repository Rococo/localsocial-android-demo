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

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import com.localsocial.*;
import com.localsocial.config.SimpleAppConfiguration;

public class MainActivity extends InitLocalSocialActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected SimpleAppConfiguration getSimpleAppConfig() {
        if (m_sac == null) {
            m_sac = new SimpleAppConfiguration(
                    getString(R.string.ls_config_callback),
                    getString(R.string.ls_config_name),
                    getString(R.string.ls_config_key),
                    getString(R.string.ls_config_secret),
                    null
            );
            String[] scanners = {LocalSocial.TYPE_BLE};
            m_sac.setScannerTypes(scanners);
        }
        return m_sac;
    }

    @Override
    protected void finishInitLocalSocial() {
        Log.d(TAG, "finishInitLocalSocial");
        startActivity(resolveIntent(getIntent()));
        finish();
    }

    Intent resolveIntent(Intent intent) {
        Log.d(TAG, "resolveIntent :: " + intent);
        String action = intent.getAction();
        Log.d(TAG, "action :: " + action);
        Intent next = new Intent(this, PlacesActivity.class);
        next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        next.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return next;
    }

    private SimpleAppConfiguration m_sac = null;
    private static final String TAG = "LocalSocialCustomer/" + LoggerFactory.getClassName(MainActivity.class);
}
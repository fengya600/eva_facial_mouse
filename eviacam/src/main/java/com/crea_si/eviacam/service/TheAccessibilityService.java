/*
 * Enable Viacam for Android, a camera based mouse emulator
 *
 * Copyright (C) 2015-16 Cesar Mauri Loba (CREA Software Systems)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crea_si.eviacam.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.crea_si.eviacam.Analytics;
import com.crea_si.eviacam.EVIACAM;
import com.crea_si.eviacam.Preferences;

/**
 * The Enable Viacam accessibility service
 */
public class TheAccessibilityService
        extends AccessibilityService
        implements ComponentCallbacks, Engine.OnInitListener {

    // reference to the engine
    private AccessibilityServiceModeEngine mEngine;

    // reference to the engine control
    private EngineControl mEngineControl;

    // stores whether it was previously initialized (see comments on init() )
    private boolean mInitialized= false;

    private void init() {
        /* TODO  
         * Check if service has been already started. 
         * Under certain circumstances onUnbind is not called (e.g. running
         * on an emulator happens quite often) and the service continues running
         * although it shows it is disabled this does not solve the issue but at
         * least the service does not crash
         *
         * http://stackoverflow.com/questions/28752238/accessibilityservice-onunbind-not-always-
         * called-when-running-on-emulator
         */
        if (mInitialized) {
            EVIACAM.debug("ALREADY RUNNING");
            return;
        }

        // When preferences are not properly initialized (i.e. is in slave mode)
        // the call will return null. As is not possible to stop the accessibility
        // service just take into account an avoid further actions.
        if (Preferences.initForA11yService(this) == null) return;

        // Init the main engine
        mEngine= MainEngine.getAccessibilityServiceModeEngine();
        mEngine.init(this, this);
    }

    /**
     * Callback for engine initialization completion
     * @param status 0 if initialization completed successfully
     */
    @Override
    public void onInit(int status) {
        if (status != 0) {
            // Initialization failed
            // TODO: provide some feedback
            EVIACAM.debug("Cannot initialize MainEngine in A11Y mode");
            return;
        }

        Analytics.get().trackStartService();

        mInitialized = true;

        mEngineControl= new EngineControl(this, mEngine);
    }

    private void cleanup() {
        // TODO: handle exceptions properly
        if (!mInitialized) return;

        Analytics.get().trackStopService();

        if (mEngineControl!= null) {
            mEngineControl.cleanup();
            mEngineControl= null;
        }

        if (mEngine!= null) {
            mEngine.cleanup();
            mEngine= null;
        }

        if (Preferences.get() != null) {
            Preferences.get().cleanup();
        }

        mInitialized= false;
    }
    
    /**
     * Called when the accessibility service is started
     */
    @Override
    public void onCreate() {
        super.onCreate();
        EVIACAM.debug("onCreate");
    }

    /**
     * Called every time the service is switched ON
     */
    @Override
    public void onServiceConnected() {
        EVIACAM.debug("onServiceConnected");
        init();
    }

    /**
     * Called when service is switched off
     */
    @Override
    public boolean onUnbind(Intent intent) {
        /* TODO: it seems that, at this point, views are already destroyed
         * which might be related with the spurious crashes when switching
         * off the accessibility service. Tested on Nexus 7 Android 5.1.1
         */
        EVIACAM.debug("onUnbind");
        cleanup();
        return false;
    }

    /**
     * Called when service is switched off after onUnbind
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EVIACAM.debug("onDestroy");
        cleanup();
    }

    /**
     * (required) This method is called back by the system when it detects an
     * AccessibilityEvent that matches the event filtering parameters specified
     * by your accessibility service.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (mEngine != null) {
            mEngine.onAccessibilityEvent(event);
        }
    }

    /**
     * (required) This method is called when the system wants to interrupt the
     * feedback your service is providing, usually in response to a user action
     * such as moving focus to a different control. This method may be called
     * many times over the life cycle of your service.
     */
    @Override
    public void onInterrupt() {
        EVIACAM.debug("onInterrupt");
    }


}

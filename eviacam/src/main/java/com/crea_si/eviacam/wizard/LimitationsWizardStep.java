/*
* Enable Viacam for Android, a camera based mouse emulator
*
* Copyright (C) 2015 Cesar Mauri Loba (CREA Software Systems)
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
package com.crea_si.eviacam.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crea_si.eviacam.R;
import com.crea_si.eviacam.service.AccessibilityServiceModeEngine;
import com.crea_si.eviacam.service.MainEngine;

import org.codepond.wizardroid.WizardStep;

public class LimitationsWizardStep extends WizardStep {

    // You must have an empty constructor for every step
    public LimitationsWizardStep() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wizard_step_limitations, container, false);
    }

    @Override
    public void onEnter() {
        AccessibilityServiceModeEngine engine = MainEngine.getAccessibilityServiceModeEngine();
        engine.disableClick();
        engine.disableDockPanel();
        engine.disablePointer();
        engine.disableScrollButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        WizardUtils.checkEngineAndFinishIfNeeded(getActivity());
    }
}

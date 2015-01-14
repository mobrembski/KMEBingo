package com.mobrembski.kmeviewer;

import java.util.EventListener;

public interface ControllerEvent extends EventListener {
    public void onConnectionStopping();
    public void onConnectionStarting();
}

package com.mobrembski.kmeviewer;

import java.util.Observable;
import java.util.Observer;

public class KmeInfoTab extends KMEViewerTab implements Observer {

    public KmeInfoTab() {
        this.layoutId = R.layout.kmeinfotab;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

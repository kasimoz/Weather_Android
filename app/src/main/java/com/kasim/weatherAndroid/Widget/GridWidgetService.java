package com.kasim.weatherAndroid.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}


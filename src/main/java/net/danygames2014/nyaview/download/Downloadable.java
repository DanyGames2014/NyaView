package net.danygames2014.nyaview.download;

import net.danygames2014.nyaview.NyaView;

import java.net.MalformedURLException;
import java.net.URL;

public interface Downloadable {
    default URL getUrl() {
        try {
            return new URL(getUrlString());
        } catch (MalformedURLException e) {
            NyaView.LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    String getUrlString();
}

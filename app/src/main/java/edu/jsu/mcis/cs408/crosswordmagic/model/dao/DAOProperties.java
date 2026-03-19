package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.Context;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import edu.jsu.mcis.cs408.crosswordmagic.R;

public class DAOProperties {

    private static final Properties PROPERTIES = new Properties();

    private final String prefix;

    DAOProperties(Context context, String prefix) {

        InputStreamReader file = new InputStreamReader(context.getResources().openRawResource(R.raw.dao));

        try {
            PROPERTIES.load(file);
        }
        catch (Exception e) { e.printStackTrace(); }

        this.prefix = prefix;

    }

    String getProperty(String key) {
        
        String fullKey = prefix + "." + key;
        String property = PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0)
            property = null;

        return property;
        
    }

}
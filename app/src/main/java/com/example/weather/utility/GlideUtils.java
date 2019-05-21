package com.example.weather.utility;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class GlideUtils {
    private static String URL = "https://darksky.net/images/weather-icons/";

    public static void loadIconDarkSky(Context context, ImageView imageView, String iconName) {
        Glide.with(context).load(URL + iconName + ".png").into(imageView);
    }
}

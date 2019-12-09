package com.lemons.fruit.service;

import com.lemons.fruit.model.GameTheme;
import com.lemons.fruit.model.GameThemeDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Path;

/**
 * A simple HTTP based service that uses Retrofit
 *
 * Created by lemonearn on 12/12/16.
 */

public interface ThemeService {

	Call<List<GameTheme>> getThemes();

	Call<GameThemeDetails> getTheme(@Path("color") String color);
}

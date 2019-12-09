package com.lemons.fruit.theme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import com.lemons.fruit.R;
import com.lemons.fruit.model.GameThemeDetails;
import com.lemons.fruit.utils.Preferences;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * An adapter for driving a ListView of GameThemeDetails
 *
 * Created by arahn on 12/13/16.
 */
public class ThemeAdapter extends ArrayAdapter<GameThemeDetails> {
	public ThemeAdapter(Activity activity) {
		super(activity, R.layout.theme_item);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		ThemeViewHolder view = (ThemeViewHolder) convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = (ThemeViewHolder) inflater.inflate(R.layout.theme_item, parent, false);
			view.bindViews();
		}
		GameThemeDetails theme = getItem(position);
		if (theme != null) {
			ImageView iconView = view.mIconView;
			String url = theme.url;

			// FIXME : Use Glide to load that url into iconView
			Glide.with(getContext()).load(url).into(iconView);

			TextView themeInfoView = view.mInfoView;
			themeInfoView.setText(theme.info);

			TextView themeNameView = view.mNameView;
			themeNameView.setText(theme.name);

			ImageView checkedView = view.mCheckedView;
			Boolean isCurrentTheme = Preferences.isSelectedTheme(theme);
			if (isCurrentTheme) {
				checkedView.setVisibility(VISIBLE);
			} else {
				checkedView.setVisibility(GONE);
			}
		}
		return view;
	}

}

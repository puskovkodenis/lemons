package com.lemons.fruit;

import android.os.Bundle;


import androidx.fragment.app.Fragment;

import com.lemons.fruit.model.GameState;


public class GameStateFragment extends Fragment {

	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	protected static final String ARG_GAME_STATE = "gameState";

	protected GameState mGameState;

	public void setArguments(GameState gameState) {
		Bundle args = new Bundle();
		args.putParcelable(ARG_GAME_STATE, gameState);
		setArguments(args);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mGameState = getArguments().getParcelable(ARG_GAME_STATE);
		}
	}
}

package com.lemons.fruit;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.lemons.fruit.model.GameState;
import com.lemons.fruit.model.IGameState;

import java.text.NumberFormat;



public class DoneSellingFragment extends GameStateFragment {
	protected static final String ARG_NEW_EARNINGS_PARAM = "newEarnings";
	protected static final String ARG_WASTED_GLASSES_PARAM = "wastedGlasses";

	private OnFragmentInteractionListener mListener;
	private int mNewEarnings;
	private int mWastedGlassesOfLemonade;

	public DoneSellingFragment() {
		// Required empty public constructor
	}

	public static DoneSellingFragment newInstance(GameState gameState, int newEarnings, int wastedGlasses) {
		DoneSellingFragment fragment = new DoneSellingFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_GAME_STATE, gameState);
		args.putInt(ARG_NEW_EARNINGS_PARAM, newEarnings);
		args.putInt(ARG_WASTED_GLASSES_PARAM, wastedGlasses);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mNewEarnings = getArguments().getInt(ARG_NEW_EARNINGS_PARAM);
			mWastedGlassesOfLemonade = getArguments().getInt(ARG_WASTED_GLASSES_PARAM);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_done_selling, container, false);
		Button restUpButton = (Button) view.findViewById(R.id.doneSellingButton);

		TextView moneyEarnedView = (TextView) view.findViewById(R.id.doneSellingTitle);
		Resources res = getResources();
		NumberFormat format = NumberFormat.getCurrencyInstance();

		String dollarString = format.format((double) mNewEarnings / 100.0);
		String glassesString = String.format(res.getString(R.string.done_selling_title), dollarString);
		moneyEarnedView.setText(glassesString);

		int money = mGameState.getMoney();
		String newEarningsDollarString = format.format((double) money / 100.0);
		TextView newEarningsDetailsView = (TextView) view.findViewById(R.id.doneSellingDetails);
		if (money < 10000) {
			String newEarningsString = String.format(res.getString(R.string.done_selling_details), newEarningsDollarString, mWastedGlassesOfLemonade);
			newEarningsDetailsView.setText(newEarningsString);
			restUpButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					restUpButtonPressed();
				}
			});
		}
		else {
			String newEarningsString = String.format(res.getString(R.string.done_selling_you_won), newEarningsDollarString);
			newEarningsDetailsView.setText(newEarningsString);
			restUpButton.setText(R.string.play_again_button);
			restUpButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					restartGameButtonPushed();
				}
			});
		}

		return view;
	}

	public void restUpButtonPressed() {
		if (mListener != null) {
			mListener.restUp(mGameState);
		}
	}

	public void restartGameButtonPushed() {
		if (mListener != null) {
			mListener.restartGame();
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	public interface OnFragmentInteractionListener {
		void restUp(IGameState gameState);
		void restartGame();
	}
}

package com.lemons.fruit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lemons.fruit.service.GameSetupService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelloFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelloFragment extends Fragment {
	private static final String LAST_HIGH_SCORE_PARAM = "lastHighScoreParam";

	private int mLastHighScore;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param lastHighScore the last high score, in cents
	 * @return A new instance of fragment HelloFragment.
	 */
	public static HelloFragment newInstance(int lastHighScore) {
		HelloFragment fragment = new HelloFragment();
		Bundle args = new Bundle();
		args.putInt(LAST_HIGH_SCORE_PARAM, lastHighScore);
		fragment.setArguments(args);
		return fragment;
	}

	public HelloFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mLastHighScore = getArguments().getInt(LAST_HIGH_SCORE_PARAM);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.fragment_hello, container, false);

		if (mLastHighScore > 0) {
			TextView highScoreView = (TextView) result.findViewById(R.id.beatHighScoreTextView);
			Resources res = getResources();
			String highScoreMessage = String.format(res.getString(R.string.beat_high_score), (double) mLastHighScore / 100.0);
			highScoreView.setText(highScoreMessage);
		}

		Button startGameButton = (Button) result.findViewById(R.id.startGameButton);
		startGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onStartGameButtonPushed(view);
			}
		});

		return result;
	}

	@Override
	public void onPause() {
		super.onPause();

		ImageView blinkyView = (ImageView) getView().findViewById(R.id.imageView);
		Drawable drawable = blinkyView.getDrawable();
		if (drawable instanceof AnimationDrawable) {
			AnimationDrawable anim = (AnimationDrawable) drawable;
			anim.stop();
		}
	}

	public void onResume() {
		super.onResume();
		
		ImageView blinkyView = (ImageView) getView().findViewById(R.id.imageView);
		AnimationDrawable anim = (AnimationDrawable) blinkyView.getDrawable();
		anim.start();

		if (mLastHighScore == 0) {
			GameSetupService setupService = new GameSetupService();
			setupService.fetchHighScore(new GameSetupService.HighScoreCallback() {
				@Override
				public void onHighScore(int highScore) {
					TextView highScoreView = (TextView) getView().findViewById(R.id.beatHighScoreTextView);
					Resources res = getResources();
					String highScoreMessage = String.format(res.getString(R.string.beat_high_score), (double) highScore / 100.0);
					highScoreView.setText(highScoreMessage);
					mLastHighScore = highScore;
				}
			});
		}
	}

	public void onStartGameButtonPushed(View v) {
		if (mListener != null) {
			mListener.startGame();
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

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void startGame();
	}
}

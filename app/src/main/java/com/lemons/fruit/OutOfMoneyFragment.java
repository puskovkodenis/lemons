package com.lemons.fruit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class OutOfMoneyFragment extends Fragment {

	private OnFragmentInteractionListener mListener;


	public static OutOfMoneyFragment newInstance() {
		OutOfMoneyFragment fragment = new OutOfMoneyFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public OutOfMoneyFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.fragment_out_of_money, container, false);

		Button playAgainButton = (Button) result.findViewById(R.id.playAgainButton);
		playAgainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onPlayAgainButtonPushed(view);
			}
		});

		return result;
	}

	public void onPlayAgainButtonPushed(View v) {
		if (mListener != null) {
			mListener.playAgain();
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
		void playAgain();
	}
}

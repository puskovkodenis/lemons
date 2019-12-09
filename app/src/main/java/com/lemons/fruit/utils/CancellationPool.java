package com.lemons.fruit.utils;

import java.util.ArrayList;

/**
 * Collects Cancellable objects and cancels them all when the pool is cancelled.
 *
 * Created by lemonearn on 12/10/16.
 */

public class CancellationPool {
	private ArrayList<Cancellable> mCancellables = new ArrayList<>();

	public void addCancellable(Cancellable cancellable) {
		mCancellables.add(cancellable);
	}

	public void cancel() {
		for (Cancellable cancellable: mCancellables) {
			cancellable.cancel();
		}
		mCancellables.clear();
	}
}

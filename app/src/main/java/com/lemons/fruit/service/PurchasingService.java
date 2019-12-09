package com.lemons.fruit.service;

import android.os.Handler;
import android.os.Looper;

import com.lemons.fruit.model.Accessor;
import com.lemons.fruit.model.GameGroceries;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides APIs for purchasing lemonade ingredients.
 * The APIs are asynchronous, calling an instance of IPurchaseCompletion.result() upon completion.
 * This Service is implemented using standard Java APIs, except in how the message is
 * passed back to the main thread.
 *
 * Created by lemonearn on 12/10/16.
 */

public class PurchasingService implements IPurchasingService {

	public static int defaultCalculatePriceForGroceries(GameGroceries quantities, final GameGroceries prices) {
		Accessor[] accessors = new Accessor[] { new Accessor.Lemon(), new Accessor.Sugar(), new Accessor.Ice() };

		int totalPrice = 0;
		for (Accessor accessor: accessors) {
			totalPrice += accessor.get(prices) * accessor.get(quantities);
		}
		return totalPrice;
	}

	public int calculatePriceForGroceries(GameGroceries quantities, final GameGroceries prices) {
		return PurchasingService.defaultCalculatePriceForGroceries(quantities, prices);
	}

	/**
	 *
	 * @param quantities How much of each item to purchase
	 * @param money How much money is being spent
	 * @param prices The prices of the items
	 * @param completion Callback when completed
	 */
	public void purchaseGroceries(final GameGroceries quantities, final int money, final GameGroceries prices, final IPurchaseCompletion completion) {
		new Timer().schedule(
				new TimerTask() {
					@Override
					public void run() {
						new Thread(new Runnable() {
							@Override
							public void run() {
								final boolean result = runPurchaseSync(quantities, money, prices, completion);

								Handler uiHandler = new Handler(Looper.getMainLooper());
								uiHandler.post(new Runnable() {
									@Override
									public void run() {
										completion.result(result);
									}
								});
							}
						}).start();
					}
				},
				1000
		);
	}

	private boolean runPurchaseSync(final GameGroceries quantities, final int money, final GameGroceries prices, final IPurchaseCompletion completion) {
		return money == calculatePriceForGroceries(quantities, prices);
	}
}

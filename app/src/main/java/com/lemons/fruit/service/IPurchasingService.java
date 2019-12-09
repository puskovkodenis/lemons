package com.lemons.fruit.service;

import com.lemons.fruit.model.GameGroceries;

/**
 * Abstraction of what it means to be a purchasing service
 *
 * Created by lemonearn on 12/13/16.
 */

public interface IPurchasingService {
	int calculatePriceForGroceries(GameGroceries quantities, final GameGroceries prices);
	void purchaseGroceries(final GameGroceries quantities, final int money, final GameGroceries prices, final IPurchaseCompletion completion);

	interface IPurchaseCompletion {
		void result(boolean success);
	}
}

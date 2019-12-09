package com.lemons.fruit.service;

import com.lemons.fruit.model.GameState;
import com.lemons.fruit.utils.Cancellable;

/**
 * Abstraction around SellingService, primarily for Testing
 */
public interface ISellingService {
	Cancellable sellLemonade(GameState gameState, int price, final SellingService.SellingCallback callback);
}

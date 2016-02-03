package com.delmesvk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ApiCallActivity extends AppCompatActivity{

    private VKRequest myRequest;
	RecyclerView.Adapter mAdapter;
	RecyclerView.LayoutManager mLayoutManager;
    private static final String FRAGMENT_TAG = "response_view";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



		setContentView(R.layout.activity_api_call);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);



		int fallback = 0;
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(fallback)
				.showImageOnFail(fallback)
				.showImageOnLoading(fallback).build();
//		if (getSupportActionBar() != null) {
//
//			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//			getSupportActionBar().setDisplayUseLogoEnabled(true);
//			getSupportActionBar().setDisplayShowCustomEnabled(true);
//			getSupportActionBar().setDisplayShowTitleEnabled(false);
//		}

		Toolbar myToolbar;
		myToolbar = (Toolbar) findViewById(R.id.app_toolbar);
		setTitle("DDIT_Results");

		setSupportActionBar(myToolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment(), FRAGMENT_TAG)
					.commit();
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.app_toolbar, new PlaceholderFragment(), FRAGMENT_TAG)
//					.commit();
			processRequestIfRequired();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.action_refresh:

				Toast.makeText(this, "sdfgsdgf", Toast.LENGTH_LONG).show();
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}
	private PlaceholderFragment getFragment() {
        return (PlaceholderFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }
    private void processRequestIfRequired() {
        VKRequest request = null;

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("request")) {
            long requestId = getIntent().getExtras().getLong("request");
            request = VKRequest.getRegisteredRequest(requestId);
            if (request != null)
                request.unregisterObject();
        }

        if (request == null) return;
        myRequest = request;
        request.executeWithListener(mRequestListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("response", mLayoutManager.onSaveInstanceState());

		if (myRequest != null) {
            outState.putLong("request", myRequest.registerObject());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        long requestId = savedInstanceState.getLong("request");
        myRequest = VKRequest.getRegisteredRequest(requestId);
        if (myRequest != null) {
            myRequest.unregisterObject();
            myRequest.setRequestListener(mRequestListener);
        }
    }
	protected void setResponseData(Object data) {
		PlaceholderFragment fragment = getFragment();



		mLayoutManager = new LinearLayoutManager(this);

        fragment.listView.setLayoutManager(mLayoutManager);

		registerForContextMenu(fragment.listView);

		MessegeItem mItem = new MessegeItem();


		Toolbar myToolbar;
		myToolbar = (Toolbar) findViewById(R.id.app_toolbar);
		setTitle("DDIT_Results");

	    setSupportActionBar(myToolbar);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);



		if (fragment != null && fragment.listView != null) {



			if (data instanceof VKApiGetMessagesResponse){
				int index = 0;
				for (VKApiMessage i:((VKApiGetMessagesResponse) data).items){
				if (TestActivity.uservs != null)
				   for (VKApiUserFull u: TestActivity.uservs){
				   	 if (u.id == i.user_id) {



						 Date d1 = new Date(i.date * 1000);
						 SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm");
						 mItem.messagesList.add(index, i.body);
						 mItem.senderList.add(index, u.first_name + " " + u.last_name + " " + format1.format(d1));
						 mItem.ImageUrlList.add(index, u.getPhoto_100());
						 index++;
				   	 }
				   }
					else
						{	mItem.messagesList.add(index,  i.body);
							mItem.senderList.add(index,"" );
							index++;
						}

			}
			}

			if (data instanceof VKApiGetDialogResponse){
				int index = 0;
				for (VKApiDialog i:((VKApiGetDialogResponse) data).items){
					if (TestActivity.uservs != null)
						for (VKApiUserFull u: TestActivity.uservs){
							if (u.id == i.message.user_id) {
								Date d2 = new Date(i.message.date * 1000);
								SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy hh:mm");

								mItem.messagesList.add(index, i.message.body);
								mItem.senderList.add(index, u.first_name + " " + u.last_name + " " + format2.format(d2) );
								mItem.ImageUrlList.add(index, u.getPhoto_100());
								index++;
							}
						}
					else
					{	mItem.messagesList.add(index,  i.message.body);
						mItem.senderList.add(index,"" );
						index++;
					}
				}
			}

//			Collections.reverse(messagesList);
//			Collections.reverse(senderList);
//			Collections.reverse(ImageUrlList);


			mAdapter = new RecyclerViewAdapter(mItem);
			fragment.listView.setAdapter(mAdapter);
		}
	}


	VKRequestListener mRequestListener = new VKRequestListener() {
		@Override
		public void onComplete(VKResponse response) {
			setResponseData(response.parsedModel);
		}

		@Override
		public void onError(VKError error) {
			//setResponseData(error.toString());
		}

		@Override
		public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded,
		                       long bytesTotal) {
			// you can show progress of the request if you want
		}

//		@Override
//		public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
//			getFragment().textView.append(
//					String.format("Attempt %d/%d failed\n", attemptNumber, totalAttempts));
//		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myRequest.cancel();
		Log.d(VKSdk.SDK_TAG, "On destroy");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public RecyclerView listView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_api_call, container, false);
			listView = (RecyclerView) v.findViewById(R.id.response);
			registerForContextMenu(listView);
			return v;
		}
	}
}
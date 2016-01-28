package com.delmesvk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ApiCallActivity extends ActionBarActivity {

    private VKRequest myRequest;
	RecyclerView.Adapter mAdapter;
	RecyclerView.LayoutManager mLayoutManager;
    private static final String FRAGMENT_TAG = "response_view";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_api_call);

		if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment(), FRAGMENT_TAG)
					.commit();
			processRequestIfRequired();
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

		final ArrayList<String> messagesList = new ArrayList<String>();
		final ArrayList<String> senderList = new ArrayList<String>();
		final ArrayList<String> ImageUrlList = new ArrayList<String>();

		mAdapter = new RecyclerViewAdapter(messagesList,senderList,ImageUrlList);




		if (fragment != null && fragment.listView != null) {



			if (data instanceof VKApiGetMessagesResponse){
				int index = 0;
				for (VKApiMessage i:((VKApiGetMessagesResponse) data).items){
				if (TestActivity.uservs != null)
				   for (VKApiUserFull u: TestActivity.uservs){
				   	 if (u.id == i.user_id) {



						 Date d1 = new Date(i.date * 1000);
						 SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm");
				   	 	 messagesList.add(index, i.body);
						 senderList.add(index, u.first_name + " " + u.last_name + " " + format1.format(d1));
						 ImageUrlList.add(index, u.getPhoto_100());
						 index++;
				   	 }
				   }
					else
						{	messagesList.add(index,  i.body);
							senderList.add(index,"" );
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

								messagesList.add(index, i.message.body);
								senderList.add(index, u.first_name + " " + u.last_name + " " + format2.format(d2) );
								ImageUrlList.add(index, u.getPhoto_100());
								index++;
							}
						}
					else
					{	messagesList.add(index,  i.message.body);
						senderList.add(index,"" );
						index++;
					}
				}
			}

//			Collections.reverse(messagesList);
//			Collections.reverse(senderList);
//			Collections.reverse(ImageUrlList);

			fragment.listView.setItemAnimator(new DefaultItemAnimator());

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

			return v;
		}
	}
}
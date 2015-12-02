package team6.iguide;

/***
 iGuide
 Copyright (C) 2015 Cameron Mace

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import team6.iguide.IssueDataModel.IssueMarker;

public class CampusIssueInfoWindow extends InfoWindow {
    // This class is used to handle what happens when an issue marker is clicked.

    private Context mContext;
    String title;
    String ref;
    String shortTitle;
    String shortRef;

    public CampusIssueInfoWindow(final Context context, final Activity activity, final MapView mv, final List<IssueMarker> issueInfo, final int position) {
        super(R.layout.campus_issue_info_window, mv);
        this.mContext = context;

        // We clip the layout so it displays rounded corners. This however only works with API 21
        // and higher.
        if(Build.VERSION.SDK_INT >= 21) {
            getView().findViewById(R.id.mainpanal).setClipToOutline(true);
            mView.setElevation(24);
        }

        ImageButton routing = (ImageButton)mView.findViewById(R.id.routing_button);
        routing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Build the alert dialog that displays when resolve issue is clicked
                AlertDialog.Builder resolveIssueConfirmation = new AlertDialog.Builder(activity);
                resolveIssueConfirmation.setTitle("Resolve Issue?");
                resolveIssueConfirmation.setMessage("This issue will be marked as resolved and disappear from the map permanently. Only resolve if you personally fixed the issue or know someone who did.");
                resolveIssueConfirmation.setPositiveButton("Resolve Issue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(MainActivity.mapContainer, "Issue resolved", Snackbar.LENGTH_LONG).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(MainActivity.mapContainer, "Long press to add an issue at that location", Snackbar.LENGTH_INDEFINITE).setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Handle click here
                                            }
                                        }).show();
                                    }
                                }, 5000);

                                resolveIssue(issueInfo.get(position).getPid());
                                close();
                            }
                        });
                resolveIssueConfirmation.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do action when dialog is canceled, in our case, we don't have
                                // to do anything
                            }
                        });
                // Show the alert dialog
                resolveIssueConfirmation.show();
            }
        });


        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    Bundle bundle = new Bundle();
                    bundle.putString("TITLE", title);
                    bundle.putString("DESCRIPTION", ref);
                    bundle.putString("TYPE", issueInfo.get(position).getType());
                    bundle.putString("STATUS", issueInfo.get(position).getStatus());
                    bundle.putString("ROOM", issueInfo.get(position).getRoom());
                    bundle.putString("CREATEDAT", issueInfo.get(position).getCreatedAt());
                    bundle.putString("UPDATEDAT", issueInfo.get(position).getUpdatedAt());
                    bundle.putInt("PID", issueInfo.get(position).getPid());

                    Intent intent = new Intent(mView.getContext(), CampusIssueDetailActivity.class);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);

                    // Still close the InfoWindow though
                    close();
                }

                // Return true as we're done processing this event
                return true;
            }
        });
    }

    @Override
    public void onOpen(Marker overlayItem) {
        title = overlayItem.getTitle();
        ref = overlayItem.getDescription();

        // Check string's length and if longer then 25 characters, we shorten
        if(title.length() > 25){
            shortTitle = title.substring(0,24)+"...";
            ((TextView) mView.findViewById(R.id.title)).setText(shortTitle);
        }else ((TextView) mView.findViewById(R.id.title)).setText(title);

        if(ref.length() > 25){
            shortRef = ref.substring(0,24)+"...";
            ((TextView) mView.findViewById(R.id.ref)).setText(shortRef);
        } else ((TextView) mView.findViewById(R.id.ref)).setText(ref);



    }

    public void resolveIssue(int pid){
        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);

        String Url = "http://iguide.heliohost.org/delete_marker.php?pid=" + pid;

        StringRequest req = new StringRequest(Request.Method.POST, Url,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.v("issue.Volley", "Volley Response: " + response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(MainActivity.mapContainer, "Error occurred resolving issue", Snackbar.LENGTH_LONG).show();
                Log.e("issue.Volley", "onErrorResponse: ", error);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(MainActivity.mapContainer, "Long press to add an issue at that location", Snackbar.LENGTH_INDEFINITE).setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle click here
                            }
                        }).show();
                    }
                }, 5000);

            }
        });
        mRequestQueue.add(req);

        // This allows volley to retry request if for some reason it times out the first time
        // More info can be found in this question:
        // http://stackoverflow.com/questions/17094718/android-volley-timeout
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }
}

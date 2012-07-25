package de.akquinet.android.tools.intentflags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.akquinet.android.tools.intentflags.util.FlagUtil;
import de.akquinet.android.tools.intentflags.util.ToastUtil;


public abstract class BaseActivity extends Activity
{
    private static final String SENT_FLAGS = "sent_flags";
    private static final String EXTRA_ACTIVITY_UNIQUEID =
            "extra_activity_uniqueid";

    private static Map<String, Integer> nextIdMap =
            new HashMap<String, Integer>();

    private final String uniqueId;
    private String activityResult = "-";

    public BaseActivity() {
        synchronized (nextIdMap) {
            String className = getClass().getSimpleName();
            Integer id = nextIdMap.get(className);
            if (id == null) {
                id = 0;
            }
            this.uniqueId =
                    getClass().getSimpleName() + "_" + id;
            nextIdMap.put(className, id + 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        setResult(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Do everything in onResume to avoid showing briefly wrong task info on
         * back button press
         */

        this.setContentView(R.layout.main);
        findViewById(R.id.rootView).setVisibility(View.INVISIBLE);

        // Set listeners for activity start buttons

        Button buttonStartActivity1 =
                (Button) findViewById(R.id.buttonStartActivity1);
        Button buttonStartActivity2 =
                (Button) findViewById(R.id.buttonStartActivity2);
        Button buttonStartActivity3 =
                (Button) findViewById(R.id.buttonStartActivity3);
        Button buttonStartActivity4 =
                (Button) findViewById(R.id.buttonStartActivity4);
        Button buttonStartActivity5 =
                (Button) findViewById(R.id.buttonStartActivity5);
        Button buttonStartActivity6 =
                (Button) findViewById(R.id.buttonStartActivity6);
        Button buttonStartActivity7 =
                (Button) findViewById(R.id.buttonStartActivity7);
        Button buttonStartActivity8 =
                (Button) findViewById(R.id.buttonStartActivity8);
        Button buttonStartActivity9 =
                (Button) findViewById(R.id.buttonStartActivity9);
        Button buttonReturn =
                (Button) findViewById(R.id.buttonReturn);
        Button buttonReturnResult =
                (Button) findViewById(R.id.buttonReturnResult);

        buttonStartActivity1.setOnClickListener(
                new LaunchButtonClickListener(Activity1.class));
        buttonStartActivity2.setOnClickListener(
                new LaunchButtonClickListener(Activity2.class));
        buttonStartActivity3.setOnClickListener(
                new LaunchButtonClickListener(Activity3.class));

        buttonStartActivity4.setOnClickListener(
                new LaunchButtonClickListener(Activity4.class, true));
        buttonStartActivity5.setOnClickListener(
                new LaunchButtonClickListener(Activity5.class, true));
        buttonStartActivity6.setOnClickListener(
                new LaunchButtonClickListener(Activity6.class, true));

        buttonStartActivity7.setText("Image");
        buttonStartActivity7
                .setOnClickListener(
                new LaunchButtonClickListener(
                new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                true));

        // open URL in browser
        buttonStartActivity8.setText("Browser");
        buttonStartActivity8.setOnClickListener(
                new LaunchButtonClickListener(
                new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.google.com/"))));

        // send email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { "example@nonexistant.local" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "message body");
        buttonStartActivity9.setText("Email");
        buttonStartActivity9.setOnClickListener(new LaunchButtonClickListener(
                emailIntent));

        buttonReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        buttonReturnResult.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_ACTIVITY_UNIQUEID, uniqueId);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        /*
         * Wait for a short delay before updating activity/task/flags info. If
         * we don't do that, the task infos we get from ActivityManager will be
         * incorrect.
         */
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                updateInfo();
                findViewById(R.id.rootView).setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultCodeStr = "" + resultCode;
        if (resultCode == RESULT_OK) {
            resultCodeStr = "RESULT_OK";
        }
        else if (resultCode == RESULT_CANCELED) {
            resultCodeStr = "RESULT_CANCELED";
        }

        if (data != null) {
            if (data.hasExtra(EXTRA_ACTIVITY_UNIQUEID)) {
                resultCodeStr = data.getStringExtra(EXTRA_ACTIVITY_UNIQUEID)
                        + ": " + resultCodeStr;
            }
            if (data.getData() != null) {
                resultCodeStr = resultCodeStr + ", " + data.getDataString();
            }
        }

        ToastUtil.showToast(
                this, uniqueId + ":\nGot activity result code "
                + resultCodeStr);
        this.activityResult = resultCodeStr;
    }

    private void updateInfo() {
        ((TextView) findViewById(R.id.activityId)).setText(uniqueId);
        ((TextView) findViewById(R.id.activityResult))
                .setText(activityResult);

        ActivityManager manager =
                (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        RunningTaskInfo taskInfo = manager.getRunningTasks(1).get(0);

        TextView taskDescription =
                (TextView) findViewById(R.id.taskDescription);
        if (taskInfo.description != null) {
            taskDescription.setText("" + taskInfo.description);
        }
        else {
            findViewById(R.id.textTaskDescription).setVisibility(View.GONE);
            taskDescription.setVisibility(View.GONE);
        }
        ImageView taskState = (ImageView) findViewById(R.id.taskState);
        if (taskInfo.thumbnail != null) {
            taskState.setImageBitmap(taskInfo.thumbnail);
        }
        else {
            findViewById(R.id.textTaskState).setVisibility(View.GONE);
            taskState.setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.taskNumActivities))
                .setText("" + taskInfo.numActivities);
        ((TextView) findViewById(R.id.taskNumRunning))
                .setText("" + taskInfo.numRunning);
        ((TextView) findViewById(R.id.taskBaseActivity))
                .setText("" + taskInfo.baseActivity.getShortClassName());
        ((TextView) findViewById(R.id.taskTopActivity))
                .setText("" + taskInfo.topActivity.getShortClassName());

        if (!getClass().getName().equals(BootstrapActivity.class.getName())) {
            inflateSentFlagsIntoView();
        }
        inflateCurrentFlagsIntoView();
    }

    private void inflateSentFlagsIntoView() {
        Intent intent = getIntent();
        String sentFlags = null;
        if (intent.getExtras() != null) {
            sentFlags = intent.getExtras().getString(SENT_FLAGS);
        }

        TextView sentFlagsView = (TextView) findViewById(R.id.sentFlags);

        if (sentFlags == null || sentFlags.trim().equals("")) {
            sentFlagsView.setText(" - no flags - ");
        }
        else {
            sentFlagsView.setText(sentFlags);
        }

        // findViewById(R.id.sentFlagsContainer).setVisibility(View.VISIBLE);
    }

    private void inflateCurrentFlagsIntoView() {
        Intent intent = getIntent();
        int currentFlagsInt = intent.getFlags();
        TextView currentFlagsView = (TextView) findViewById(R.id.currentFlags);

        StringBuilder currentFlags = new StringBuilder();
        for (Entry<String, Integer> flagEntry : FlagUtil.getFlags()) {
            if ((flagEntry.getValue() & currentFlagsInt) != 0) {
                currentFlags.append(flagEntry.getKey() + "\n");
            }
        }
        if (currentFlags.length() > 0) {
            currentFlags.deleteCharAt(currentFlags.length() - 1);
            currentFlagsView.setText(currentFlags.toString());
        }
        else {
            currentFlagsView.setText(" - no flags - ");
        }
    }

    private class LaunchButtonClickListener implements OnClickListener
    {
        private final Intent intent;
        private final boolean forResult;

        public LaunchButtonClickListener(Intent intent) {
            this(intent, false);
        }

        public LaunchButtonClickListener(Intent intent, boolean forResult) {
            this.intent = intent;
            this.forResult = forResult;
        }

        public LaunchButtonClickListener(Class<?> targetActivityClass) {
            this(targetActivityClass, false);
        }

        public LaunchButtonClickListener(Class<?> targetActivityClass,
                boolean forResult) {
            this.intent = new Intent(Intent.ACTION_VIEW);
            this.intent.setClass(BaseActivity.this,
                    targetActivityClass);
            this.forResult = forResult;
        }

        @Override
        public void onClick(View v) {
            final List<Integer> intentFlags = new ArrayList<Integer>();
            final String[] allFlags =
                    getResources().getStringArray(R.array.intentFlags);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(BaseActivity.this);
            builder.setMultiChoiceItems(R.array.intentFlagsShort, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                        int which, boolean isChecked) {
                    String flagStr = allFlags[which];
                    Integer flagInt = FlagUtil.getFlag(flagStr);

                    if (isChecked) {
                        intentFlags.add(flagInt);
                    }
                    else {
                        intentFlags.remove(flagInt);
                    }
                }
            });
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                        int which) {
                    SortedSet<String> sentFlags = new TreeSet<String>();
                    for (Integer flag : intentFlags)
                    {
                        intent.addFlags(flag);
                        sentFlags.add(FlagUtil.getFlagName(flag));
                    }

                    StringBuilder flagBuffer = new StringBuilder();
                    for (String sentFlag : sentFlags) {
                        flagBuffer.append(sentFlag + "\n");
                    }
                    if (flagBuffer.length() > 0) {
                        flagBuffer.deleteCharAt(flagBuffer.length() - 1);
                    }

                    intent.putExtra(SENT_FLAGS, flagBuffer.toString());
                    if (forResult) {
                        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_FORWARD_RESULT) != 0) {
                            ToastUtil.showErrorToast(BaseActivity.this,
                                    "Combining FLAG_ACTIVITY_FORWARD_RESULT"
                                    + " and startActivityForResult(..)"
                                    + " is impossible (leads to " +
                                    " exception)");
                        }
                        else {
                            startActivityForResult(intent, 0);
                        }
                    }
                    else {
                        startActivity(intent);
                    }
                }
            });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                        int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
}

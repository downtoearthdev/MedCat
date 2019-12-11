package com.trccomputers.scorchedcode.MedCat;

import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.View.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.net.*;

public class MainActivity extends AppCompatActivity implements Medicationable
{
	// Constant variable to label and track our userlist
	private static final String USER_LIST_TAG = "UserList";
	private static final String MEDICATION_LIST_TAG = "MedList";
	private static final String USER_DIALOG = "UserDialog";
	private static final String MEDICATION_DIALOG = "MedDialog";
    private FloatingActionButton fAB;
	private FragmentManager fm;
	// Overriden entrypoint for main code to link userlist fragment to the Activity
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main);
		fm = getSupportFragmentManager();
		UserListFragment fragment = (UserListFragment) fm.findFragmentByTag(USER_LIST_TAG);
		if (fragment == null)
		{ // If it doesn't exist yet due to app start, instantiate it
			fragment = new UserListFragment();
			// Add the fragment as a child of our FrameLayout, linking it to our Activity
			fm.beginTransaction().add(R.id.app_main_container, fragment, USER_LIST_TAG).commit();
		}
		fAB = (FloatingActionButton) findViewById(R.id.addFAB);
		fAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_48dp));
		fAB.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					Fragment user = fm.findFragmentByTag(USER_LIST_TAG);
					Fragment meds = fm.findFragmentByTag(MEDICATION_LIST_TAG);
					if (user != null && user.isAdded())
					{
						FragmentTransaction ft = fm.beginTransaction().addToBackStack(USER_DIALOG);
						((UserListFragment)user).getUserDialog().show(ft, USER_DIALOG);
						showFAB(false);
					}
					if (meds != null && meds.isAdded()) {
						FragmentTransaction ft = fm.beginTransaction().addToBackStack(MEDICATION_DIALOG);
						((UserMedicationFragment)meds).getMedsDialog().show(ft, MEDICATION_DIALOG);
						showFAB(false);
					}
						
				}
			});
		// Assign our fragment Object into Object 'fragment' using our variable to see if it exists
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.rxlocate, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + item.getTitle()));
		intent.setPackage("com.google.android.apps.maps");
		startActivity(intent);
		return true;
	}


	public void popDialog(boolean userdialog)
	{
		fm.popBackStack((userdialog) ? USER_DIALOG : MEDICATION_DIALOG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public void showFAB(boolean show)
	{
		if (show)
			fAB.show();
		else
			fAB.hide();
	}

	public void switchMedicationScreen(int position)
	{
		fm.beginTransaction().replace(R.id.app_main_container, UserMedicationFragment.newInstance(position), MEDICATION_LIST_TAG).addToBackStack(MEDICATION_LIST_TAG).commit();
	}

}

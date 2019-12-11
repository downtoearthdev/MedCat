package com.trccomputers.scorchedcode.MedCat;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import dalvik.annotation.*;
import android.support.v7.app.*;
import android.app.Dialog;
import android.content.*;
import android.provider.*;
import android.app.Activity;
import android.graphics.*;
import android.support.v4.view.*;
import java.io.*;

public class UserListFragment extends Fragment
{
	// Overridden entrypoint for main code of our userlist fragment, called after .commit() in MainActivity
	private RecyclerView recycler;
	private File picsDir;
	private static int imagepos = 0;
	private static final int PICTURE_REQUEST_CODE = 1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		picsDir = getActivity().getDir("pics", Activity.MODE_PRIVATE);
		// Convert the XML layout for the fragment into a java Object called View so we can use it programmatically
		View fragment = inflater.inflate(R.layout.user_list_fragment, container, false);
		// Use the View object to find our RecyclerView (list) by ID and assign it to an Object
		recycler = (RecyclerView) fragment.findViewById(R.id.user_list_fragment_recycler);
		// This is required for the RecyclerView, LinearLayoutManager arranges our users into a normal list format
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		// This is required for the RecyclerView, assigns our inline UserListAdapter class to manage data and how it relates to the list
		recycler.setAdapter(new UserListAdapter());
		// This method must return a View with our layout, we assigned this earlier
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			((ImageView)recycler.getLayoutManager().getChildAt(imagepos).findViewById(R.id.user_item_pic)).setImageBitmap((Bitmap)data.getExtras().get("data"));
			try
			{
				OutputStream outStream = new FileOutputStream(picsDir.getPath() + "/" + ((TextView)recycler.getLayoutManager().getChildAt(imagepos).findViewById(R.id.userNameTextView)).getText().toString() + ".png");
				Bitmap bitMap = (Bitmap)data.getExtras().get("data");
				bitMap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			}
			catch (FileNotFoundException e)
			{}
			imagepos = 0;
		}
	}
	
	
	public void updateUserList() {
		recycler.getAdapter().notifyDataSetChanged();
	}
	
	public AddUserDialog getUserDialog() {
		return new AddUserDialog();
	}
	
	
	// Inline classes are within other classes and can only be accessed within the scope of the containing class,
	// perfect for classes that do not need to be accessed elsewhere
	class UserListAdapter extends RecyclerView.Adapter<UserHolder>
	{
		Apothecary apothecary = new Apothecary(getActivity());
		// All of the methods MUST be implemented from RecyclerView.Adapter, as it is abstract, the IDE will do this automatically
		@Override
		public UserHolder onCreateViewHolder(ViewGroup p1, int p2)
		{
			// Convert the XML layout for each user into a View like above, and pass it to a custom ViewHolder to assign any
			// Views like the ImageView and TextView to variables they can be modified from, this is essentially a filter
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			return new UserHolder(inflater.inflate(R.layout.user_list_item, p1, false));
		}

		@Override
		public void onBindViewHolder(UserHolder p1, int p2)
		{
			// Code that changes the Views for each user and assigns data based on Model objects is placed here
			final int position = p2;
			p1.userName.setText(apothecary.getUsers().get(p2).getName());
			p1.card.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View p1)
					{
						((Medicationable)getActivity()).switchMedicationScreen(position);
					}

				});
			p1.pictureView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View p1)
					{
						Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
							imagepos = position;
							startActivityForResult(pictureIntent, PICTURE_REQUEST_CODE);
						}
					}
					
			});
			File userPic = new File(picsDir + "/" + apothecary.getUsers().get(p2).getName() + ".png");
			if(userPic.exists())
				p1.pictureView.setImageBitmap(BitmapFactory.decodeFile(userPic.getPath()));
		}

		@Override
		public int getItemCount()
		{
			// RecyclerView.Adapter is dumb, amd does't know how many items (users) to list. We trick him here
			return apothecary.getUsers().size();
		}
		
		
		
	}
	
	class UserHolder extends RecyclerView.ViewHolder {
		
		// This will be used to assign our ImageView and TextViews later so we can load images and information above in onBindViewHolder()
		public TextView userName;
		public CardView card;
		public ImageView pictureView;
		public UserHolder(View itemView) {
			super(itemView);
			userName = (TextView)itemView.findViewById(R.id.userNameTextView);
			card = (CardView)itemView.findViewById(R.id.userCard);
			pictureView = (ImageView) itemView.findViewById(R.id.user_item_pic);
		}
	}
	
	private class AddUserDialog extends DialogFragment
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			Dialog userDialog = new AlertDialog.Builder(getContext())
			.setTitle("Add a user.")
			.setView(R.layout.adduser_dialog)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						Apothecary apothecary = new Apothecary(getActivity());
						String name = ((EditText)((Dialog)p1).findViewById(R.id.addUserEditText)).getText().toString();
						apothecary.addUser(name);
						updateUserList();
					}
					
			})
			.create();
			return userDialog;
		}
		

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			((Medicationable)getActivity()).showFAB(true);
			((Medicationable)getActivity()).popDialog(true);
		}
	}
}

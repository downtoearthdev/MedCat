package com.trccomputers.scorchedcode.MedCat;

import android.content.*;
import android.util.*;
import java.util.*;
import java.util.concurrent.*;

public class Apothecary
{
	private static final String PHARMACY_FILE = "Apothecary";
	private static final String PRESCRIPTION_USER = "Users";
	private static ArrayList<PrescriptionUser> userList = null;
	public Context context;

	public Apothecary(Context context)
	{
		this.context = context;
		if (userList == null)
			init();
	}

	public ArrayList<PrescriptionUser> getUsers()
	{
		return userList;
	}

	private void init()
	{
		//Executes code that populates all retrievable data
		Set<String> demoUsers = new ArraySet<String>();
		Set<String> demoMeds = new ArraySet<String>();
		SharedPreferences prefs = context.getSharedPreferences(PHARMACY_FILE, Context.MODE_PRIVATE);
		Set<String> users = prefs.getStringSet(PRESCRIPTION_USER, demoUsers);
		userList = new ArrayList<PrescriptionUser>();
		for (String user : users)
		{
			ArrayList<Prescription> medications = new ArrayList<Prescription>();
			for (String script : prefs.getStringSet(user, demoMeds))
				medications.add(new Prescription(script));
			medications = sort(medications);
			userList.add(new PrescriptionUser(user, medications));
		}
		userList = sort(userList);

	}

	public void addUser(String userName)
	{
		SharedPreferences prefs = context.getSharedPreferences(PHARMACY_FILE, Context.MODE_PRIVATE);
		Set<String> newUser = new ArraySet<String>();
		Set<String> user = prefs.getStringSet(PRESCRIPTION_USER, new ArraySet<String>());
		for (String data : user) //Necessary as the set returned from getStringSet isn't modifiable
			newUser.add(data);
		newUser.add(userName);
		userList.add(new PrescriptionUser(userName, new ArrayList<Prescription>()));
		userList = sort(userList);
		prefs.edit().putStringSet(PRESCRIPTION_USER, newUser).apply();
	}

	public int addPrescription(String name, String dosage, String frequency, String amount, int currentUser)
	{
		SharedPreferences prefs = context.getSharedPreferences(PHARMACY_FILE, Context.MODE_PRIVATE);
		String argString = name + "_" + frequency + "_" + amount + "_" + dosage;
		Set<String> newScripts = new ArraySet<String>();
		Set<String> scripts = prefs.getStringSet(userList.get(currentUser).getName(), new ArraySet<String>());
		for (String data : scripts)
			newScripts.add(data);
		newScripts.add(argString);
		int position = userList.get(currentUser).addPrescription(new Prescription(argString));
		prefs.edit().putStringSet(userList.get(currentUser).getName(), newScripts).commit();
		return position;
	}

	private <T extends NameIterable> ArrayList<T> sort(ArrayList<T> list)
	{
		CopyOnWriteArrayList<T> tempList = new CopyOnWriteArrayList<T>();
		for (int x = 65; x < 91; x++)
		{
			ListIterator<T> li = list.listIterator();
			while (li.hasNext())
			{
				T tempName = li.next();
				if (tempName.getName().toUpperCase().charAt(0) == x)
					tempList.add(tempName);
			}
		}
		return new ArrayList<T>(tempList);
	}

	class PrescriptionUser implements NameIterable
	{
		private ArrayList<Prescription> scripts;
		private String userName;
		public PrescriptionUser(String userName, ArrayList<Prescription> scripts)
		{
			this.scripts = scripts;
			this.userName = userName;
		}

		public String getName()
		{
			return userName;
		}

		public int addPrescription(Prescription script)
		{
			scripts.add(script);
			scripts = sort(scripts);
			Log.d("MedCat", String.valueOf(scripts.lastIndexOf(script)));
			return scripts.lastIndexOf(script);
		}

		public ArrayList<Prescription> getPrescriptions()
		{
			return scripts;
		}
	}

	public interface NameIterable
	{
		public String getName();
	}
}

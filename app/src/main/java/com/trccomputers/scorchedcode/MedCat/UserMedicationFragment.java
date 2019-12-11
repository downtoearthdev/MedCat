package com.trccomputers.scorchedcode.MedCat;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.bignerdranch.expandablerecyclerview.Adapter.*;
import com.bignerdranch.expandablerecyclerview.Model.*;
import com.bignerdranch.expandablerecyclerview.ViewHolder.*;
import java.util.*;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.*;
import org.apache.http.auth.*;


public class UserMedicationFragment extends Fragment
{
    HashMap<String, List<String>> Medication_category;
    List<String> Medication;
    ExpandableListView Exp_list;
	public int currentUser;
	private RecyclerView recycler;
	private static final String POSITION = "User";

	public static UserMedicationFragment newInstance(int userPosition)
	{
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION, userPosition);
		UserMedicationFragment fragment = new UserMedicationFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_user_medications, container, false);
		recycler = (RecyclerView) fragment.findViewById(R.id.medicationsRecyclerView);
        currentUser = getArguments().getInt(POSITION);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(new MedicationsAdapter(getActivity(), new Apothecary(getActivity()).getUsers().get(currentUser).getPrescriptions()));
        return fragment;
    }
	
	public AddPrescriptionDialog getMedsDialog() {
		return new AddPrescriptionDialog();
	}
	
	public void updateMedsList(int position) {
		
		((ExpandableRecyclerAdapter)recycler.getAdapter()).notifyParentItemInserted(position); //notifyParentItemRangeChanged(0, new Apothecary(getActivity()).getUsers().get(currentUser).getPrescriptions().size()-1);
	}

//this is to create 2 view holders for the parent and child views;
//Parent View Holder
	class ParentMedicationsViewHolder extends ParentViewHolder
	{

		public TextView parentMedicationsNameTextView;
		public ImageView prescriptionAlarmImageView;
		public ParentMedicationsViewHolder(View itemView)
		{
			super(itemView);
			prescriptionAlarmImageView = (ImageView) itemView.findViewById(R.id.prescriptionAlarmIcon);
			parentMedicationsNameTextView = (TextView) itemView.findViewById(R.id.perscriptionNameTextView);
		}
		public void bind(Prescription prescription)
		{
			prescriptionAlarmImageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View p1)
					{
						(new PrescriptionAlarmDialog()).newInstance(parentMedicationsNameTextView.getText().toString()).show(getFragmentManager(), "AlarmDialog");
					}
			});
			parentMedicationsNameTextView.setText(prescription.getName());
		}
	}
	//Child view holder;
	class ChildMedicationsViewHolder extends ChildViewHolder
	{

		TextView medFrequency, medAmount, medDosage;

		public ChildMedicationsViewHolder(View itemView)
		{
			super(itemView);
			medFrequency = (TextView) itemView.findViewById(R.id.frequencyTextView);
			medAmount = (TextView) itemView.findViewById(R.id.amountTextView);
			medDosage = (TextView) itemView.findViewById(R.id.dosageTextView);
		}

		public void bind(Prescription medDetails)
		{
			medFrequency.setText("Taken " + medDetails.getFrequency() + " times a day.");
			medAmount.setText(medDetails.getTotalTaken() + " pills at a time.");
			medDosage.setText(medDetails.getDose() + "mg per pill");
		}
	}

	//Adapter for Expandable List Recycler View with parent and child as generics (Big Nerd Ranch)

	class MedicationsAdapter extends ExpandableRecyclerAdapter<ParentMedicationsViewHolder, ChildMedicationsViewHolder>
	{
		LayoutInflater inflater;
		public MedicationsAdapter(Context context, List<? extends ParentListItem> parentListItem) {
			super(parentListItem);
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public UserMedicationFragment.ParentMedicationsViewHolder onCreateParentViewHolder(ViewGroup p1)
		{
			View parentMeds = inflater.inflate(R.layout.parent_medication_list, p1, false);
			return new ParentMedicationsViewHolder(parentMeds);
		}

		@Override
		public UserMedicationFragment.ChildMedicationsViewHolder onCreateChildViewHolder(ViewGroup p1)
		{
			View childMeds = inflater.inflate(R.layout.child_medication_list, p1, false);
			return new ChildMedicationsViewHolder(childMeds);
		}

		@Override
		public void onBindParentViewHolder(UserMedicationFragment.ParentMedicationsViewHolder p1, int p2, ParentListItem p3)
		{
			p1.bind((Prescription)p3);
		}

		@Override
		public void onBindChildViewHolder(UserMedicationFragment.ChildMedicationsViewHolder p1, int p2, Object p3)
		{
			p1.bind((Prescription)p3);
		}
		
    }
	
	class PrescriptionAlarmDialog extends DialogFragment
	{
		private static final String MEDICATION = "Meds";
		public PrescriptionAlarmDialog newInstance(String meds) {
			Bundle bundle = new Bundle();
			bundle.putString(MEDICATION, meds);
			PrescriptionAlarmDialog pad = new PrescriptionAlarmDialog();
			pad.setArguments(bundle);
			return pad;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			Calendar now = Calendar.getInstance();
			TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker p1, int p2, int p3)
					{
						AlarmManager am = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
						Intent takeYourMeds = new Intent(getContext(), AlarmBroadcast.class);
						takeYourMeds.putExtra(AlarmBroadcast.EXTRA_MEDICATION, getArguments().getString(MEDICATION));
						Calendar then = Calendar.getInstance();
						then.set(Calendar.HOUR_OF_DAY, p2); then.set(Calendar.MINUTE, p3);
						am.setInexactRepeating(AlarmManager.RTC_WAKEUP, then.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(getActivity(), 0, takeYourMeds, PendingIntent.FLAG_CANCEL_CURRENT));
					}
				}, now.get(now.HOUR_OF_DAY), now.get(now.MINUTE), true);
			return dialog;
		}
	}
	
	private class AddPrescriptionDialog extends DialogFragment
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			Dialog medDialog = new AlertDialog.Builder(getContext())
				.setTitle("Add a prescription.")
				.setView(R.layout.add_script_dialog)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						Apothecary apothecary = new Apothecary(getActivity());
						String name, frequency, amount, dosage;
						name = ((EditText)((Dialog)p1).findViewById(R.id.addScriptNameEditText)).getText().toString();
						frequency = ((EditText)((Dialog)p1).findViewById(R.id.addScriptFrequencyEditText)).getText().toString();
						amount = ((EditText)((Dialog)p1).findViewById(R.id.addScriptAmountEditText)).getText().toString();
						dosage = ((EditText)((Dialog)p1).findViewById(R.id.addScriptDosageEditText)).getText().toString();
						int position = apothecary.addPrescription(name, dosage, frequency, amount, currentUser);
						updateMedsList(position);
					}

				})
				.create();
			return medDialog;
		}


		@Override
		public void onDismiss(DialogInterface dialog)
		{
			((Medicationable)getActivity()).showFAB(true);
			((Medicationable)getActivity()).popDialog(false);
		}
	}
}


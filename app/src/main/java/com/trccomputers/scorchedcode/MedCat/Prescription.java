package com.trccomputers.scorchedcode.MedCat;
import com.bignerdranch.expandablerecyclerview.Model.*;
import java.util.*;

public class Prescription implements ParentListItem, Apothecary.NameIterable
{

	@Override
	public List<?> getChildItemList()
	{
		ArrayList<Prescription> medInfo = new ArrayList<Prescription>();
		medInfo.add(this);
		return medInfo;
	}

	@Override
	public boolean isInitiallyExpanded()
	{
		return false;
	}
	
	private String scriptName, scriptFrequency, scriptTotal;
	private String scriptDose;
	public Prescription(String medicData) {
		String[] data = medicData.split("_");
		scriptName = data[0];
		scriptFrequency = data[1];
		scriptTotal = data[2];
		scriptDose = data[3];
	}
	
	public String getName() {
		return scriptName;
	}
	
	public String getFrequency() {
		return scriptFrequency;
	}
	
	public String getTotalTaken() {
		return scriptTotal;
	}
	
	public String getDose() {
		return scriptDose;
	}
}

package com.winbook.DTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.winbook.domainobjects.WinbookConstants;

public class NegotiationProgressDTO {

	/*
	 * HashMap for status count for a particular 'day' - this day is n-days ago from current date.
	 */
	private Map<Integer, NegotiationStatusCount> progress;
	
	public NegotiationProgressDTO(){
		this.progress = new TreeMap<Integer, NegotiationProgressDTO.NegotiationStatusCount>();
	}
	
	public Map<Integer, NegotiationStatusCount> getProgress() {
		return progress;
	}

	public void setProgress(Map<Integer, NegotiationStatusCount> progress) {
		this.progress = progress;
	}

	public void setStatusCount(int day, String status, int count){
		
		NegotiationStatusCount statusCount = null;
		if(!progress.containsKey(Integer.valueOf(day)))
			progress.put(Integer.valueOf(day), new NegotiationStatusCount());
		
		statusCount = progress.get(Integer.valueOf(day));
		
		if(status.equalsIgnoreCase(WinbookConstants.STATUS_OPEN))
			statusCount.setOpen(count);
		else if(status.equalsIgnoreCase(WinbookConstants.STATUS_AGREE))
			statusCount.setAgreed(count);
		else if(status.equalsIgnoreCase(WinbookConstants.STATUS_MAY_AGREE))
			statusCount.setMaybe(count);

	}
	
	public void computeCumulativeSum()
	{
		Object[] keys = progress.keySet().toArray();
		Arrays.sort(keys, Collections.reverseOrder());
		
		for(int i=1; i<keys.length; i++)
		{
			progress.get(keys[i]).setOpen(progress.get(keys[i]).getOpen() + progress.get(keys[i-1]).getOpen());
			progress.get(keys[i]).setAgreed(progress.get(keys[i]).getAgreed() + progress.get(keys[i-1]).getAgreed());
			progress.get(keys[i]).setMaybe(progress.get(keys[i]).getMaybe() + progress.get(keys[i-1]).getMaybe());
		}
	}
	
	


	/*
	 * Just serves the purpose of a data structure for organizing the content of the 'progress' map
	 */
	private class NegotiationStatusCount
	{
		private int open=0, agreed=0, maybe=0;

		public int getOpen() {
			return open;
		}

		public void setOpen(int open) {
			this.open = open;
		}

		public int getAgreed() {
			return agreed;
		}

		public void setAgreed(int agreed) {
			this.agreed = agreed;
		}

		public int getMaybe() {
			return maybe;
		}

		public void setMaybe(int maybe) {
			this.maybe = maybe;
		}
		
		
		
	}///:~ Negotiation Status
	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		
//		for(Map.Entry<Integer, NegotiationStatusCount> entry : progress.entrySet())
//		{
//			sb.append("day: "+entry.getKey().toString()+"\n\t");
//			sb.append("open: "+entry.getValue().open+"\n\t");
//			sb.append("agreed: "+entry.getValue().agreed+"\n\t");
//			sb.append("maybe: "+entry.getValue().maybe+"\n");
//		}
//		
//		return sb.toString();
//	}


	
}

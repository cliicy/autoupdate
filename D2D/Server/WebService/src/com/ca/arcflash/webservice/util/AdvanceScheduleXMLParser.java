package com.ca.arcflash.webservice.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.ThrottleItem;

public class AdvanceScheduleXMLParser {
	private static XMLBeanMapper<AdvanceSchedule> advanceScheduleMapper;
	private static XMLBeanMapper<DailyScheduleDetailItem> dailyScheduleDetailItemMapper;
	private static XMLBeanMapper<ScheduleDetailItem> scheduleDetailItemMapper;
	private static XMLBeanMapper<ThrottleItem> throttleItemMapper;
	private static XMLBeanMapper<MergeDetailItem> mergeDetailItemMapper;	
	
	//wanqi06  
	private static XMLBeanMapper<DayTime> timeItemMapper;
	
	private static XMLBeanMapper<PeriodSchedule> periodScheduleMapper;
	private static XMLBeanMapper<EveryDaySchedule> dayScheduleMapper;
	private static XMLBeanMapper<EveryWeekSchedule> weekScheduleMapper;
	private static XMLBeanMapper<EveryMonthSchedule> monthScheduleMapper;	
	
	static {
		try {
			advanceScheduleMapper = new XMLBeanMapper<AdvanceSchedule>(AdvanceSchedule.class);
			dailyScheduleDetailItemMapper = new XMLBeanMapper<DailyScheduleDetailItem>(DailyScheduleDetailItem.class);
			scheduleDetailItemMapper = new XMLBeanMapper<ScheduleDetailItem>(ScheduleDetailItem.class);
			throttleItemMapper = new XMLBeanMapper<ThrottleItem>(ThrottleItem.class);
			mergeDetailItemMapper = new XMLBeanMapper<MergeDetailItem>(MergeDetailItem.class);
			timeItemMapper = new XMLBeanMapper<DayTime>(DayTime.class);
			
			periodScheduleMapper = new XMLBeanMapper<PeriodSchedule>(PeriodSchedule.class);;
			dayScheduleMapper= new XMLBeanMapper<EveryDaySchedule>(EveryDaySchedule.class);
			weekScheduleMapper= new XMLBeanMapper<EveryWeekSchedule>(EveryWeekSchedule.class);
			monthScheduleMapper= new XMLBeanMapper<EveryMonthSchedule>(EveryMonthSchedule.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Element getElement(AdvanceSchedule schedule, Document doc) throws Exception{
		Element advScheduleElement = advanceScheduleMapper.saveBean(schedule,doc,"AdvanceSchedules");
		
		List<DailyScheduleDetailItem> daylyLists = schedule.getDailyScheduleDetailItems();
		if(daylyLists !=null){
			for (DailyScheduleDetailItem daylyItem : daylyLists) {
				Element daylyElement = dailyScheduleDetailItemMapper.saveBean(daylyItem, doc, "DailyScheduleDetailItems");
				
				ArrayList<ScheduleDetailItem> itemLists = daylyItem.getScheduleDetailItems();
				if(itemLists!=null){
					for (ScheduleDetailItem item : itemLists) {
						Element itemElement = scheduleDetailItemMapper.saveBean(item, doc,"ScheduleDetailItem");
						Element startElement = timeItemMapper.saveBean(item.getStartTime(), doc, "StartTime");
						Element endElement = timeItemMapper.saveBean(item.getEndTime(), doc, "EndTime");
						itemElement.appendChild(startElement);
						itemElement.appendChild(endElement);
						daylyElement.appendChild(itemElement);
					}
				}
				
				ArrayList<ThrottleItem> throttleItems = daylyItem.getThrottleItems();
				if(throttleItems!=null){
					for (ThrottleItem item : throttleItems) {
						Element itemElement = throttleItemMapper.saveBean(item, doc,"ThrottleItem");
						daylyElement.appendChild(itemElement);
					}
				}
				
				ArrayList<MergeDetailItem> mergeDetailItems = daylyItem.getMergeDetailItems();
				if(mergeDetailItems!=null){
					for (MergeDetailItem item : mergeDetailItems) {
						Element itemElement = mergeDetailItemMapper.saveBean(item, doc,"MergeDetailItem");
						daylyElement.appendChild(itemElement);
					}
				}
				
				advScheduleElement.appendChild(daylyElement);
			}
		}
		
		savePeriodSchedule(schedule, doc, advScheduleElement);
		
		return advScheduleElement;
	}

	private static void savePeriodSchedule(AdvanceSchedule schedule, Document doc, Element advScheduleElement)
			throws Exception {
		PeriodSchedule periodSchedule = schedule.getPeriodSchedule();
		if(periodSchedule != null){
			Element periodScheduleEle = periodScheduleMapper.saveBean(periodSchedule, doc,"periodSchedule");
			advScheduleElement.appendChild(periodScheduleEle);
			EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
			EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
			EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();

			if(daySchedule != null){	
				Element dayScheduleEle = dayScheduleMapper.saveBean(daySchedule, doc,"daySchedule");
				periodScheduleEle.appendChild(dayScheduleEle);
			}
			if(weekSchedule != null){
				Element weekScheduleEle = weekScheduleMapper.saveBean(weekSchedule, doc,"weekSchedule");
				periodScheduleEle.appendChild(weekScheduleEle);
			}
			
			if(monthSchedule != null){
				Element monthScheduleEle = monthScheduleMapper.saveBean(monthSchedule, doc,"monthSchedule");
				periodScheduleEle.appendChild(monthScheduleEle);
			}
		}
	}
	
	public static AdvanceSchedule getAdvanceScheduleFromXML(Document doc) throws Exception{
		AdvanceSchedule advanceSchedule = null;
		NodeList advanceScheduleNodeList = doc.getElementsByTagName("AdvanceSchedules");
		if(advanceScheduleNodeList != null && advanceScheduleNodeList.getLength() > 0) {
			Node advanceNode = advanceScheduleNodeList.item(0);
			advanceSchedule = advanceScheduleMapper.loadBean(advanceNode);
			
			Node periodNode = null;
			
			ArrayList<DailyScheduleDetailItem> daylyItems = new ArrayList<DailyScheduleDetailItem>();
			NodeList daylyNodes = advanceNode.getChildNodes();
			for(int i=0;i<daylyNodes.getLength(); i++){
				Node daylyNode = daylyNodes.item(i); 
				if(daylyNode instanceof Element && daylyNode.getNodeName().equals("DailyScheduleDetailItems")) {
					DailyScheduleDetailItem daylyDetailItem = dailyScheduleDetailItemMapper.loadBean(daylyNode);
					ArrayList<ScheduleDetailItem> itemLists = new ArrayList<ScheduleDetailItem>();
					ArrayList<ThrottleItem> throttleItems = new ArrayList<ThrottleItem>();
					ArrayList<MergeDetailItem> mergeItems = new ArrayList<MergeDetailItem>();
					NodeList itemNodes = daylyNode.getChildNodes();
					for(int j =0; j< itemNodes.getLength(); j++){
						Node itemNode = itemNodes.item(j);
						if(itemNode instanceof Element && itemNode.getNodeName().equals("ScheduleDetailItem")){
							ScheduleDetailItem detailItem = scheduleDetailItemMapper.loadBean(itemNode);
							itemLists.add(detailItem);
						}
						else if(itemNode instanceof Element && itemNode.getNodeName().equals("ThrottleItem")){
							ThrottleItem throttleItem = throttleItemMapper.loadBean(itemNode);
							throttleItems.add(throttleItem);
						}
						else if(itemNode instanceof Element && itemNode.getNodeName().equals("MergeDetailItem")){
							MergeDetailItem mergeItem = mergeDetailItemMapper.loadBean(itemNode);
							mergeItems.add(mergeItem);
						}
					}
					daylyDetailItem.setScheduleDetailItems(itemLists);
					daylyDetailItem.setThrottleItems(throttleItems);
					daylyDetailItem.setMergeDetailItems(mergeItems);
					daylyItems.add(daylyDetailItem);
				}
				
				if(daylyNode instanceof Element && daylyNode.getNodeName().equalsIgnoreCase("periodSchedule")){
					periodNode = daylyNode;
					PeriodSchedule periodSchedule = periodScheduleMapper.loadBean(periodNode);
					advanceSchedule.setPeriodSchedule(periodSchedule);
				}
			}
			advanceSchedule.setDailyScheduleDetailItems(daylyItems);
			
			if(periodNode != null)
				loadPeriodSchedule(periodNode, advanceSchedule);			
		}
		
		return advanceSchedule;
	}

	private static void loadPeriodSchedule(Node periodNode, AdvanceSchedule advanceSchedule) throws Exception {
		for(int i=0;i<periodNode.getChildNodes().getLength();i++){
			Node node = periodNode.getChildNodes().item(i); 
			if(node instanceof Element && node.getNodeName().equalsIgnoreCase("DaySchedule")) {
				EveryDaySchedule daySchedule = dayScheduleMapper.loadBean(node);
				advanceSchedule.getPeriodSchedule().setDaySchedule(daySchedule);
			}else if(node instanceof Element && node.getNodeName().equalsIgnoreCase("weekSchedule")) {
				EveryWeekSchedule weekSchedule = weekScheduleMapper.loadBean(node);
				advanceSchedule.getPeriodSchedule().setWeekSchedule(weekSchedule);
			}else if(node instanceof Element && node.getNodeName().equalsIgnoreCase("monthSchedule")) {
				EveryMonthSchedule monthSchedule = monthScheduleMapper.loadBean(node);
				advanceSchedule.getPeriodSchedule().setMonthSchedule(monthSchedule);
			}
		}
		
	}
}

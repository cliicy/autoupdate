package com.ca.arcflash.ui.client.model;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class VCMDataStoreModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7415307915306060781L;
	
	public VCMDataStoreModel(DiskModel diskModel){
		set("diskModel", diskModel);
		
		ListStore<ModelData> esxlListStore=new ListStore<ModelData>();
		ComboBox<ModelData> esxDataStoreComboBox=new ComboBox<ModelData>();
		esxDataStoreComboBox.setStore(esxlListStore);
		esxDataStoreComboBox.setFieldLabel("DataStore Type");
		set("esxDataStoreComboBox", esxDataStoreComboBox);
		
		TextField<String> hyperVPath=new TextField<String>();
		hyperVPath.setAllowBlank(false);
		set("hyperVPath", hyperVPath);

		com.sencha.gxt.widget.core.client.form.TextField hyperVPath3=new com.sencha.gxt.widget.core.client.form.TextField();
		hyperVPath.setAllowBlank(false);
		set("hyperVPath3", hyperVPath3);
		
	}
	
	@SuppressWarnings("unchecked")
	public ComboBox<ModelData>  getEsxDataStoreComboBox(){
		return (ComboBox<ModelData>)get("esxDataStoreComboBox");
	}
	@SuppressWarnings("unchecked")
	public TextField<String> getHyperVPath(){
		return get("hyperVPath");
	}
	@SuppressWarnings("unchecked")
	public com.sencha.gxt.widget.core.client.form.TextField getHyperVPath3(){
		return get("hyperVPath3");
	}
	
	@SuppressWarnings("unchecked")
	public DiskModel getDiskModel(){
		return (DiskModel)get("diskModel");
	}
	
	@SuppressWarnings("unchecked")
	public String getVolumes(){
		String volumeString="";
		DiskModel diskModel=getDiskModel();
		for(int i=0;i<diskModel.getVolumes().size();i++)
		{
			String driveLetter=getDriveLetter(diskModel.getVolumes().get(i).getDriveLetter());
			if((driveLetter==null)||(driveLetter.isEmpty())){
				continue;
			}
			
			if(volumeString.isEmpty()){
				volumeString=driveLetter;
			}
			else{
				volumeString=volumeString+","+driveLetter;
			}
		}
		return volumeString.toUpperCase();
	}
	private String getDriveLetter(String strDriveLetter){
		if(strDriveLetter==null){
			return "";
		}
		if(strDriveLetter.length()==1){
			strDriveLetter=strDriveLetter+":\\";
		}
		else if(strDriveLetter.length()==2){
			strDriveLetter=strDriveLetter+"\\";
		}
		return strDriveLetter;
	}

}

package com.ca.arcflash.ui.client.model;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class DedupeSettingInfo extends BaseModelData{
		
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public String getNodeId(){
		return (String)get("NodeId");
	}
	
	public void setNodeId(String NodeId){
		set("NodeId", NodeId);
	}
	
	public String getBlockSize() {
        return (String)get("BlockSize");
    }
	
    public void setBlockSize(String BlockSize) {
        set("BlockSize", BlockSize);
    }
    
	public String getStoreName() {
		return (String)get("StoreName");
    }
	
    public void setStoreName(String StoreName) {
        set("StoreName",  StoreName);
    }

	public String getCompress() {
		return (String)get("Compress");
	}

	public void setCompress(String Compress) {
		set("Compress", Compress);
	}

	public String getEncryption() {
		return (String)get("Encryption");
	}

	public void setEncryption(String Encryption) {
		set("Encryption", Encryption);
	}

	public String getPassphrase() {
		return (String)get("Passphrase");
	}

	public void setPassphrase(String Passphrase) {
		set("Passphrase", Passphrase);
	}
	
/*	public String getConfirmPassphrase() {
		return (String)get("ConfirmPassphrase");
	}

	public void setConfirmPassphrase(String ConfirmPassphrase) {
		set("ConfirmPassphrase", ConfirmPassphrase);
	}*/
	
	public String getHashStore() {
		return (String)get("HashStore");
	}

	public void setHashStore(String HashStore) {
		set("HashStore", HashStore);
	}
	
	public String getIndexStore() {
		return (String)get("IndexStore");
	}

	public void setIndexStore(String IndexStore) {
		set("IndexStore", IndexStore);
	}
	
	public String getDataStore() {
		return (String)get("DataStore");
	}

	public void setDataStore(String DataStore) {
		set("DataStore", DataStore);
	}
}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DataStoreInfoModel extends BaseModelData{
	private String dataStorePath = "";
	private long dirSize = -1;
	private long freeSize = -1;
	private double totalSize = -1;
	private boolean isDedupe = false;
	private String indexPath = "";
	private long indexDirSize = -1;
	private long indexFreeSize = -1;
	private double indexTotalSize = -1;
	private String dataPath = "";
	private long dataDirSize = -1;
	private long dataFreeSize = -1;
	private double dataTotalSize = -1;
	private String hashPath = "";
	private long hashDirSize = -1;
	private long hashFreeSize = -1;
	private double hashTotalSize = -1;
	
	public String getDataStorePath() {
		return dataStorePath;
	}
	public void setDataStorePath(String dataStorePath) {
		this.dataStorePath = dataStorePath;
	}
	public long getDirSize() {
		return dirSize;
	}
	public void setDirSize(long dirSize) {
		this.dirSize = dirSize;
	}
	public long getFreeSize() {
		return freeSize;
	}
	public void setFreeSize(long freeSize) {
		this.freeSize = freeSize;
	}
	public double getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(double totalSize) {
		this.totalSize = totalSize;
	}
	public boolean isDedupe() {
		return isDedupe;
	}
	public void setDedupe(boolean isDedupe) {
		this.isDedupe = isDedupe;
	}
	public String getIndexPath() {
		return indexPath;
	}
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	public long getIndexDirSize() {
		return indexDirSize;
	}
	public void setIndexDirSize(long indexDirSize) {
		this.indexDirSize = indexDirSize;
	}
	public long getIndexFreeSize() {
		return indexFreeSize;
	}
	public void setIndexFreeSize(long indexFreeSize) {
		this.indexFreeSize = indexFreeSize;
	}
	public double getIndexTotalSize() {
		return indexTotalSize;
	}
	public void setIndexTotalSize(double indexTotalSize) {
		this.indexTotalSize = indexTotalSize;
	}
	public String getDataPath() {
		return dataPath;
	}
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
	public long getDataDirSize() {
		return dataDirSize;
	}
	public void setDataDirSize(long dataDirSize) {
		this.dataDirSize = dataDirSize;
	}
	public long getDataFreeSize() {
		return dataFreeSize;
	}
	public void setDataFreeSize(long dataFreeSize) {
		this.dataFreeSize = dataFreeSize;
	}
	public double getDataTotalSize() {
		return dataTotalSize;
	}
	public void setDataTotalSize(double dataTotalSize) {
		this.dataTotalSize = dataTotalSize;
	}
	public String getHashPath() {
		return hashPath;
	}
	public void setHashPath(String hashPath) {
		this.hashPath = hashPath;
	}
	public long getHashDirSize() {
		return hashDirSize;
	}
	public void setHashDirSize(long hashDirSize) {
		this.hashDirSize = hashDirSize;
	}
	public long getHashFreeSize() {
		return hashFreeSize;
	}
	public void setHashFreeSize(long hashFreeSize) {
		this.hashFreeSize = hashFreeSize;
	}
	public double getHashTotalSize() {
		return hashTotalSize;
	}
	public void setHashTotalSize(double hashTotalSize) {
		this.hashTotalSize = hashTotalSize;
	}
}

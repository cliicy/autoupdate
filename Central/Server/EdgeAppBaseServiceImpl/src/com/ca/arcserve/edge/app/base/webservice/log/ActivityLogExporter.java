package com.ca.arcserve.edge.app.base.webservice.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.ActivityLogMsgUtil;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.CsvBuilder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportProgress.LogExportStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;

public class ActivityLogExporter implements Runnable
{
	private static Logger log = Logger.getLogger( ActivityLogExporter.class );
	private static final String exportFileNamePrefix= "activitylog_export_"; ///temp solution; no localization now
	private static ActivityLogServiceImpl service = new ActivityLogServiceImpl(); //please avoid recursive function call!
	private static SimpleDateFormat formatter = new SimpleDateFormat( MessageReader.getDateFormat("timeDateFormat") );
	private static SimpleDateFormat formatForFileName = new SimpleDateFormat( "yyyy_MM_dd_HH_mm_ss_SSS" ); 

	private static Integer shareSize = 50000;   
	private LogPagingConfig config;
	private LogFilter filter;
	private String exportIdentifier;

	private String filter_TitleString;

	private boolean canceled = false; 

	private File exportFolder; 
	ActivityLogExporter( LogPagingConfig config,  LogFilter filter, String exportIdentifier  ) {
		this.config= config;
		this.filter = filter;
		this.exportIdentifier = exportIdentifier;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	@Override
	public void run() {
		try {
			if( !createFolder()  ) {
				LogExportController.setResult( exportIdentifier, LogExportStatus.FAIL ,"", "" );
				return;
			}
			config.setCount(0);
			config.setStartIndex(-1);

			writeFiltersAndTitle();
			LogPagingResult detectResult = service.getUnifiedLogs( config, filter );///detect size;
			generateProgress(0, 10, 1,1  );
			
			if( detectResult.getTotalCount() < shareSize ) {
				try {
					CsvBuilder builder = new CsvBuilder();
					int subTrip = 2;
					builder.addStringToBufferInstantly( filter_TitleString );
					int subCount = (int)Math.ceil( ( detectResult.getTotalCount()  +0.0 )/subTrip ); 
					for( int i =0 ; i<subTrip; i++ ) {
						oneTripReadDB( builder, i*subCount , subCount, false );
						generateProgress(10, 80, subTrip, i+1  );
					}
					oneTripWriteCSV(builder,1 , 0, detectResult.getTotalCount() ); 
					generateProgress(80, 80, 1, 1  );
				}
				catch( Exception  e ) {
					log.error( "ActivityLogExporter: log export error from row: 0 to row: " +   detectResult.getTotalCount() + " fail! " , e);
				}
			}
			else {  //split load
				int share = (int)Math.ceil( (detectResult.getTotalCount() +0.0 )/shareSize );
				for( int i = 0; i<share; i++ ) {	
					try {
						CsvBuilder builder = new CsvBuilder();
						oneTripReadDB( builder, shareSize * i, shareSize , true );
						generateProgress(10, 80, share, i+0.8  );
						
						oneTripWriteCSV( builder, i+1 , shareSize * i, shareSize ); 
						generateProgress(10, 80, share, i+1  );
					}
					catch( Exception  e ) {
						log.error( "ActivityLogExporter: log export error from row: " + shareSize * i + " with total= " +  shareSize + " fail! " , e);
					}
					///it's not recommended, but I find call gc here can greatly reduce memory usage quickly; 
					//and the log export is not a function called frequently, so I add this code; if you find any problem, you can remove it
					System.gc();  
				}
			}
			if ( generateZip() ) { 
				LogExportController.setResult(exportIdentifier, LogExportStatus.SUCCESS , exportFolder.getName() + ".zip" , "");
			}
			else {
				LogExportController.setResult( exportIdentifier, LogExportStatus.FAIL ,"", "");
			}
		}
		catch( Exception e ) {
			log.error( "ActivityLogExporter:  log export fail! ", e );
			LogExportController.setResult( exportIdentifier, LogExportStatus.FAIL ,"", "");
		}
		catch( Error error ) {
			log.error( "ActivityLogExporter:  log export fail!: ", error );
		}
		finally{
			if( exportFolder !=null && exportFolder.exists() ){
				CommonUtil.recursiveDelFolder( exportFolder.getAbsolutePath() );
			}
		}
	}
	/**
	 * it has 2 functions;1. set progess; set cancel point 
	 * @param startPoint
	 * @param endPoint
	 * @param totalProgress
	 * @param thisProgress
	 */
	private void generateProgress( int startPoint, int endPoint, int totalProgress, double thisProgress ){
		if( canceled ) {
			throw new Error( " user cancel this export operation from  UI " );
		}
		else {
			LogExportController.setProgress( exportIdentifier , LogExportStatus.FetchDB,  startPoint + ((Double)( (endPoint +0.00-startPoint )*(thisProgress)/totalProgress)).intValue() );	

		}
	}
	private boolean generateZip() {
		File[] exportCsvs = exportFolder.listFiles();
		if( exportCsvs == null ) {
			log.error( "ActivityLogExporter: genrate Zip fail! no csv files " );
			return false;
		}
		String exportPath = CommonUtil.getReportFileTempDir(); // use exported file directory same as report export file path;
		
		File zipFile = new File( exportPath + exportFolder.getName() + ".zip" ); 

		byte[] buf=new byte[32*1024];
		ZipOutputStream out = null;
		FileInputStream in  = null;
	    try {
	    	out = new ZipOutputStream(new FileOutputStream(zipFile));
	    	for( int i =0 ; i< exportCsvs.length; i++ ) {
	    		File toZip = exportCsvs[i];
	    		in = new FileInputStream( toZip );
	    		out.putNextEntry(new ZipEntry( toZip.getName()));
	    		int len;
	    		while((len=in.read(buf))>0) {
	    			out.write(buf,0,len);
	    		}
	    		out.closeEntry();
	    		in.close();
	    		generateProgress(80, 100, exportCsvs.length,  i+1  );
	    	}
	    	return true;
	    } 
	    catch (Exception e) {
	    	log.error( "ActivityLogExporter: genrate Zip fail! "  ,e );
	      	return false;
	    }
	    finally {
	    	try {
	    		if( out !=null ) {
					out.close();
	    		}
	    		if( in !=null ) {
					in.close();
	    		}
			} catch (IOException e) {
			}
	    }
	}
	
	private boolean createFolder(){	 
		String exportPath = CommonUtil.getReportFileTempDir(); // use exported file directory same as report export file path;
		String exportFolderName = exportFileNamePrefix + formatForFileName.format(new Date()); 
		exportFolder = new File ( exportPath + exportFolderName ); 
		try {
			if( exportFolder.exists() ){
				exportFolder = new File ( exportPath + exportFolderName + "_" + exportIdentifier.hashCode() ); 
				if( exportFolder.exists() ) {
					log.error( "ActivityLogExporter: log export create folder " + exportFolder + " fail! a folder with duplicated name exist ");
					return false;
				}
			}
			if( exportFolder.mkdir() ) {
				return true;
			}
			else {
				log.error( "ActivityLogExporter: log export create folder " + exportFolder + " fail!");
				return false;
			}
		}
		catch( Exception e  ) {
			log.error( "ActivityLogExporter: log export create folder " + exportFolder + " fail! a folder with duplicated name exist ");
			return false;
		}
	}
	
	private void oneTripReadDB( CsvBuilder builder, int startRow, int rowCount, boolean writeFilterTitle ) throws EdgeServiceFault {
 
		config.setStartIndex( startRow );
		config.setCount( rowCount );
		LogPagingResult pagingResult = service.getUnifiedLogs( config, filter );
		if( writeFilterTitle ) {
			builder.addStringToBufferInstantly( filter_TitleString );
		}
		List<ActivityLog> logs = pagingResult.getData();
		for( ActivityLog log  : logs ) {
			writeRow( builder, log );
		}
	}
	private void oneTripWriteCSV( CsvBuilder builder, int tripNum,int startRow, int rowCount  ) {
		FileOutputStream output = null; 
		try {
			String exportFileName = exportFileNamePrefix + tripNum +".csv" ;
			File exportFile = new File ( exportFolder.getAbsolutePath(), exportFileName );
			if( exportFile.exists() || exportFile.createNewFile() ) { 	
				log.info( "ActivityLogExporter: write start: " + new Date()   );
			    output = new FileOutputStream(exportFile);
			    FileChannel channel = output.getChannel();

			    CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
			    CharBuffer buffer = CharBuffer.wrap( builder.getCsvContent().toCharArray() );
			    ByteBuffer byteBuffer = encoder.encode(buffer);
			    channel.write( ByteBuffer.wrap(
			    		new byte[] {(byte)0xEF , (byte)0xBB , (byte)0xBF}) ); 
			    channel.write( byteBuffer );
			    channel.close();
				log.info( "ActivityLogExporter: write end: " + new Date()   );
			}
			else {
				log.error( "ActivityLogExporter:  log export failed create csv file in disk! from row: " + startRow + " to row: " +  rowCount + " fail! " );
			}
		}
		catch( IOException e ) {
			log.error( "ActivityLogExporter: log export file write error from row: " + startRow + " to row: " +  rowCount  , e);
		}
		finally {
			if( output != null ) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private void writeRow( CsvBuilder builder, ActivityLog log ){
		List<String> rowStrings = new ArrayList<String>(); 
		rowStrings.add(  ActivityLogMsgUtil.getSeverityString( log.getSeverity(), 1) );
		
		rowStrings.add(  formatter.format( log.getTime() ));
		//actually node name 
		rowStrings.add( StringUtil.isEmptyOrNull(log.getTargetNodeName())? StringUtil.isEmptyOrNull( log.getTargetVMName() ) ? "" : MessageReader.getFlashUIMsg( "unknown_vm", log.getTargetVMName() ): log.getTargetNodeName() );
		///actually generate from
		rowStrings.add( log.getNodeName() ); 
		
		rowStrings.add( log.getJobId() ==0 ? null: String.valueOf( log.getJobId() ) );
		
		rowStrings.add( ActivityLogMsgUtil.getJobTypeString( log.getJobType() ) );
		
		rowStrings.add( log.getMessage() );
		
		builder.addLineToBufferInstantly( rowStrings );
	}

	private List<String> filterString = new ArrayList<String>();
	private List<String> titleString =  new ArrayList<String>();
	private void writeFiltersAndTitle ( ) {
		CsvBuilder builder = new CsvBuilder();

		writeOneFilter( MessageReader.getCMUILogConst("severity"), ActivityLogMsgUtil.getSeverityString( filter.getSeverity(), 0) );
		
		writeOneFilter( MessageReader.getCMUILogConst("time") , ActivityLogMsgUtil.getTimeFilterValue(filter.getTimeFilter(), formatter ) );
		
		writeOneFilter( MessageReader.getCMUILogConst("nodeName") , filter.getNodeName() );
		//generated from
		writeOneFilter( MessageReader.getCMUILogConst("serverName"), filter.getServerName());
		
		writeOneFilter( MessageReader.getCMUILogConst("jobId"),  filter.getJobId()>0 ? String.valueOf( filter.getJobId() ): "" );
		 

		writeOneFilter( MessageReader.getCMUILogConst("jobType"), ActivityLogMsgUtil.getJobTypeFilterString( filter.getJobType() ) );
		
		writeOneFilter( MessageReader.getCMUILogConst("message"), filter.getMessage() );
		
		builder.addLineToBufferInstantly( filterString );
		builder.addLineToBufferInstantly( null );
		builder.addLineToBufferInstantly( titleString );
		filter_TitleString = builder.getCsvContent();
	}
	private void writeOneFilter( String name, String value ){
		if( StringUtil.isEmptyOrNull(value) ){
			value = MessageReader.getCMUILogConst("all");
		}
		filterString.add( name +" : " + value );
		titleString.add(name);
	}
}
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ASBUMediaFlag {
	TSI_TF_LOCKED                   (0x00000001L),     // Device is L),ocked
	TSI_TF_SCSI_VALID               (0x00000002L),     // Device SCSI command is vaL),id
	TSI_TF_EMPTY                    (0x00000004L),     // Device is empty
	TSI_TF_WRTPROT                  (0x00000008L),     // Media is write protected
	TSI_TF_BLANK                    (0x00000010L),     // Media is bL),ank
	TSI_TF_DRIVE_ACTIVE             (0x00000020L),     // Drive is active
	TSI_TF_BAD_TAPE                 (0x00000040L),     //NT added, Tape is bad, media error
	TSI_TF_SLOT_NOT_VERIFIED        (0x00000080L),     //NT added, Tape sL),ot is not verified (CHANGER ONL),Y)
	TSI_TF_AT_BOM                   (0x00000100L),     // Device is at beginning of media
	TSI_TF_AT_EOM                   (0x00000200L),     // Device is at end of media
	TSI_TF_OFF_LINE                 (0x00000400L),     // Device is off L),ine
	TSI_TF_BAR_CODE                 (0x00000800L),     // NT added, Tape has BAR CODE
	TSI_TF_RAID_TAPE                (0x00001000L),     // NT added, RAID Tape
	TSI_TF_COMPRESSION_ON           (0x00002000L),     // Hardware Data Compression enabL),ed
	TSI_TF_LOGGING_ON               (0x00004000L),     // SCSI L),og Sense enabL),ed
	TSI_TF_WORM_CAPABLE             (0x00004000L),     // WORM capabL),e
	TSI_TF_WORM                     (0x00008000L),     // WORM enabL),ed
	TSI_TF_PAD_ON                   (0x00008000L),     // Padding enabL),ed
	TSI_TF_LOAD_OK                  (0x00010000L),     // L),oad init compL),eted
	TSI_TF_UNICENTER_TAPE           (0x00020000L),     // Media is a TNG UNICENTER Tape.
	TSI_TF_DOOR_LOCKED              (0x00040000L),     // Device door L),ocked
	TSI_TF_OPTICAL_PROD_MEDIA       (0x00080000L),     // Media beL),ongs to and is managed by OpticaL),Products
	TSI_TF_READ                     (0x00100000L),     // NT added, BarCode Tape need to be read
	TSI_TF_VAULT                    (0x00200000L),     // NT added, Tape is ready for vauL),ting, shouL),d be offL),ine. 5/31/98
	TSI_TF_MARKED_BLANK             (0x00800000L),     // Unknown tape, marked as bL),ank
	TSI_TF_SNAPLOCKED               (0x04000000L),     // the tape has been snapL),ocked
	TSI_TF_POSSIBLE_PROBLEM         (0x01000000L),     // Drive has possibL),e probL),em
	TSI_TF_INTERLEAVING             (0x00400000L),     // Media is in interL),eaving format
	TSI_TF_RESERVED                 (0x02000000L),     // NT added, Indicate sL),ot or media is reserved for immediate use
	TSI_TF_MEDIA_IS_LOADED          (0x04000000L),     // NT added, Media is in a drive
	TSI_TF_MEDIA_IN_USE             (0x08000000L),     // NT added, Media is being used
	TSI_TF_TI_VALID                 (0x10000000L),     // NT added, Tape Info is vaL),id
	TSI_TF_NON_CHEY                 (0x20000000L),     // NT added, Non-Cheyenne Format Tape
	TSI_TF_MUX                      (0x40000000L),     // NT added, Indicates MUX tapes
	TSI_TF_CLEAN_TAPE               (0x80000000L);     // NT added, Changer CL),eaning tape,  (CHANGER ONL),Y)
	
	private ASBUMediaFlag(long value){
		this.value = value;
	}
	private long value;

	public long getValue() {
		return value;
	}
}

#pragma once
//
// *********************************************************
// define the internal error for update
// *********************************************************
//

//
// there is no update found
//
#define		inter_error_no_update_found				-1
//
// the current build is already update to date
//
#define		inter_error_update_to_date				-2
//
// the update job is canceled 
//
#define		inter_error_update_canceled				-3
//
// the update is busy
//
#define		inter_error_update_busy					-4
//
// the update server is unavailable.
//
#define		inter_error_server_unavailable			-5
// 
// failed to download update
//
#define		inter_error_failed_download				-6
//
// the latest update was downloaded already
//
#define		inter_error_already_download			-7
//
// the configure file is invalid
//
#define		inter_error_invalid_config_file			-8
//
// the downaloded file is not singed by Arcserve
//
#define		inter_error_invalid_signature			-9
//
// the downloaded file is damaged
//
#define		inter_error_file_damaged				-10
//
// failed to create folder
//
#define		inter_error_folder_not_found			-12

//
// must firstly do self update before downloading others
//
#define		inter_error_selfupdate_required			-13

//
// arcupdate server is stopped.
//
#define		inter_error_service_stopped				-14

//
// arcupdate server is stopped.
//
#define		inter_error_failed_trigger_job			-15

//
// the update job crashed.
//
#define		inter_error_update_crash				-16

//
// the update job is not supported
//
#define		inter_error_unsupported_update			-17

//
// invalid job script
//
#define		inter_error_invalid_job_script			-18


//
// *********************************************************
// define the internal error used in communication
// *********************************************************
//

//
// the specified command is not supported
//
#define		error_pipe_unsupported_cmd				-1000

//
// the request parameters are incorrect
//
#define		error_pipe_invalid_parameter			-1001
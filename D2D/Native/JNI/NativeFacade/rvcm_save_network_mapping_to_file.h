#include "stdafx.h"

#include <string>
#include <vector>
using namespace std;

namespace NSRVCM
{


enum virtual_platform_e
{
	platform_hyperv_t,
	platform_esx_t,
	platform_vsphere_t,
	platform_xen_t,
	platform_ec2_t,
	max_platform_t
};

struct adapter_map_info
{
	bool				replicate_mac;
	std::string			device_id;
	std::string			mac_address;

	bool				ip_use_dhcp; //<sonmi01>2012-10-19 ###???
	bool				dns_use_dhcp;
	bool				use_original_setting;
	std::string			wins_p;
	std::string			wins_s;
	std::vector<std::string> ips;
	std::vector<std::string> subnets;
	std::vector<std::string> gateways;
	std::vector<std::string> dns;
};

bool save_network_mapping_to_file(IN virtual_platform_e vpe, IN const std::vector<adapter_map_info>& net_adapters, CONST WCHAR * pFileName);
bool load_network_mapping_from_file(OUT virtual_platform_e & vpe, OUT std::vector<adapter_map_info>& net_adapters, CONST WCHAR * pFileName);

} //end NSRVCM
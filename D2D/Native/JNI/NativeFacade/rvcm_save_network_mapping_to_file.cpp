#include "stdafx.h"

#include <comutil.h>

#include <vector>
#include <string>
#include <fstream>
using namespace std;

#include "rvcm_save_network_mapping_to_file.h"

namespace NSRVCM
{


//namespace /*private */{
//f:\~~~workspace\RHA-dev\common\common2\win32_base\serializer.h
const long true_pattern = 0xF00DF00D;
const long false_pattern = 0xBAD0BAD0;

class serializer
{
public:
	serializer (std::ostream& os) : _ostream(os) {}
	~serializer () {_ostream.flush();}
	void put_char(char value) { return put_basic_t<char>(value); }
	void put_uchar(unsigned char value) { return put_basic_t<unsigned char>(value); }
	void put_wchar(wchar_t value) { return put_basic_t<wchar_t>(value); }
	void put_bool(bool value) { return put_basic_t<bool>(value); }
	void put_short(short value) { return put_basic_t<short>(value); }
	void put_long(long value) { return put_basic_t<long>(value); }
	void put_ulong(unsigned long value) { return put_basic_t<unsigned long>(value); }
	void put_llong(INT64 value) { return put_basic_t<INT64>(value); }
	void put_ullong(UINT64 value) { return put_basic_t<UINT64>(value); }
	void put_double(double value) { return put_basic_t<double>(value); }
	void put_string(const std::string& value) { return put_string_t<std::string>(value); }
	void put_wstring(const std::wstring& value) { return put_string_t<std::wstring>(value); }
	void put_raw(size_t length, const byte* buff);
	//void put_raw(const util::raw_buffer& buff);

protected:
	template<typename T> void put_basic_t(T value)
	{
		_ostream.write (reinterpret_cast<char*>(&value), sizeof(T));
		_ostream.flush();
	}
	template<> void put_basic_t(bool value)
	{
		long pattern = value? true_pattern: false_pattern;
		put_long(pattern);
		_ostream.flush();
	}
	template<typename T> void put_string_t(const T& value)
	{
		long len = static_cast<long>(value.length() * sizeof(T::value_type));
		put_long(len);
		if(len > 0)
			_ostream.write(reinterpret_cast<const char*>(value.data()), len);
		_ostream.flush();
	}
private:
	std::ostream& _ostream;
	serializer& operator =(const serializer&);
};

class deserializer
{
public:
	deserializer (std::istream& is) : _istream(is) {}
	char get_char() { return get_basic_t<char>(); }
	unsigned char get_uchar() { return get_basic_t<unsigned char>(); }
	wchar_t get_wchar() { return get_basic_t<wchar_t>(); }
	bool get_bool() { return get_basic_t<bool>(); }
	short get_short() { return get_basic_t<short>(); }
	long get_long() { return get_basic_t<long>(); }
	unsigned long get_ulong() { return get_basic_t<unsigned long>(); }
	INT64 get_llong() { return get_basic_t<INT64>(); }
	UINT64 get_ullong() { return get_basic_t<UINT64>(); }
	double get_double() { return get_basic_t<double>(); }
	std::string get_string() { return get_string_t<std::string>(); }
	std::wstring get_wstring() { return get_string_t<std::wstring>(); }
	//util::raw_buffer get_raw();
	//void get_raw(util::raw_buffer& buff);

protected:
	template <typename T> T get_basic_t()
	{
		T value;
		memset(&value, 0 ,sizeof(T));
		_istream.read(reinterpret_cast<char *>(&value), sizeof(T));
		return value;
	}
	template<> bool get_basic_t()
	{
		long value = get_long();
		if (value == true_pattern)
			return true;
		else if (value == false_pattern)
			return false;
		else
			return false;
	}
	template<typename T> T get_string_t()
	{
		long len = get_long();
		T value;
		value.resize(len / sizeof(T::value_type));
		if (_istream.eof())
			return value;
		if(len > 0)
			_istream.read(reinterpret_cast<char*>(&value[0]), len);
		return value;
	}

private:
	std::istream& _istream;
	deserializer& operator =(const deserializer&);
};

class serializable
{
public:
	virtual bool serialize(serializer& out) const = 0;
	virtual bool deserialize(deserializer& in) = 0;
};

//f:\~~~workspace\RHA-dev\common\db\p2v\p2v_common_def.h
//struct adapter_map_info
//{
//	bool				replicate_mac;
//	std::string			device_id;
//	std::string			mac_address;
//
//	bool				use_dhcp;
//	bool				use_original_setting;
//	std::string			wins_p;
//	std::string			wins_s;
//	std::vector<std::string> ips;
//	std::vector<std::string> subnets;
//	std::vector<std::string> gateways;
//	std::vector<std::string> dns;
//};

//f:\~~~workspace\RHA-dev\common\common2\win32_base\network_adapter.h
struct win32_network_adapter_info
{
	bool ip_use_dhcp; //<sonmi01>2012-10-19 ###???
	bool dns_use_dhcp;
	_bstr_t id;
	_bstr_t name;
	_bstr_t mac_address;
	std::vector<_bstr_t> gateways;
	std::vector<_bstr_t> ips;
	std::vector<_bstr_t> subnets;
	std::vector<_bstr_t> dns;
	_bstr_t wins_primary;
	_bstr_t wins_secondary;
};

//f:\~~~workspace\RHA-dev\common\common2\win32_base\basic_util.cpp
std::wstring win32_bstr_t_to_wstr(const _bstr_t& src)
{
	const wchar_t* p = static_cast<const wchar_t*>(src);
	return p? std::wstring(p) : L"";
}

//f:\~~~workspace\RHA-dev\common\db\p2v\p2v_common_def.h
struct adapter_info : public win32_network_adapter_info, public serializable
{
	bool				use_original;
	virtual bool		serialize(serializer& out) const
	{
		out.put_bool(ip_use_dhcp); //<sonmi01>2012-10-19 ###???
		out.put_bool(dns_use_dhcp);
		out.put_wstring(win32_bstr_t_to_wstr(id));
		out.put_wstring(win32_bstr_t_to_wstr(name));
		out.put_wstring(win32_bstr_t_to_wstr(mac_address));

		out.put_long(static_cast<long>(gateways.size()));
		for (size_t i = 0; i < gateways.size(); ++i)
			out.put_wstring(win32_bstr_t_to_wstr(gateways[i]));

		out.put_long(static_cast<long>(ips.size()));
		for (size_t i = 0; i < ips.size(); ++i)
			out.put_wstring(win32_bstr_t_to_wstr(ips[i]));

		out.put_long(static_cast<long>(subnets.size()));
		for (size_t i = 0; i < subnets.size(); ++i)
			out.put_wstring(win32_bstr_t_to_wstr(subnets[i]));

		out.put_long(static_cast<long>(dns.size()));
		for (size_t i = 0; i < dns.size(); ++i)
			out.put_wstring(win32_bstr_t_to_wstr(dns[i]));

		out.put_wstring(win32_bstr_t_to_wstr(wins_primary));
		out.put_wstring(win32_bstr_t_to_wstr(wins_secondary));
		out.put_bool(use_original);
		return true;
	}
	virtual bool		deserialize(deserializer& in)
	{
		gateways.clear();
		ips.clear();
		subnets.clear();
		dns.clear();

		ip_use_dhcp = in.get_bool(); //<sonmi01>2012-10-19 ###???
		dns_use_dhcp = in.get_bool();
		id = in.get_wstring().c_str();
		name = in.get_wstring().c_str();
		mac_address = in.get_wstring().c_str();

		long sz = in.get_long();
		gateways.reserve(sz);
		for (long i = 0; i < sz; ++i)
			gateways.push_back(in.get_wstring().c_str());

		sz = in.get_long();
		ips.reserve(sz);
		for (long i = 0; i < sz; ++i)
			ips.push_back(in.get_wstring().c_str());

		sz = in.get_long();
		subnets.reserve(sz);
		for (long i = 0; i < sz; ++i)
			subnets.push_back(in.get_wstring().c_str());

		sz = in.get_long();
		dns.reserve(sz);
		for (long i = 0; i < sz; ++i)
			dns.push_back(in.get_wstring().c_str());

		wins_primary = in.get_wstring().c_str();
		wins_secondary = in.get_wstring().c_str();
		use_original = in.get_bool();
		return true;
	}
	adapter_info() : use_original(false) {}
};

struct network_adapters : public serializable
{
	std::vector<adapter_info> adapters;
	virtual bool		serialize(serializer& out) const
	{
		out.put_long(static_cast<long>(adapters.size()));
		for (std::vector<adapter_info>::const_iterator i = adapters.begin(); i != adapters.end(); ++i)
			i->serialize(out);
		return true;
	}
	virtual bool		deserialize(deserializer& in)
	{
		adapters.clear();
		long size = in.get_long();
		adapters.reserve(size);
		for (long i = 0; i < size; ++i)
		{
			adapter_info ai;
			ai.deserialize(in);
			adapters.push_back(ai);
		}
		return true;
	}
};

//f:\~~~workspace\RHA-dev\common\db\p2v\virtual.h
//enum virtual_platform_e{
//	platform_hyperv_t,
//	platform_esx_t,
//	platform_vsphere_t,
//	platform_xen_t,
//	platform_ec2_t,
//	max_platform_t
//};

//f:\~~~workspace\RHA-dev\common\db\p2v\p2v_controller.h
struct p2v_config_info : public serializable
{
	virtual_platform_e		_virtual_platform;
	network_adapters		_net_adapters;
	virtual bool serialize(serializer& out) const
	{
		out.put_short(static_cast<short>(_virtual_platform));
		_net_adapters.serialize(out);
		return true;
	}
	virtual bool deserialize(deserializer& in)
	{
		_virtual_platform = (virtual_platform_e)in.get_short();

		_net_adapters.deserialize(in);
		return true;
	}
};

//}//end namespace /*private */

//f:\~~~workspace\RHA-dev\common\db\p2v\p2v_controller.cpp
bool save_network_mapping_to_file(virtual_platform_e vpe, const std::vector<adapter_map_info>& net_adapters, CONST WCHAR * pFileName)
{
	////XOTR_ENTER(p2v_controller::_save_network_mapping_to_file);
	// switchover and AR host can have network mapping setting both
	// so, we always need copy the setting when do switchover or AR
	p2v_config_info p2v_conf;
	p2v_conf._virtual_platform = vpe;

	network_adapters& nas = p2v_conf._net_adapters;
	for (size_t i = 0; i < net_adapters.size(); ++i)
	{
		adapter_info ni;
		ni.use_original = net_adapters[i].use_original_setting;
		ni.ip_use_dhcp = net_adapters[i].ip_use_dhcp; //<sonmi01>2012-10-19 ###???
		ni.dns_use_dhcp = net_adapters[i].dns_use_dhcp;
		ni.id = net_adapters[i].device_id.c_str();
		ni.mac_address = net_adapters[i].mac_address.c_str();

		for (size_t j = 0; j < net_adapters[i].gateways.size(); ++j)
			ni.gateways.push_back(net_adapters[i].gateways[j].c_str());
		for (size_t j = 0; j < net_adapters[i].ips.size(); ++j)
			ni.ips.push_back(net_adapters[i].ips[j].c_str());
		for (size_t j = 0; j < net_adapters[i].subnets.size(); ++j)
			ni.subnets.push_back(net_adapters[i].subnets[j].c_str());
		for (size_t j = 0; j < net_adapters[i].dns.size(); ++j)
			ni.dns.push_back(net_adapters[i].dns[j].c_str());
		ni.wins_primary = net_adapters[i].wins_p.c_str();
		ni.wins_secondary = net_adapters[i].wins_s.c_str();

		nas.adapters.push_back(ni);
	}

	// serialize it
	//std::wstringstream dir;
	//std::wstring sys_folder_volume_name = L"C:\\";
	//dir << sys_folder_volume_name;
	std::wstring full_name = pFileName;
	{
		std::ofstream ofs(full_name.c_str(), std::ios_base::binary);
		if (!ofs)
		{
			////xotr_nl(XOTR_ERROR, "Fail to open output file to serialize customized network setting");
			return false;
		}	

		serializer szer(ofs);
		p2v_conf.serialize(szer);
		////xotr_nl(XOTR_DEBUG, "P2V configuration file (p2v_config.dat) is saved.");
	}

	//{
	//	//debug purpose only
	//	ifstream ifs(full_name.c_str(), std::ios_base::binary);
	//	deserializer ds(ifs);
	//	p2v_config_info p2vcfg;
	//	p2vcfg.deserialize(ds);
	//	ifs.close();
	//}
	
	return true;
}


bool load_network_mapping_from_file(OUT virtual_platform_e & vpe, OUT std::vector<adapter_map_info>& net_adapters, CONST WCHAR * pFileName)
{
	ifstream ifs(pFileName, std::ios_base::binary);
	if (!ifs)
	{
		////xotr_nl(XOTR_ERROR, "Fail to open output file to serialize customized network setting");
		return false;
	}

	deserializer ds(ifs);
	p2v_config_info p2vcfg;
	p2vcfg.deserialize(ds);

	vpe = p2vcfg._virtual_platform;
	for (size_t ii = 0; ii < p2vcfg._net_adapters.adapters.size(); ++ii)
	{
		const adapter_info & ai = p2vcfg._net_adapters.adapters[ii];
		adapter_map_info ami;

		ami.replicate_mac = false; //<sonmi01>2012-7-26 ###???

		ami.use_original_setting = ai.use_original;
		ami.ip_use_dhcp = ai.ip_use_dhcp;
		ami.dns_use_dhcp = ai.dns_use_dhcp;
		ami.device_id = ai.id;
		ami.mac_address = ai.mac_address;

		for (size_t j = 0; j < ai.gateways.size(); ++j)
			ami.gateways.push_back((LPCSTR)ai.gateways[j]);

		for (size_t j = 0; j < ai.ips.size(); ++j)
			ami.ips.push_back((LPCSTR)ai.ips[j]);

		for (size_t j = 0; j < ai.subnets.size(); ++j)
			ami.subnets.push_back((LPCSTR)ai.subnets[j]);

		for (size_t j = 0; j < ai.dns.size(); ++j)
			ami.dns.push_back((LPCSTR)ai.dns[j]);


		ami.wins_p = ai.wins_primary;
		ami.wins_s = ai.wins_secondary;


		net_adapters.push_back(ami);
	}

	return true;
}

} //end NSRVCM
SET default_parallel 20; 
REGISTER MyUDF.jar;  

A = load '$clk_path'  USING PigStorage('$delimiter')  AS (log_version:chararray,req_time:chararray,log_time:chararray,app_id:chararray,cp_id:chararray,uuid1:chararray,uuid2:chararray,terminal_type:chararray,pingpai_type:chararray,os:chararray,sdk_type:chararray,sdk_version:chararray,oper_company:chararray,net_type:chararray,ad_width:chararray,ad_heigth:chararray,screent_width:chararray,screent_height:chararray,is_henping:chararray,is_broke:chararray,ip_address:chararray,app_name:chararray,bundle_id:chararray,ad_id:chararray,ad_group_id:chararray,ad_group_type:chararray,app_type:chararray,ssnid:chararray,terminal_type_2:chararray,country:chararray,province:chararray,city:chararray,tele_company:chararray,ad_owner:chararray,ad_activity:chararray);

shw = load '$shw_path' USING PigStorage('$delimiter')  AS (log_version:chararray,req_time:chararray,log_time:chararray,app_id:chararray,cp_id:chararray,uuid1:chararray,uuid2:chararray,terminal_type:chararray,pingpai_type:chararray,os:chararray,sdk_type:chararray,sdk_version:chararray,oper_company:chararray,net_type:chararray,ad_width:chararray,ad_heigth:chararray,screent_width:chararray,screent_height:chararray,is_henping:chararray,is_broke:chararray,ip_address:chararray,app_name:chararray,bundle_id:chararray,ad_id:chararray,ad_group_id:chararray,ad_group_type:chararray,app_type:chararray,ssnid:chararray,terminal_type_2:chararray,country:chararray,province:chararray,city:chararray,tele_company:chararray,ad_owner:chararray,ad_activity:chararray);

apps = load '$apps_path' AS
(id:chararray, appId:chararray, cpId:chararray, freq:chararray, name:chararray, appintro:chararray, url:chararray, terType:chararray, appScreenShort:chararray, appCategory:chararray, createTime:chararray, updateTime:chararray, status:chararray, userId:chararray);

ads = load '$ads_path' AS
(id:chararray, adId:chararray, name:chararray, adCampaignId:chararray, adGroupId:chararray, advertiserId:chararray, showType:chararray, deviceType:chararray, type:chararray, content:chararray, userId:chararray, picUrl:chararray, clickAction:chararray, clickDetail:chararray,  status:chararray, idDel:chararray, updateTime:chararray, createTime:chararray);

clk = FOREACH A GENERATE ssnid AS is_click; 

shw_clk = JOIN shw BY ssnid LEFT OUTER, clk BY is_click;

ads_shw_clk = JOIN ads BY adId RIGHT OUTER, shw_clk BY ad_id;

apps_ads_shw_clk = JOIN apps BY appId RIGHT OUTER, ads_shw_clk BY app_id;

DESCRIBE apps_ads_shw_clk; 

F = FOREACH apps_ads_shw_clk GENERATE 
	shw::ssnid,
	((is_click is null)?0:1),
	log_version, 
	UDF.DateTime(shw::req_time, 'Day') as Req_Day,
	UDF.DateTime(shw::req_time, 'Time') as Req_Time,
	app_id,
	cp_id,
	terminal_type, 
	os,
	sdk_type,
	sdk_version,
	oper_company, 
	net_type, 
	CONCAT(CONCAT(ad_width, 'X'), ad_heigth),
	is_henping, 
	is_broke, 
	UDF.AppCate(app_type,'1'),
	UDF.AppCate(app_type,'2'),
	UDF.AppCate(app_type,'3'),
	UDF.AppCate(app_type,'4'),
	UDF.AppCate(app_type,'5'),
	UDF.AppCate(app_type,'6'),
	UDF.AppCate(app_type,'7'),
	UDF.AppCate(app_type,'8'),
	UDF.AppCate(app_type,'9'),
	UDF.AppCate(app_type,'10'),
	UDF.AppCate(app_type,'11'),
	UDF.AppCate(app_type,'12'),
	UDF.AppCate(app_type,'13'),
	UDF.AppCate(app_type,'14'),
	UDF.AppCate(app_type,'15'),
	UDF.AppCate(app_type,'16'),
	UDF.AppCate(app_type,'17'),
	UDF.AppCate(app_type,'18'),
	UDF.AppCate(app_type,'19'),
	UDF.AppCate(app_type,'20'),
	UDF.AppCate(app_type,'21'),
	province, 
	UDF.WeekCounter(apps::createTime) as app_createTime,
	(((apps::updateTime is null) or (ads::updateTime=='\\N') )? UDF.WeekCounter(apps::createTime) : UDF.WeekCounter(apps::updateTime)) as app_updateTime,
	ad_id, 
	ad_group_id, 
	ad_owner, 
	ad_activity, 
	showType, 
	deviceType, 
	clickAction, 
	UDF.WeekCounter(ads::createTime) as ad_createTime,
	(((ads::updateTime is null) or (ads::updateTime=='\\N') )? UDF.WeekCounter(ads::createTime) : UDF.WeekCounter(ads::updateTime)) as ad_updateTime;
STORE F into '$output';

geoip.inc、geoipcity.inc、geoipregionvars.php：PHP的库，通过
来访者的IP， 定位他的经纬度，国家/地区，省市，甚至街道等位置信息。

GeoLiteCity.dat：IP库

geo_getcitywords.php：依赖GeoIP库，作用是接受请求根据访问者的IP地址
分为不同地区查询MySQL中储存的热词数据，返回国家名和热词数据。
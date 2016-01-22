<?php

header('Content-Type:text/plain;charset=UTF-8');

$ip = $_SERVER['REMOTE_ADDR'];

get_country($ip);

function get_country($ip_address)
{

// This code demonstrates how to lookup the country, region, city,
// postal code, latitude, and longitude by IP Address.
// It is designed to work with GeoIP/GeoLite City

// Note that you must download the New Format of GeoIP City (GEO-133).
// The old format (GEO-132) will not work.

    require_once("geoipcity.inc");
    require_once("geoipregionvars.php");

// uncomment for Shared Memory support
// geoip_load_shared_mem("/usr/local/share/GeoIP/GeoIPCity.dat");
// $gi = geoip_open("/usr/local/share/GeoIP/GeoIPCity.dat",GEOIP_SHARED_MEMORY);

    $gi = geoip_open("GeoLiteCity.dat",GEOIP_STANDARD);

    $geo_data = array();

    if (!$record = geoip_record_by_addr($gi, $ip_address)) {
        $geo_data['geo_country'] = 'US';
        $geo_data['geo_countryName'] = 'United States';
        return false;
    }


    $geo_data['geo_country'] = $record->country_code;
    $geo_data['geo_countryName'] = $record->country_name;

    geoip_close($gi);

    $key = strtolower($geo_data['geo_country']);
    $keywordNum = 10;
    $keywordLIst = [];
    if ($key=='cn') {
      $conn = mysqli_connect("localhost", 'root', 'cxc705296', 'baidutrends', '3306');
      if (!$conn)
      {
          die("Connection error: " . mysqli_connect_errno());
      }
      $sql='SET NAMES UTF8';
      mysqli_query($conn,$sql);

      //中国热词查询语句
      $sql = "SELECT keyword FROM keyword LIMIT 0,$keywordNum";

      $result = mysqli_query($conn, $sql);
      while ($row = mysqli_fetch_assoc($result)){
           $keywordLIst[] = $row['keyword'];
       }
       mysqli_free_result($result);

       $geo_data['keyword'] = $keywordLIst;
	   }else {
      $conn = mysqli_connect("localhost", 'root', 'cxc705296', 'googletrends', '3306');
      if (!$conn)
      {
          die("Connection error: " . mysqli_connect_errno());
      }
      $sql='SET NAMES UTF8';
      mysqli_query($conn,$sql);

      //其他国家热词查询语句
      $sql = "SELECT keyword FROM keyword,country WHERE st='$key' AND keyword.country=country.country LIMIT 0,$keywordNum";

      $result = mysqli_query($conn, $sql);
      if (mysqli_affected_rows($conn)==0){
          $result = mysqli_query($conn, "SELECT keyword FROM keyword,country WHERE st='us' AND keyword.country=country.country LIMIT 0,$keywordNum");
       }
       while ($row = mysqli_fetch_assoc($result)){
           $keywordLIst[] = $row['keyword'];
       }
       mysqli_free_result($result);

      $geo_data['keyword'] = $keywordLIst;
    }


    $json_string=json_encode($geo_data,JSON_UNESCAPED_UNICODE);
    echo $json_string;

}

package com.airdnd.back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.AirdndSearchService;
import vo.AirdndHomePictureVO;
import vo.AirdndSearchVO;

@Controller
public class SearchController {

	@Autowired
	AirdndSearchService airdndsearchService;

	@RequestMapping(value="/search",
			method=RequestMethod.GET, produces = "application/json;charset=utf8", consumes = MediaType.ALL_VALUE)
	@ResponseBody         // 어디검색, 몇박며칠, 인원수...
	public String check(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="location", required=false)String location, @RequestParam(value="checkIn", defaultValue="0")String checkIn,
			@RequestParam(value="checkOut", defaultValue="0")String checkOut, @RequestParam(value="guests", defaultValue="0")int guests,
			@RequestParam(value="refund", defaultValue="0")boolean refund, @RequestParam(value="roomTypeHouse", defaultValue="0")boolean roomTypeHouse,
			@RequestParam(value="roomTypePrivate", defaultValue="0")boolean roomTypePrivate, @RequestParam(value="roomTypeShared", defaultValue="0")boolean roomTypeShared,
			@RequestParam(value="neLat", defaultValue="84")double neLat, @RequestParam(value="neLng", defaultValue="0")double neLng,
			@RequestParam(value="swLat", defaultValue="0")double swLat, @RequestParam(value="swLng", defaultValue="180")double swLng,
			@RequestParam(value="priceMin", defaultValue="0")int priceMin, @RequestParam(value="priceMax", defaultValue="2147483646")int priceMax,
			@RequestParam(value="instantBooking", defaultValue="0")boolean instantBooking, @RequestParam(value="bedCount", defaultValue="0")int bedCount,
			@RequestParam(value="bedroomCount", defaultValue="0")int bedroomCount, @RequestParam(value="bathCount", defaultValue="0")int bathCount,
			@RequestParam(value="convenience", defaultValue="0")boolean convenience, @RequestParam(value="amenityList", defaultValue="0")String amenityList,
			@RequestParam(value="facilityList", defaultValue="0")String facilityList, @RequestParam(value="hostLangList", defaultValue="0") String hostLangList,
			@RequestParam(value="page", defaultValue="0")int page) {

		try {
			location = URLDecoder.decode(location, "utf-8");
			checkIn = URLDecoder.decode(checkIn, "utf-8");
			checkOut = URLDecoder.decode(checkOut, "utf-8");

			amenityList = URLDecoder.decode(amenityList, "utf-8");
			facilityList = URLDecoder.decode(facilityList, "utf-8");
			hostLangList = URLDecoder.decode(hostLangList, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject res = new JSONObject();
		JSONObject empty = new JSONObject();
		List empty_list = new ArrayList<Object>();

		if(!(location.equals("서울") || location.equals("제주도") || location.equals("괌") || location.equals("하와이"))) {
			res.put("homes", empty_list);
			res.put("dataTotal", 0);
			res.put("filterCondition", empty);
			res.put("priceArray", empty_list);
			res.put("averagePrice", 0);
			res.put("recenthomes", empty_list);
		}else {

			//search_list : 페이지별 숙소 리스트------------------
			Map<Object, Object> param = new HashMap();

			param.put("location", location);
			param.put("page", page);
			param.put("guests", guests);
			param.put("priceMin", priceMin);
			param.put("priceMax", priceMax);
			param.put("bedCount", bedCount);
			param.put("bedroomCount", bedroomCount);
			param.put("bathCount", bathCount);
			param.put("neLat", neLat);
			param.put("neLng", neLng);
			param.put("swLat", swLat);
			param.put("swLng", swLng);
			System.out.println("ddddd" + neLat + " / " + neLng + " / " + swLat + " / " + swLng + " / ");
			System.out.println("fffff" + param.get("neLat") + " / " + param.get("neLng") + " / " + param.get("swLat") + " / " + param.get("swLng") + " / ");
			//if(filter_roomType_house)

			List<AirdndSearchVO> search_list = airdndsearchService.searchselect(param);
			int size = search_list.size();

			List<JSONObject> homes = new ArrayList<JSONObject>();

			Double[] maxmin_lat = new Double[2];
			Double[] maxmin_lng = new Double[2];

			for(int i = 0; i < size; i++) {
				int home_idx = search_list.get(i).getHome_idx();

				List<AirdndHomePictureVO> picture = airdndsearchService.pictureselect(home_idx);
				List<String> picture_arr = new ArrayList<String>();

				JSONObject homes_info = new JSONObject();
				JSONObject latlng = new JSONObject();

				for(int j = 0; j < picture.size(); j++) {
					picture_arr.add(picture.get(j).getUrl());
				}

				search_list.get(i).setUrl(picture_arr);
				double lat = Double.parseDouble(search_list.get(i).getLat());
				double lng = Double.parseDouble(search_list.get(i).getLng());

				for(int j = 0; j < homes.size(); j++) {
					JSONObject lo = (JSONObject) homes.get(j).get("location");

					if(lat == (Double)(lo.get("lat")) && lng == (Double)lo.get("lng")) {
						lat += 0.01;
					}
				}

				latlng.put("lat", lat);
				latlng.put("lng", lng);

				if(maxmin_lat[0] == null) maxmin_lat[0] = lat;
				else if(lat < maxmin_lat[0]) maxmin_lat[0] = lat;

				if(maxmin_lat[1] == null) maxmin_lat[1] = lat;
				else if(lat > maxmin_lat[1]) maxmin_lat[1] = lat;

				if(maxmin_lng[0] == null) maxmin_lng[0] = lng;
				else if(lng < maxmin_lng[0]) maxmin_lng[0] = lng;

				if(maxmin_lng[1] == null) maxmin_lng[1] = lng;
				else if(lng > maxmin_lng[1]) maxmin_lng[1] = lng;


				homes_info.put("homeId", search_list.get(i).getHome_idx());
				homes_info.put("isSuperhost", search_list.get(i).getIsSuperHost());
				homes_info.put("isBookmarked", "아직안받아옴");
				homes_info.put("imageArray", search_list.get(i).getUrl());
				homes_info.put("imageCount", search_list.get(i).getUrl().size());
				homes_info.put("subTitle", search_list.get(i).getSub_title());
				homes_info.put("title", search_list.get(i).getTitle());
				homes_info.put("feature", "최대 인원 " + search_list.get(i).getFilter_max_person() + "명 · 침실 " + search_list.get(i).getFilter_bedroom() + 
						"개 · 침대 " + search_list.get(i).getFilter_bed() + "개 · 욕실 " + search_list.get(i).getFilter_bathroom() + "개");
				homes_info.put("rating", search_list.get(i).getRating());
				homes_info.put("reviewCount", search_list.get(i).getReview_num());
				homes_info.put("price", search_list.get(i).getPrice());
				homes_info.put("location", latlng);
				homes.add(homes_info);
			}

			JSONObject mapCenter = new JSONObject();
			Double avgLat;
			Double avgLng;
			try {
				avgLat =(Math.round((maxmin_lat[0] + maxmin_lat[1])/2*10000000)/10000000.0); 
				avgLng =(Math.round((maxmin_lng[0] + maxmin_lng[1])/2*10000000)/10000000.0);
			} catch (Exception e) {
				avgLat = (swLat+neLat)/2; 
				avgLng = (swLng+neLng)/2;
			}
			
			mapCenter.put("lat", avgLat);
			mapCenter.put("lng", avgLng);
			res.put("mapCenter",mapCenter);
			//Double avgLng = (double) (Math.round((addLng/size)*10000000)/10000000);

			res.put("homes", homes);

			//가격 분포도--------------------------------
			List<AirdndSearchVO> pricelist = airdndsearchService.unitpriceselect(location);
			List<Integer> price_array = new ArrayList();
			int start = 0;
			int end = 2;
			int save_num = 0;
			for(int i = 0; i < 50; i++) {
				for(AirdndSearchVO price : pricelist) {
					if(start <= (int)price.getPrice()/10000 && end > (int)price.getPrice()/10000) {
						save_num++;
					}
				}
				price_array.add(save_num);
				start += 2;
				end += 2;
				save_num = 0;
			}

			res.put("priceArray", price_array);

			//필터조건들 ---------------------------------
			List<AirdndSearchVO> facilities = airdndsearchService.facilityList(param);
			//List<AirdndUserVO> hostlanlists = airdndsearchService.hostLanlist(location);

			List<String> facilityListStr= new ArrayList<String>();
			List<String> amenityListStr = new ArrayList<String>();
			List<String> hostLangListStr = new ArrayList<String>();

			for(AirdndSearchVO filter : facilities) {
				if( !filter.getFacilityList().contains("이용 불가:") ) {
					String element = filter.getFacilityList();
					if( element.contains("수영장") ||  element.contains("주차") || element.contains("자쿠지") || element.contains("헬스장") ) {
						facilityListStr.add(element);
					} else {
						amenityListStr.add(element);
					}
				}
			}
			hostLangListStr.add("영어");
			hostLangListStr.add("프랑스어");
			hostLangListStr.add("한국어");
			hostLangListStr.add("스페인어");
			hostLangListStr.add("중국어");

			//for(AirdndUserVO filter : hostlanlists) {
			//  hostlanguagelist.add(filter.getHostLanlist());
			//}

			JSONObject filterCondition = new JSONObject();
			if( amenityListStr.size()!=0) filterCondition.put("amenityList", amenityListStr);
			if( facilityListStr.size()!=0) filterCondition.put("facilityList", facilityListStr);
			filterCondition.put("hostLangList", hostLangListStr);

			//--------최근 본 목록 코드 작성------------ 
			List<JSONObject> recentHomes = new ArrayList<JSONObject>();//이걸 쿠키로 받아와 검색하는 쿼리문
			List<Integer> recentHomesIdx = new ArrayList<Integer>();
			List<AirdndSearchVO> recentHomeOne = new ArrayList<AirdndSearchVO>();

			Cookie[] cookies = request.getCookies();

			if(cookies == null) {
			}else{
				for (Cookie cookie : cookies) {
					if(cookie.getName().contains("AirdndRH")) {
						recentHomesIdx.add(Integer.parseInt(cookie.getName()));

						//int recentHomesIdx[] = {596431, 4010129, 4165392};
						for(int recenthome:recentHomesIdx) {
							recentHomeOne = airdndsearchService.select_one(recenthome);

							List<AirdndHomePictureVO> recentPicture = airdndsearchService.pictureselect(recenthome);

							List<String> recent_picture_arr = new ArrayList<String>();

							for(int j = 0; j < recentPicture.size(); j++) {
								recent_picture_arr.add(recentPicture.get(j).getUrl());
							}

							JSONObject recentHome_info = new JSONObject();
							JSONObject latlng = new JSONObject();

							String lat = recentHomeOne.get(0).getLat();
							String lng = recentHomeOne.get(0).getLng();
							latlng.put("lat", lat);
							latlng.put("lng", lng);

							recentHomeOne.get(0).setUrl(recent_picture_arr);
							recentHome_info.put("homeId", recentHomeOne.get(0).getHome_idx());
							recentHome_info.put("isSuperhost", recentHomeOne.get(0).getIsSuperHost());
							recentHome_info.put("isBookmarked", "아직안받아옴");
							recentHome_info.put("imageArray", recentHomeOne.get(0).getUrl());
							recentHome_info.put("imageCount", recentHomeOne.get(0).getUrl().size());
							recentHome_info.put("subTitle", recentHomeOne.get(0).getSub_title());
							recentHome_info.put("title", recentHomeOne.get(0).getTitle());
							recentHome_info.put("rating", recentHomeOne.get(0).getRating());
							recentHome_info.put("reviewCount", recentHomeOne.get(0).getReview_num());
							recentHome_info.put("price", recentHomeOne.get(0).getPrice());
							recentHome_info.put("location", latlng);

							recentHomes.add(recentHome_info);

						}
						//recentHomeList만 뿌려주면 끝
					}
				}
			}
			
			res.put("filterCondition", filterCondition);
			//전체 숙소 데이터 개수, 1박 평균 가격 -----------------
			List<AirdndSearchVO> total = airdndsearchService.searchtotalselect(param);

			res.put("recentHomes", recentHomes);

			try {
				res.put("dataTotal", total.get(0).getData_total());
				res.put("averagePrice", total.get(0).getAverage_price());
			} catch (Exception e) {
				res.put("dataTotal", 0);
				res.put("averagePrice", 0);
			}
			
		}

		return res.toString();
	}
}
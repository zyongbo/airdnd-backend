create database AirdndDB character set=utf8;
commit;
show databases;

use AirdndDB;

# HOME DB
create table airdnd_home(
	home_idx int not null unique primary key, 
    place varchar(50) not null, #어딘지
	title varchar(500) not null, #큰제목
    isSuperHost boolean not null, #슈퍼호스트있는지
    addr varchar(500) not null, #괌
	lat varchar(30) not null, #위
    lng varchar(30) not null, #경
    sub_title varchar(500) not null, #사진밑그제목 전체그놈
    filter_max_person int not null, #최대인원
    filter_bedroom int not null, #침실
    filter_bed int not null, #침대
    filter_bathroom int not null, #욕실
    price int(10) not null, #가격
    host_notice text not null, #호스트말
    loc_info varchar(5000) #거리옆에 그거 설명들
    ) engine=InnoDB character set = utf8;


drop view airdnd_search_view_final;
# Home View DB
create view airdnd_search_view as select h.place, h.home_idx, h.isSuperHost, h.sub_title, h.title, h.filter_max_person,
		h.filter_bedroom, h.filter_bed, h.filter_bathroom, h.price, round(((avg(h_r.room_cleanliness) +
        avg(h_r.room_accuracy) + avg(h_r.room_communication) + avg(h_r.room_position) + avg(h_r.room_checkin) +
        avg(h_r.room_cost_effectiveness))/6),1) as rating, count(*) as review_num, h.lat, h.lng, h_host.host_language 
        from airdnd_home as h, airdnd_home_review as h_r, airdnd_host as h_host where h_r.home_idx = h.home_idx group by h_r.home_idx; 

create view airdnd_search_view2 as select v.place, v.home_idx, v.isSuperHost, v.sub_title, v.title, v.filter_max_person,
		v.filter_bedroom, v.filter_bed, v.filter_bathroom, v.price, v.rating, v.review_num, v.lat, v.lng, h.host_language
        from airdnd_search_view as v, airdnd_host as h where v.home_idx = h.home_idx;

create view airdnd_search_view_sub as SELECT home_idx, group_concat(facility) as facility FROM airdnd_home_convenient_facility group by home_idx;

create view airdnd_search_view_final as select v.place, v.home_idx, v.isSuperHost, v.sub_title, v.title, v.filter_max_person,
		v.filter_bedroom, v.filter_bed, v.filter_bathroom, v.price, v.rating, v.review_num, v.lat, v.lng, v.host_language,
        s.facility from airdnd_search_view2 as v, airdnd_search_view_sub as s where v.home_idx = s.home_idx;

create view airdnd_facility_view as select facility from airdnd_home_convenient_facility where home_idx = home_idx;

# USER DB
create table airdnd_user(
	user_idx int(9) NOT NULL auto_increment primary key,
	email varchar(100) NOT NULL,
	pwd varchar(100) NOT NULL,
	last_name varchar(200) NOT NULL,
	first_name varchar(200) NOT NULL,
	birthday date NOT NULL,
	profileImg varchar(100) default null,
	phone varchar(100) default null,
	signupDate DATE NOT NULL,
	description TEXT(10000) default null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_picture(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	url VARCHAR(300) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_user_host(
	idx int(9) NOT NULL auto_increment,
	user_idx int(9) NOT NULL,
	host_idx int(9) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_home_notice(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	home_notice_sort VARCHAR(100) NOT NULL,
    home_notice_content VARCHAR(300) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_convenient_facility(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	facility VARCHAR(200) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_review(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	user_name VARCHAR(200) NOT NULL,
	review_date DATE NOT NULL,
	review_content VARCHAR(10000) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_attractions_distance(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	attractions_name VARCHAR(200) NOT NULL,
	attractions_distance VARCHAR(200) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_use_rule(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	use_rule VARCHAR(200) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_safety_rule(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	safety_rule VARCHAR(200) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_bed(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	bed_room_name VARCHAR(200) NOT NULL,
	bed_room_option VARCHAR(200) NOT NULL,
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table airdnd_host(
	idx int(9) NOT NULL auto_increment,
	home_idx int(9) NOT NULL,
	host_name varchar(100) NOT NULL,
	host_sign_in_date varchar(100) NOT NULL,
	check_superhost int(3) DEFAULT 0,
	check_certification int(3) DEFAULT 0,
	host_review_num int(3) DEFAULT 0,
	host_status_message VARCHAR(500),
	Interaction_with_guests VARCHAR(500),
	host_language VARCHAR(200),
	response_rate VARCHAR(200),
	response_time VARCHAR(200),
	PRIMARY KEY(idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#검색
select * from airdnd_home;
#view
drop view airdnd_search_view;

#칼럼 추가
alter table airdnd_home_review add idx int not null auto_increment primary key;
# USER DB 예시 --null, default 다 가능
insert into airdnd_user values(0, 'yujin0131@naver.com', '1111', 'lee', 'yujin', '2000-01-31', null, '01047403951', now(), DEFAULT); 
#삭제
truncate table airdnd_user;
truncate table airdnd_home;
truncate table airdnd_home_attractions_distance;
truncate table airdnd_home_bed;
truncate table airdnd_home_convenient_facility;
truncate table airdnd_home_notice;
truncate table airdnd_home_picture;
truncate table airdnd_home_review;
truncate table airdnd_home_safety_rule;
truncate table airdnd_home_use_rule;

delete from AirdndDB.airdnd_home where home_idx = 41319274;
delete from AirdndDB.airdnd_home_attractions_distance where home_idx = 41319274;
delete from AirdndDB.airdnd_home_bed where home_idx = 41319274;
delete from AirdndDB.airdnd_home_convenient_facility where home_idx = 41319274;
delete from AirdndDB.airdnd_home_notice where home_idx = 41319274;
delete from AirdndDB.airdnd_home_picture where home_idx = 41319274;
delete from AirdndDB.airdnd_home_review where home_idx = 41319274;
delete from AirdndDB.airdnd_home_safety_rule where home_idx = 41319274;
delete from AirdndDB.airdnd_home_use_rule where home_idx = 41319274;

#칼럼 default 추가
update airdnd_host set host_profileimg = 'https://a0.muscache.com/defaults/user_pic-225x225.png?v=3' where (host_profileimg is null);

#잡다한것들
select width_bucket(price, 0, 2000000, 50) as price from airdnd_search_view group by home_idx;
select * , if(isSuperHost, 'true', 'false') as isSuperHost from airdnd_search_view where place='제주도';
select AVG(price) as average_price, COUNT(home_idx) as data_total, price from airdnd_search_view where place = '서울';
select price from airdnd_search_view where price > 260000 and place='';

drop view airdnd_search_view;
select * from airdnd_search_view where title="arthome";
select * from airdnd_home_review where home_idx = 42301018;
#(select round(  from airdnd_home_review where h.home_idx = h_r.home_idx);

select round( avg((room_cleanliness + room_accuracy + room_communication + room_position + room_checkin + room_cost_effectiveness)/6) , 1)
	as rating from airdnd_home_review;

SELECT facility FROM airdnd_home_convenient_facility where 
home_idx = ANY(SELECT home_idx FROM airdnd_search_view where place="괌") group by facility;

select AVG(price) as average_price, COUNT(home_idx) as data_total from airdnd_search_view where place = '괌' Group by place;

   
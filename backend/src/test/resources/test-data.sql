--User:
INSERT INTO users(about, email, enabled, is_banned, first_name, password, restore_code, second_name, username, verification_code)
VALUES ('Its a test', 'test@test.test', true, false, 'test', 'test', null, 'st', 'test', null);

--Auctions:
--1)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-04 12:00:00','00:00:30','00:05:00','Золотые и серебряные монеты',3,1);
--2)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-24 08:30:00','00:00:30','00:05:00','Балерины',2,1);
--3)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-04 18:30:00','00:01:00','00:04:00','Мячи',5,1);
--4)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-05 12:30:00','00:01:00','00:05:00','Чашки',4,1);
--5)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-05 14:30:00','00:00:30','00:30:00','Консервы',3,1);
--6)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-22 07:30:00','00:00:30','00:05:00','Люстры',5,1);
--7)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-22 07:30:00','00:00:30','00:10:00','Чайники',5,1);
--8)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-22 16:00:00','00:00:30','00:05:00','Старая мебель',5,1);
--9)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-23 13:30:00','00:00:30','00:05:00','Изделия японских кузнецов',5,1);
--10)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-29 19:30:00','00:01:00','00:20:00','Пули старинные',5,1);
--11)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-11-29 15:30:00','00:02:00','00:05:00','Чучела животных',10,1);
--12)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-01 15:30:00','00:03:00','00:05:00','Шкуры животных',10,1);
--13)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-02 09:30:00','00:02:00','00:05:00','Статуи животных',12,1);
--14)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-02 14:30:00','00:01:00','00:20:00','Картины диких кошек',10,1);
--15)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-04 12:30:00','00:00:30','00:05:00','Картины природы',4,1);
--16)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-05 13:30:00','00:00:30','00:05:00','Современные картины',6,1);
--17)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-05 11:00:00','00:01:00','00:30:00','Digital картины',5,1);
--18)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-06 12:30:00','00:00:30','00:30:00','Старинные картины',6,1);
--19)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-25 13:30:00','00:01:00','00:10:00','Экшен-фигурки',4,1);
--20)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-07 14:00:00','00:00:30','00:05:00','Эксклюзивные вина',8,1);
--21)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-07 07:30:00','00:01:00','00:05:00','Старинные вина',4,1);
--22)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-08 08:30:00','00:01:00','00:05:00','Домашние вина',5,1);
--23)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-08 15:30:00','00:00:40','00:05:00','Моносортовый сидр',7,1);
--24)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-09 19:30:00','00:00:50','00:05:00','Коллекционный алкоголь',6,1);
--25)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-09 08:30:00','00:00:40','00:10:00','Изделия из натуральной шерсти',5,1);
--26)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-11 10:30:00','00:00:50','00:05:00','Мебель королевской семьи',8,1);
--27)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-11 12:30:00','00:01:00','00:05:00','Зеркала Московской фабрики',3,1);
--28)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-12 09:30:00','00:00:30','00:05:00','Мебель из редкого материала',8,1);
--29)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-12 14:30:00','00:01:00','00:05:00','Декоративная мебель',4,1);
--30)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-13 12:30:00','00:01:00','00:10:00','Одежда дизайнера Зверева',4,1);
--31)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-13 15:30:00','00:00:30','00:05:00','Одежда будущего',3,1);
--32)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-14 11:30:00','00:00:30','00:10:00','Ювелирные изделия',3,1);
--33)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-14 12:30:00','00:01:00','00:05:00','Драгоценные камни',4,1);
--34)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-14 18:30:00','00:01:00','00:05:00','Женские украшения',3,1);
--35)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-15 10:30:00','00:00:30','00:05:00','Саммые ценные и редкие монеты России',5,1);
--36)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-15 09:30:00','00:01:00','00:10:00','Коллекции монет',6,1);
--37)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-16 11:30:00','00:01:00','00:05:00','Монеты с историей',5,1);
--38)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-16 09:30:00','00:01:00','00:05:00','Памятные монеты',3,1);
--39)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-17 10:30:00','00:01:00','00:05:00','Коллекционное оружие',5,1);
--40)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-17 10:30:00','00:00:50','00:05:00','Декоративное оружие',6,1);
--41)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-18 16:30:00','00:00:30','00:05:00','Копии, реплики оружия',4,1);
--42)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-18 16:30:00','00:00:60','00:05:00','Копии, реплики оружия',3,1);
--43)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-19 13:30:00','00:00:50','00:05:00','Редкие сушеные травы',3,1);
--44)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-19 11:30:00','00:00:30','00:05:00','Редкие напитки',4,1);
--45)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-20 08:30:00','00:00:30','00:10:00','Трюфели',5,1);
--46)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-20 15:30:00','00:00:30','00:15:00','Соки редких деревьев',3,1);
--47)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-20 12:30:00','00:00:30','00:08:00','Винтажная одежда',4,1);
--48)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-21 16:30:00','00:00:30','00:07:00','Мужские аксессуары',3,1);
--49)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-21 12:30:00','00:00:50','00:06:00','Эко-одежда',3,1);
--50)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-22 11:30:00','00:02:00','00:05:00','Кимоно',3,1);
--51)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-22 21:30:00','00:01:00','00:05:00','Фарфоровая коллекция',4,1);
--52)
INSERT INTO auctions(begin_date, boost_time, lot_duration, name, users_limit, user_id)
VALUES ('2021-12-23 11:30:00','00:05:00','00:10:00','Статуэтки России до 1900г',4,1);
--------------------------------------------------------------
--TEST LOTS
insert into lots (description, min_bank, name, picture, step, auction_id)
values ('Борис Аникин родился в Ленинграде. В 1966 году поступил в Академию Художеств, которую окончил в 1974 году. Член Союза художников с 1982 года. Проиллюстрировал более 150 книг, которые можно найти практически в каждом российском доме. Многие из произведений художника находятся не только в частных коллекциях, но и в собраниях ведущих галерей Европы и Америки. Художник удостоен многих премий и дипломов художественных издательств и журналов.',
        1500,'Корабль','https://img-fotki.yandex.ru/get/9304/224845352.9d/0_d3013_417d7e96_orig', 1, 1);
insert into lots (description, min_bank, name, picture, step, auction_id)
values ('Борис Аникин родился в Ленинграде. В 1966 году поступил в Академию Художеств, которую окончил в 1974 году. Член Союза художников с 1982 года. Проиллюстрировал более 150 книг, которые можно найти практически в каждом российском доме. Многие из произведений художника находятся не только в частных коллекциях, но и в собраниях ведущих галерей Европы и Америки. Художник удостоен многих премий и дипломов художественных издательств и журналов.',
        2000,'Лондон','https://img-fotki.yandex.ru/get/9304/224845352.9d/0_d3008_a7d43d30_orig', 1, 1);
insert into lots (description, min_bank, name, picture, step, auction_id)
values ('Борис Аникин родился в Ленинграде. В 1966 году поступил в Академию Художеств, которую окончил в 1974 году. Член Союза художников с 1982 года. Проиллюстрировал более 150 книг, которые можно найти практически в каждом российском доме. Многие из произведений художника находятся не только в частных коллекциях, но и в собраниях ведущих галерей Европы и Америки. Художник удостоен многих премий и дипломов художественных издательств и журналов.',
        3500,'Зимний лес','https://barcaffe.ru/wp-content/uploads/2020/01/xudozhnik_aleksej_anikin_01.jpg', 1, 1);
--------------------------------------------------------------
--TEST AUCTION PROCESSES
INSERT INTO auction_processes(current_bank, remaining_time, auction_auction_id, lot_lot_id, user_user_id)
VALUES (20000, '00:05:00', 1, 1, 1);
insert into category (id,name,parent_id) values 
(1,'Clothes',null),
(2,'Food',null),
(3,'Snack',2);

insert into inventory (id,name,quantity,belong_to) values 
(1,'Potato Chips',99,3);
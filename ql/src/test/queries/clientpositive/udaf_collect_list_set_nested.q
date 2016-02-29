set hive.support.sql11.reserved.keywords=false;

-- initialize tables

CREATE TABLE customers (id int, name string, age int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH "../../data/files/customers.txt" INTO TABLE customers;

Create TABLE orders (id int, cid int, date date, amount double)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH "../../data/files/orders.txt" INTO TABLE orders;

Create TABLE nested_orders (id int, cid int, date date, sub map<string,double>)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
COLLECTION ITEMS TERMINATED BY '$'
MAP KEYS TERMINATED BY ':';

LOAD DATA LOCAL INPATH "../../data/files/nested_orders.txt" INTO TABLE nested_orders;

-- 1. test struct

-- 1.1 when field is primitive

SELECT c.id, collect_set(named_struct("date", o.date, "amount", o.amount))
FROM customers c
INNER JOIN orders o
ON (c.id = o.cid) GROUP BY c.id;

Select c.id, collect_list(named_struct("date", o.date, "amount", o.amount))
FROM customers c
INNER JOIN orders o
ON (c.id = o.cid) GROUP BY c.id;

-- 1.2 when field is map

SELECT c.id, collect_set(named_struct("date", o.date, "sub", o.sub))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

Select c.id, collect_list(named_struct("date", o.date, "sub", o.sub))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

-- 1.3 when field is list

SELECT c.id, collect_set(named_struct("date", o.date, "sub", map_values(o.sub)))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

SELECT c.id, collect_list(named_struct("date", o.date, "sub", map_values(o.sub)))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;


-- 2. test map

-- 2.1 when field is primitive

SELECT c.id, collect_set(map("amount", o.amount))
FROM customers c
INNER JOIN orders o
ON (c.id = o.cid) GROUP BY c.id;

Select c.id, collect_list(map("amount", o.amount))
FROM customers c
INNER JOIN orders o
ON (c.id = o.cid) GROUP BY c.id;

-- 2.2 when field is struct

SELECT c.id, collect_set(map("sub", o.sub))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

SELECT c.id, collect_list(map("sub", o.sub))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

-- 2.3 when field is list

SELECT c.id, collect_set(map("sub", map_values(o.sub)))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;

SELECT c.id, collect_list(map("sub", map_values(o.sub)))
FROM customers c
INNER JOIN nested_orders o
ON (c.id = o.cid) GROUP BY c.id;


-- clean up

DROP TABLE customer;
DROP TABLE orders;
DROP TABLE nested_orders

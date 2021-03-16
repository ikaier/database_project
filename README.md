# **Relational Model**

**Vaccination_Location**(<u>place_name</u>, postal_code, city, street_address)

**Location_DailyPlan**(<u>place_name</u>, <u>date</u> ,max_vaccine, max_slots) 

**Hospital**(<u>place_name</u>) 

**Nurse**(<u>license_number</u>, name, place_name) 

**Nurse_Work_Record**(<u>license_number, date, place_name</u>)

**Receiver**(<u>hinsurnum</u>, name, birthday, phone, postal_code, city, street_address, gender) 

**Critical_Situation**(<u>critical_category</u>, priority_level) 

**Receiver_Priority**(<u>hinsurnum</u>, <u>critical_category</u>) 

**Slot**(<u>place_name, date, Slot_time</u>)

**Allocate_Slot**(<u>hinsurnum, date, place_name, slot_time</u>, allocation_date)

**Vaccine_Batch**(<u>batch_number, vaccine_name</u>, vial_quantity, expired_date, manufacturing_date, required_does_quantity, date, place_name) 

**Vaccine_Does**(<u>batch_number, vaccine_name, vial_number</u>, license_number, hinsurnum, does_date, does_time) 

**Assign_Dose_To_Slot**(<u>slot_time,place_name,date,batch_number,vaccine_name,vial_number</u>) 





# Application interaction

### a

#### a i.  

```sql
SELECT COUNT(*) AS num FROM RECEIVER WHERE HINSURNUM='TEST00000000';
```

![ai](/ai.png)

#### a ii.  

<img src="/aii.png" alt="aii" style="zoom: 50%;" />

#### a iii. 

```sql
SELECT * FROM RECEIVER WHERE HINSURNUM='TEST00000000';
```

![aiii](/aiii.png)

```sql
SELECT * FROM RECEIVER_PRIORITY WHERE HINSURNUM='TEST00000000';
```

![aiii2](/aiii2.png)

#### a iv.

![aiv](/aiv.png)

#### a v.

```sql
SELECT * FROM RECEIVER WHERE HINSURNUM='TEST00000000';
```

![av2](/av2.png)

```sql
SELECT * FROM RECEIVER WHERE HINSURNUM='TEST00000000';
```

![av1](/av1.png)

### b

#### b i.

```sql
SELECT t1.HINSURNUM, COUNT(t2.HINSURNUM) AS num_shot 
FROM RECEIVER t1 LEFT JOIN 
    (SELECT HINSURNUM FROM VACCINE_DOES) t2 ON t1.HINSURNUM=t2.HINSURNUM 
WHERE t1.HINSURNUM='TEST00000003' GROUP BY t1.HINSURNUM
;
```

![bi](/bi.png)

#### b ii.

<img src="/bii.png" alt="bii" style="zoom:50%;" />

#### B iii.

```sql
SELECT * FROM ALLOCATE_SLOT WHERE HINSURNUM='TEST00000003';
```

![biii](/biii.png)

#### B iv

<img src="/bvi.png" alt="bvi" style="zoom: 50%;" />

#### b v.

```sql
SELECT * FROM ALLOCATE_SLOT WHERE HINSURNUM='TEST00000003';
```

![bv](/bv.png)

### C

#### c i.

```sql
SELECT VACCINE_NAME,REQUIRED_DOES_QUANTITY 
FROM VACCINE_BATCH WHERE VACCINE_NAME='Moderna COVID-19 Vaccine' 
GROUP BY VACCINE_NAME, REQUIRED_DOES_QUANTITY;
```

![ci](/ci.png)

#### c ii.

```sql
SELECT t1.HINSURNUM,VACCINE_NAME,num_shot 
FROM VACCINE_DOES t1 
    INNER JOIN (SELECT HINSURNUM,COUNT(*) AS num_shot FROM VACCINE_DOES GROUP BY HINSURNUM) t2 
        ON t1.HINSURNUM=t2.HINSURNUM 
WHERE num_shot=2 
GROUP BY t1.HINSURNUM, VACCINE_NAME, num_shot
;
```

![cii](/cii.png)

#### c iii.

<img src="/ciii.png" alt="ciii" style="zoom:50%;" />

#### c iv.

```sql
SELECT t1.PLACE_NAME,t1.DATE,t1.SLOT_TIME, COUNT(t2.PLACE_NAME) AS num_assigned FROM SLOT t1 LEFT JOIN
    (SELECT PLACE_NAME,DATE,SLOT_TIME FROM ALLOCATE_SLOT) t2
    ON t1.PLACE_NAME=t2.PLACE_NAME AND t1.DATE=t2.DATE AND t1.SLOT_TIME=t2.SLOT_TIME
WHERE t1.PLACE_NAME='Jewish General' AND t1.DATE='2021-03-20' AND t1.SLOT_TIME='09:50:00'
GROUP BY t1.PLACE_NAME, t1.DATE, t1.SLOT_TIME
;
```

![civ](/civ.png)

### d

#### d i.

<img src="/di.png" alt="di" style="zoom: 50%;" />

#### d ii

```sql
SELECT HINSURNUM,LICENSE_NUMBER,VIAL_NUMBER,DOES_DATE,DOES_TIME,PLACE_NAME 
FROM VACCINE_DOES 
    INNER JOIN VACCINE_BATCH VB 
        on VACCINE_DOES.BATCH_NUMBER = VB.BATCH_NUMBER and VACCINE_DOES.VACCINE_NAME = VB.VACCINE_NAME
WHERE VB.BATCH_NUMBER='88888888' AND VB.VACCINE_NAME='Moderna COVID-19 Vaccine';
```

![dii](/dii.png)

### e

#### e i.

```sql
SELECT t1.HINSURNUM,t2.VACCINE_NAME, COUNT(t2.HINSURNUM) AS num_shot
FROM RECEIVER t1 LEFT JOIN
     (SELECT HINSURNUM,VACCINE_NAME FROM VACCINE_DOES) t2 ON t1.HINSURNUM=t2.HINSURNUM
WHERE t1.HINSURNUM='TEST00000006' GROUP BY t1.HINSURNUM,t2.VACCINE_NAME
;
```

![ei](/ei.png)

#### e ii.

<img src="/eii.png" alt="eii" style="zoom:50%;" />

#### e iii.

<img src="/eiii.png" alt="eiii" style="zoom:45%;" />



# Index

```sql
CREATE INDEX receiver_phone_idx
ON receiver(phone)
;
```

![idx](/idx.png)

 If we assume everyone has an unique phone numberWe can add index on phone attribute. Database will sort the table according to the phone number.

Using index to search will be much more efficient then searching by a insurance number in a big table. When search without index, the database system needs to check if there is a match for every row of the whole table. However, by searching on an index, the database only need to walk through a subset of the table, which is denfintely faster than walking through the whole table.

There are also downsides for using index, updating or inerting rows into a table with index will be slower than before. So, it will be not so efficient to add index if we need to update or insert the table more frequently than query it. 

On the other hand, phone number is shot and simple, and most people remembered it. People usually don't make mistake on telling thier phone number. So, asking a person's phone number, then conform the health insurance number with him is more pratical and faster than searching him just with his health insurance number. 





# Data Analytics

```sql
SELECT FIRST_DOES_DATE, COUNT(*) AS num_people FROM(
SELECT HINSURNUM, MIN(DOES_DATE) AS FIRST_DOES_DATE FROM VACCINE_DOES GROUP BY HINSURNUM) GROUP BY FIRST_DOES_DATE;
```

![data](/data.png)




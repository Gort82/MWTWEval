create or replace NONEDITIONABLE FUNCTION          "BINARY_CHAR_TO_INTEGER" ( string_binary IN VARCHAR2 )
    RETURN NUMBER IS
       new_string_binary VARCHAR2(2000);
       length_string INT;
       final_result NUMBER;
    BEGIN
      length_string:=LENGTH(string_binary);
      new_string_binary:='';
      IF length_string > 0 THEN
        FOR i  IN 1..length_string
          LOOP
          IF i < LENGTH(string_binary) THEN
            new_string_binary := new_string_binary || SUBSTR(string_binary,i,1) ||',';
          ELSE
            new_string_binary := new_string_binary || SUBSTR(string_binary,i,1);
          END IF;
        END LOOP;
      ELSE
        new_string_binary := '0'; 
      END IF;
     EXECUTE IMMEDIATE 'SELECT BIN_TO_NUM('||new_string_binary||') FROM  dual' INTO final_result; 
     RETURN final_result;
 END BINARY_CHAR_TO_INTEGER;



create or replace NONEDITIONABLE FUNCTION "BITNOT" (x IN NUMBER) RETURN NUMBER AS 
BEGIN
    RETURN (0 - x) - 1;
END;


create or replace NONEDITIONABLE FUNCTION "BITOR" (x IN NUMBER, y IN NUMBER) RETURN NUMBER DETERMINISTIC IS
BEGIN
    RETURN x - bitand(x, y) + y;
END bitor;


create or replace NONEDITIONABLE FUNCTION "BITXOR" (x IN NUMBER, y IN NUMBER) RETURN NUMBER DETERMINISTIC IS
BEGIN
    RETURN x - bitand(x, y) + y;
END bitxor;



create or replace NONEDITIONABLE FUNCTION "CET_MSB_RANGE" (BINARY_VALUE IN VARCHAR2, MSB_ROOT IN INT) RETURN INT AS 
msb_pos INT;
BEGIN
  IF (SUBSTR(BINARY_VALUE,ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT)+1,1)='0') THEN
      IF(SUBSTR(BINARY_VALUE,ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT)-1,1)='0') THEN
         msb_pos := ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT);
      ELSE
         msb_pos := ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT)-1;
      END IF;
  ELSE
     IF(SUBSTR(BINARY_VALUE,ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT)-1,1)='0') THEN
         msb_pos := ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT);
     ELSE
         msb_pos := ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT)+1;
     END IF;
  END IF;
  RETURN msb_pos;
END CET_MSB_RANGE;



create or replace NONEDITIONABLE FUNCTION  "CONSIDER_VALUE" (BINARY_VALUE IN VARCHAR2, MSB_ROOT IN INT) RETURN VARCHAR2 AS 
binary_string VARCHAR2(400);
BEGIN
	IF SUBSTR(BINARY_VALUE,ROUND(LENGTH(BINARY_VALUE)/MSB_ROOT),1)='1' THEN
		binary_string := '_';
    ELSE
        binary_string := SUBSTR(BINARY_VALUE,1,CET_MSB_RANGE(BINARY_VALUE,MSB_ROOT));
    END IF;

  RETURN binary_string;
END CONSIDER_VALUE;


create or replace NONEDITIONABLE FUNCTION "CREATE_HAV" (current_id IN INT, SECRET_KEY IN VARCHAR2, ATTR_VALUE IN FLOAT) RETURN INT AS 
hav INT;
BEGIN
  SELECT ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => current_id || SECRET_KEY || ATTR_VALUE)) AS HAV INTO hav FROM dual;
  RETURN hav;
END CREATE_HAV;


create or replace NONEDITIONABLE FUNCTION  "CREATE_VPK" (current_id IN INT, SECRET_KEY IN VARCHAR2) RETURN INT AS 
vpk INT;
BEGIN
  SELECT ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => current_id || SECRET_KEY)) AS VPK INTO vpk FROM dual;
  RETURN vpk;
END CREATE_VPK;


create or replace NONEDITIONABLE FUNCTION "GENERATE_E" ( RELATION IN VARCHAR2, MSB_RANGE IN INT, LSB_COUNT IN INT, CANT_ATTR IN INT, ATTR_ID IN INT, PRIV_KEY IN VARCHAR2, TUPLES_FRACTION IN INT, IMAGE_HEIGHT IN INT, IMAGE_WIDTH IN INT) RETURN sys_refcursor AS
                                         
    id_cursor sys_refcursor;
    result_cursor sys_refcursor;
    attr_collect VARCHAR_ARRAY;
    general_cons INT;

BEGIN
    general_cons := TUPLES_FRACTION * CANT_ATTR;
    attr_collect := VARCHAR_ARRAY();
    attr_collect.EXTEND(CANT_ATTR);

    attr_collect(1):= 'ELEVATION';
    attr_collect(2):= 'ASPECT';
    attr_collect(3):= 'SLOPE';
    attr_collect(4):= 'HOR_DIST_TO_HYDROLOGY';
    attr_collect(5):= 'VERT_DIST_TO_HYDROLOGY';
    attr_collect(6):= 'HOR_DIST_TO_ROADWAYS';
    attr_collect(7):= 'HILLSHADE_9AM';
    attr_collect(8):= 'HILLSHADE_NOON';
    attr_collect(9):= 'HILLSHADE_3PM';
    attr_collect(10):= 'HOR_DIST_TO_FIRE_POINTS';


            OPEN result_cursor FOR   
                    'WITH aux  AS (SELECT ID as ID, '||ATTR_ID||' AS ATTR_ID, CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(NUM_TO_BIN(ABS('||attr_collect(ATTR_ID)||')),1,'||MSB_RANGE||')),'''||PRIV_KEY||''') AS VPK, '||attr_collect(ATTR_ID)||' AS ATTR_VALUE FROM '||RELATION ||'), 
                          aux2 AS (SELECT ID AS IDD, ATTR_ID AS ATTR_IDD, MOD(VPK,'||general_cons||'+1) AS SEED_BODY FROM aux) 

                     SELECT ID, ATTR_ID, VPK, ATTR_VALUE, 
                                              MOD(VPK,'||general_cons||') AS CONS_FACT, 
                                              ORA_HASH(VPK,'||IMAGE_HEIGHT||',SEED_BODY+1) AS H, 
                                              ORA_HASH(VPK,'||IMAGE_WIDTH||',SEED_BODY+2) AS W,
                                              ORA_HASH(VPK,'||MSB_RANGE||',SEED_BODY+4) AS MSB_POS, 
                                              ORA_HASH(VPK,'||LSB_COUNT||',SEED_BODY+3) AS LSB_POS 
                     FROM aux, aux2 
                     WHERE (ID = IDD) AND (ATTR_ID = ATTR_IDD) 
                     ORDER BY ATTR_VALUE';

  RETURN result_cursor;
END GENERATE_E;


create or replace NONEDITIONABLE FUNCTION  "GENERATE_FROM_MSB" (DECIMAL_VALUE IN INT, MSB_RANGE IN INT, PRIV_KEY IN VARCHAR2) RETURN INT AS 
    value_gen INT;
BEGIN
    value_gen := CREATE_VPK(BINARY_CHAR_TO_INTEGER(TO_CHAR(SUBSTR(num_to_bin(DECIMAL_VALUE),1,MSB_RANGE))),PRIV_KEY); 

     RETURN value_gen;
END GENERATE_FROM_MSB;



create or replace NONEDITIONABLE FUNCTION  "GENERATE_MEAN" (VALUE_LIST IN NUMBER_ARRAY) RETURN INT AS 
    mean_value FLOAT;
BEGIN
    FOR i IN 0..VALUE_LIST.COUNT LOOP
        mean_value := mean_value + VALUE_LIST(i);                
    END LOOP;
    mean_value := mean_value/VALUE_LIST.COUNT;
    RETURN mean_value;
END GENERATE_MEAN;



create or replace NONEDITIONABLE FUNCTION  "GENERATE_VALUE" (DECIMAL_VALUE IN INT, LSB_RANGE IN INT, PRIV_KEY IN VARCHAR2) RETURN INT AS 
    value_gen INT;
BEGIN
    value_gen := CREATE_VPK(BINARY_CHAR_TO_INTEGER(TO_CHAR(SUBSTR(num_to_bin(DECIMAL_VALUE),1,LENGTH(num_to_bin(DECIMAL_VALUE))-LSB_RANGE))),PRIV_KEY); 
     RETURN value_gen;
END GENERATE_VALUE;


create or replace NONEDITIONABLE FUNCTION  "GET_ALL_ROWS" (RELATION IN VARCHAR2) RETURN INT AS
  rows_counter INT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||RELATION INTO rows_counter;
  RETURN rows_counter;
END GET_ALL_ROWS;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTR_BEH" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(COUNT(*)) AS HIST_BEAN, AVG(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')))) AS AVG_DUPLICATED, MAX(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')))) AS MAX_DUPLICATED, MIN(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')))) AS MIN_DUPLICATED FROM '||RELATION ||' WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT||' GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||'))';

    RETURN inf_cursor;
END GET_ATTR_BEH;


create or replace NONEDITIONABLE FUNCTION "GET_ATTR_COUNT" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(*) AS COUNTER_ATTRR FROM '||RELATION ||' WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) > '||MSB_COUNT;
    RETURN inf_cursor;
END GET_ATTR_COUNT;


create or replace NONEDITIONABLE FUNCTION "GET_ATTR_GENSTAT" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT AVG('||ATTR_NAME ||') AS MEAN_ATTR, STDDEV('||ATTR_NAME ||') AS STD_ATTR FROM '||RELATION;
    RETURN inf_cursor;
END GET_ATTR_GENSTAT;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTR_HIST" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT '||ATTR_NAME ||' AS ITEM_VAL, COUNT(*) AS ITEMS_AMOUNT FROM '||RELATION ||' GROUP BY '||ATTR_NAME ||' ORDER BY '||ATTR_NAME;

    RETURN inf_cursor;
END GET_ATTR_HIST;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTR_HIST_COUNT" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2) RETURN INT AS 
  rows_counter INT;
BEGIN
    EXECUTE IMMEDIATE 'SELECT COUNT(COUNT(*)) AS COUNTER FROM '||RELATION ||' GROUP BY '||ATTR_NAME INTO rows_counter;
    RETURN rows_counter;
END GET_ATTR_HIST_COUNT;


create or replace NONEDITIONABLE FUNCTION "GET_ATTR_OF" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2,TUPLE_ID IN INT) RETURN NUMBER AS 
 attr_value NUMBER;
BEGIN
  EXECUTE IMMEDIATE 'SELECT '||ATTR_NAME||' FROM '||RELATION||' WHERE id =:1' INTO attr_value USING TUPLE_ID;
  RETURN attr_value;
END GET_ATTR_OF;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTR_POS" (HAV_VALUE IN VARCHAR2, ATTR_FRACTION IN INT, IMAGE_HEIGHT IN INT, IMAGE_WIDTH IN INT, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
  OPEN id_cursor FOR 'SELECT ORA_HASH('||HAV_VALUE||','||IMAGE_HEIGHT||', MOD('||HAV_VALUE||','||ATTR_FRACTION||'+1)+1) AS H,  ORA_HASH('||HAV_VALUE||','||IMAGE_WIDTH||', MOD('||HAV_VALUE||','||ATTR_FRACTION||'+1)+2) AS W, ORA_HASH('||HAV_VALUE||','||MSB_COUNT||', MOD('||HAV_VALUE||','||ATTR_FRACTION||'+1)+4) AS MSB_POS, ORA_HASH('||HAV_VALUE||','||LSB_COUNT||', MOD('||HAV_VALUE||','||ATTR_FRACTION||'+1)+3) AS LSB_POS FROM dual';
  RETURN id_cursor;
END GET_ATTR_POS;


create or replace NONEDITIONABLE FUNCTION "GET_ATTR_STAT" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT AVG('||ATTR_NAME ||') AS MEAN_ATTR, 
                                STDDEV('||ATTR_NAME ||') AS STD_ATTR 
                         FROM '||RELATION ||' 
                         WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT;
    RETURN inf_cursor;
END GET_ATTR_STAT;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTRS_OF" (IDENT IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN

     OPEN id_cursor FOR 'SELECT LLP_EMB,
                                ELEVATION,
                                ASPECT,
                                SLOPE,
                                HOR_DIST_TO_HYDROLOGY,
                                VERT_DIST_TO_HYDROLOGY,
                                HOR_DIST_TO_ROADWAYS,
                                HILLSHADE_9AM,
                                HILLSHADE_NOON,
                                HILLSHADE_3PM,
                                HOR_DIST_TO_FIRE_POINTS
                         FROM COVERTYPE_A WHERE LLP = ' || IDENT;

     RETURN id_cursor;
END GET_ATTRS_OF;


create or replace NONEDITIONABLE FUNCTION  "GET_ATTRSK_BEH" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT,PRIV_KEY IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(COUNT(*)) AS HIST_BEAN, 
                                AVG(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS AVG_DUPLICATED, 
                                MAX(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MAX_DUPLICATED, 
                                MIN(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MIN_DUPLICATED 
                         FROM '||RELATION ||' 
                         WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 
                         GROUP BY CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')';

    RETURN inf_cursor;
END GET_ATTRSK_BEH;


create or replace NONEDITIONABLE FUNCTION  "GET_AVG_ATTR" (IDENT IN INT) RETURN FLOAT AS 
  avg_attr FLOAT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT (ELEVATION + ASPECT + SLOPE + HOR_DIST_TO_HYDROLOGY + VERT_DIST_TO_HYDROLOGY + HOR_DIST_TO_ROADWAYS + 
       HILLSHADE_9AM + HILLSHADE_NOON + HILLSHADE_3PM + HOR_DIST_TO_FIRE_POINTS)/10 AS PROM FROM COVERTYPE_A WHERE LLP = ' || IDENT  INTO avg_attr;
  RETURN avg_attr;
END GET_AVG_ATTR;


create or replace NONEDITIONABLE FUNCTION "GET_CANT_ROWS" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2, TUPLES_FRACTION IN INT) RETURN INT AS
  rows_counter INT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||RELATION||' WHERE MOD(CREATE_VPK(id,'''||PRIV_KEY||'''),'||TUPLES_FRACTION||')=0' INTO rows_counter;
  RETURN rows_counter;
END GET_CANT_ROWS;


create or replace NONEDITIONABLE FUNCTION  "GET_ESCHEME_BRUTE" (RELATION IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(COUNT(*)) AS TOT_VALUE, MAX(COUNT(ALL_RES)) AS MAX_VALUE, MIN(COUNT(ALL_RES)) AS MIN_VALULE, AVG(COUNT(ALL_RES)) AS AVG_VALUE
      FROM (SELECT BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

        UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' 

      UNION ALL

      SELECT  BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||')

GROUP BY ALL_RES';

    RETURN inf_cursor;
END GET_ESCHEME_BRUTE;


create or replace NONEDITIONABLE FUNCTION  "GET_ESCHEME_PRACT" (RELATION IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT, PRIV_KEY IN VARCHAR2, TUPLE_FRACTION IN INT, ATTR_SIZE IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(COUNT(*)) AS TOT_VALUE, MAX(ALL_RES) AS MAX_VALUE, MIN(ALL_RES) AS MIN_VALULE, AVG(ALL_RES) AS AVG_VALUE
      FROM (SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

        UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0

      UNION ALL

      SELECT ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS ALL_RES 
      FROM '||RELATION ||' 
      WHERE (LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||') AND 
            MOD(ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')),'||TUPLE_FRACTION||'*'||ATTR_SIZE||')=0)

	GROUP BY ALL_RES';

    RETURN inf_cursor;
END GET_ESCHEME_PRACT;


create or replace NONEDITIONABLE FUNCTION  "GET_GENERAL_INFO" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2, TUPLES_FRACTION IN INT, IMAGE_HEIGHT IN INT, IMAGE_WIDTH IN INT, ATTR_COUNT IN INT, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 

    'WITH aux AS (SELECT ID as ID, G_I AS GR, DELETED AS DEL, CREATE_VPK(LLP,'''||PRIV_KEY||''') AS VPK, VPK_II
                  FROM '||RELATION ||'), 

          aux2 AS (SELECT ID AS IDD, MOD(VPK,'||TUPLES_FRACTION||'+1) AS SEED_BODY 
                   FROM aux) 

                   SELECT ID, GR, DEL, VPK, MOD(VPK,'||TUPLES_FRACTION||') AS CONS_FACT, 
                                   ORA_HASH(VPK,'||IMAGE_HEIGHT||',SEED_BODY+1) AS H, 
                                   ORA_HASH(VPK,'||IMAGE_WIDTH||',SEED_BODY+2) AS W,
                                   ORA_HASH(VPK,'||ATTR_COUNT||',SEED_BODY) AS ATTR_POS,  
                                   ORA_HASH(VPK,'||MSB_COUNT||',SEED_BODY+4) AS MSB_POS, 
                                   ORA_HASH(VPK,'||LSB_COUNT||',SEED_BODY+3) AS LSB_POS, VPK_II AS DIRECT_POS 
                  FROM aux, aux2 
                  WHERE ID = IDD 
                  ORDER BY ID';

  RETURN id_cursor;
END GET_GENERAL_INFO;



create or replace NONEDITIONABLE FUNCTION  "GET_HIST_COUNT" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN INT AS 
  rows_counter INT;
BEGIN
    EXECUTE IMMEDIATE 'SELECT COUNT(COUNT(*)) AS COUNTER FROM '||RELATION ||' WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT||' GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||'))' INTO rows_counter;
    RETURN rows_counter;
END GET_HIST_COUNT;


create or replace NONEDITIONABLE FUNCTION  "GET_INCL_MATRIX" (RELATION IN VARCHAR2, MSB_COUNT IN INT, PRIV_KEY IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 
        'SELECT ID AS ID, 
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(ELEVATION)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0

                    ELSE 1 
                END AS AT_01,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(ASPECT)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_02,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(SLOPE)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_03,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_04,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_05,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_06,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_9AM)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_07,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_NOON)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_08,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_3PM)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_09,
                CASE CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),'||MSB_COUNT||') 
                    WHEN ''_'' THEN 0
                    ELSE 1 
                END AS AT_10

               FROM '||RELATION ||'
         ORDER BY ID';

    RETURN inf_cursor;
END GET_INCL_MATRIX;


create or replace NONEDITIONABLE FUNCTION "GET_INDEX" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2, VS_LENGHT IN INT, ATTR_STRD IN VARCHAR2) RETURN sys_refcursor AS 
    id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'SELECT MOD(ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => ID ||'''||PRIV_KEY||''')),'||VS_LENGHT||') AS INDX_ELEM, LLP AS PK, '||ATTR_STRD||' AS STORE_VAL
        FROM '||RELATION;

    RETURN id_cursor;
END GET_INDEX;


create or replace NONEDITIONABLE FUNCTION  "GET_LENGTH_RANGE" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT MAX(LENGTH(num_to_bin('||ATTR_NAME ||'))) AS MAX_LENGTH, MIN(LENGTH(num_to_bin('||ATTR_NAME ||'))) AS MIN_LENGTH FROM '||RELATION;

    RETURN inf_cursor;
END GET_LENGTH_RANGE;


create or replace NONEDITIONABLE FUNCTION          "GET_MSB_HIST" (RELATION IN VARCHAR2, MSB_COUNT IN INT, EXCL_TUPLE IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
IF (EXCL_TUPLE = 1) THEN
    OPEN inf_cursor FOR 'SELECT COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))) AS COUNT_DUPLICATED, 

             BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')) AS VALUE_DUPLICATED 

             FROM '||RELATION ||'

             WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT||' 

            GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))

             ORDER BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))';

        ELSE
          OPEN inf_cursor FOR 'SELECT COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))) AS COUNT_DUPLICATED, 

             BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')) AS VALUE_DUPLICATED

             FROM '||RELATION ||'

             WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT||' 

            GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))

             ORDER BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))';

              END IF;


    RETURN inf_cursor;
END GET_MSB_HIST;

create or replace NONEDITIONABLE FUNCTION          "GET_MSB_JOIN" (RELATION IN VARCHAR2, MSB_COUNT IN INT, EXCL_TUPLE IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    IF (EXCL_TUPLE = 1) THEN
      OPEN inf_cursor FOR 'SELECT COUNT(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS COUNT_DUPLICATED,

             MAX(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS MAX_DUPLICATED,

             MIN(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS MIN_DUPLICATED,

             AVG(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS AVG_DUPLICATED

      FROM '||RELATION ||' 

      WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT||'

      GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))';

    ELSE
      OPEN inf_cursor FOR 'SELECT COUNT(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS COUNT_DUPLICATED,

             MAX(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS MAX_DUPLICATED,

             MIN(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS MIN_DUPLICATED,

             AVG(COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')))) AS AVG_DUPLICATED

      FROM '||RELATION ||' 

      GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))';


    END IF;

    RETURN inf_cursor;
END GET_MSB_JOIN;


create or replace NONEDITIONABLE FUNCTION          "GET_MSBSK_JOIN" (RELATION IN VARCHAR2, MSB_COUNT IN INT, EXCL_TUPLE IN INT,  PRIV_KEY IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    IF (EXCL_TUPLE = 1) THEN
      OPEN inf_cursor FOR 'SELECT COUNT(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS COUNT_DUPLICATED,

             MAX(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MAX_DUPLICATED,

             MIN(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MIN_DUPLICATED,

             AVG(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS AVG_DUPLICATED

      FROM '||RELATION ||' 

      WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT||' AND
            LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT||'

      GROUP BY CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')';

    ELSE
       OPEN inf_cursor FOR 'SELECT COUNT(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS COUNT_DUPLICATED,

             MAX(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MAX_DUPLICATED,

             MIN(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS MIN_DUPLICATED,

             AVG(COUNT(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''))) AS AVG_DUPLICATED

      FROM '||RELATION ||' 

      GROUP BY CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||''')';


    END IF;

    RETURN inf_cursor;
END GET_MSBSK_JOIN;

create or replace NONEDITIONABLE FUNCTION          "GET_MSCHEME_GEN" (RELATION IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT, PRIV_KEY IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS ELEV_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS ASP_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS SLOP_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HDTH_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS VDTH_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HDTR_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HSDIX_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HSDN_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HSDIII_VALUE,
       CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT ||')),'''||PRIV_KEY||''') AS HDTFP_VALUE

FROM '||RELATION ||' 

WHERE (LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT ||'+'||LSB_COUNT||'               AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' ) AND
      (LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT ||'+'||LSB_COUNT||'                   AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' ) AND
      (LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT ||'+'||LSB_COUNT||'  AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' ) AND
      (LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||'           AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' ) AND
      (LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT ||'+'||LSB_COUNT||'           AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT ||'+'||LSB_COUNT||' ) ';



    RETURN inf_cursor;
END GET_MSCHEME_GEN;



create or replace NONEDITIONABLE FUNCTION  "GET_NNEXT" (LSB_COUNT IN INT, ID_CORE IN NUMBER, CANT_TUPL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN

     OPEN id_cursor FOR 'SELECT LLP, 
                                VPK_PERS, 
                                CONS_PERS, 
                                SEC_PERS,
                                ORA_HASH(VPK_PERS,'||LSB_COUNT||',SEED_PERS + 3) AS LSB_POS 
                         FROM COVERTYPE_A 
                         WHERE (SEC_PERS > -1) AND (CONS_PERS > 0) AND (LLP > '||ID_CORE||') AND (ROWNUM <= '||CANT_TUPL||')
                         ORDER BY LLP';

     RETURN id_cursor;
END GET_NNEXT;


create or replace NONEDITIONABLE FUNCTION  "GET_NO_FIELD_OF_TYPE" (FIELDTYPE IN VARCHAR2, RELATION IN VARCHAR2) RETURN INT AS 
  field_counter INT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT (*) FROM user_tab_columns WHERE TABLE_NAME = '''||RELATION||''' AND data_type ='''||FIELDTYPE||'''' INTO field_counter;
  RETURN field_counter;
END GET_NO_FIELD_OF_TYPE;


create or replace NONEDITIONABLE FUNCTION  "GET_NO_TUPLES" (RELATION IN VARCHAR2) RETURN INT AS 
  row_counter INT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||RELATION INTO row_counter;
  RETURN row_counter;
END GET_NO_TUPLES;


create or replace NONEDITIONABLE FUNCTION  "GET_NOEXCL_ATTR" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN INT AS 
  total_counter INT;
  considered_counter INT;
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||RELATION||' WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT INTO considered_counter;
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||RELATION INTO total_counter;

  RETURN total_counter - considered_counter;
END GET_NOEXCL_ATTR;


create or replace NONEDITIONABLE FUNCTION  "GET_NPREV" (LSB_COUNT IN INT, ID_CORE IN NUMBER, CANT_TUPL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN

     OPEN id_cursor FOR 'SELECT * FROM (SELECT LLP, VPK_PERS, CONS_PERS, SEC_PERS, ORA_HASH(VPK_PERS,'||LSB_COUNT||',SEED_PERS + 3) AS LSB_POS 
                                        FROM COVERTYPE_A 
                                        WHERE (SEC_PERS > -1) AND (CONS_PERS > 0) AND (LLP < '||ID_CORE||')
                                        ORDER BY LLP DESC)
                         WHERE ROWNUM <= '||CANT_TUPL;



     RETURN id_cursor;
END GET_NPREV;


create or replace NONEDITIONABLE FUNCTION  "GET_RANDOM_IDS" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2, NUM_TUPL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'WITH aux AS (SELECT id as ID, CREATE_VPK(id,'''||PRIV_KEY||''') AS VPK FROM (SELECT ID FROM '||RELATION ||' ORDER BY dbms_random.value) WHERE rownum <= '||NUM_TUPL ||') SELECT ID, VPK FROM aux ORDER BY ID';
  RETURN id_cursor;
END GET_RANDOM_IDS;


create or replace NONEDITIONABLE FUNCTION  "GET_RANDOM_MARKED_IDS" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2, NUM_TUPL IN INT, TUPLES_FRACTION IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'WITH aux AS (SELECT id as ID, CREATE_VPK(id,'''||PRIV_KEY||''') AS VPK FROM (SELECT ID FROM '||RELATION ||' ORDER BY dbms_random.value) WHERE rownum <= '||NUM_TUPL ||' AND MOD(CREATE_VPK(id,'''||PRIV_KEY||'''),'||TUPLES_FRACTION||')=0) SELECT ID, VPK FROM aux ORDER BY ID';
  RETURN id_cursor;
END GET_RANDOM_MARKED_IDS;


create or replace NONEDITIONABLE FUNCTION  "GET_RANDOM_TUPLES" (RELATION IN VARCHAR2, NUM_TUPL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'WITH aux AS (SELECT id as ID FROM (SELECT ID FROM '||RELATION ||' ORDER BY dbms_random.value) WHERE rownum <= '||NUM_TUPL ||' ) SELECT ID FROM aux ORDER BY ID';
  RETURN id_cursor;
END GET_RANDOM_TUPLES;


create or replace NONEDITIONABLE FUNCTION          "GET_RELATION_IDS" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2,TUPLES_FRACTION IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
  OPEN id_cursor FOR 'SELECT id, (SELECT CREATE_VPK(id,'''||PRIV_KEY||''') FROM dual)AS vpk, MOD((SELECT CREATE_VPK(id,'''||PRIV_KEY||''') FROM dual),'||TUPLES_FRACTION||') FROM '||RELATION ||' ORDER BY id';
  RETURN id_cursor;
END GET_RELATION_IDS;


create or replace NONEDITIONABLE FUNCTION  "GET_RND_IDS" (NUM_TUPL IN INT, ATTR_VAL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'WITH aux AS (SELECT id as ID FROM (SELECT ID, ORIGINAL_ATTR FROM COVERTYPE_K ORDER BY dbms_random.value) WHERE rownum <= '||NUM_TUPL ||' AND ORIGINAL_ATTR < '||ATTR_VAL ||') SELECT ID FROM aux ORDER BY ID';
  RETURN id_cursor;
END GET_RND_IDS;


create or replace NONEDITIONABLE FUNCTION          "GET_SPREAD_ATTR" (RELATION IN VARCHAR2, PRIV_KEY IN VARCHAR2,MSB_COUNT IN INT, VECTOR_SIZE IN INT, SEED_VALUE IN INT) RETURN sys_refcursor AS 


  id_cursor sys_refcursor;

BEGIN

  OPEN id_cursor FOR 'SELECT MIN(COUNT(CASE ((ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''),'||VECTOR_SIZE||',1) + '||SEED_VALUE||')) 

             WHEN 1 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||'))
             WHEN 2 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||'))                                                     
             WHEN 3 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||'))                                                     
             WHEN 4 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 5 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 6 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||'))                                                     
             WHEN 7 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||'))                                                     
             WHEN 8 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||'))                                                     
             WHEN 9 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||'))                                                     
             WHEN 10 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))                                                     
             END)) AS MIN_VALUE,

             MAX(COUNT(CASE ((ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''),'||VECTOR_SIZE||',1) + '||SEED_VALUE||')) 

             WHEN 1 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||'))
             WHEN 2 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||'))                                                     
             WHEN 3 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||'))                                                     
             WHEN 4 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 5 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 6 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||'))                                                     
             WHEN 7 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||'))                                                     
             WHEN 8 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||'))                                                     
             WHEN 9 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||'))                                                     
             WHEN 10 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))                                                     
             END)) AS MAX_VALUE,

             AVG(COUNT(CASE ((ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''),'||VECTOR_SIZE||',1) + '||SEED_VALUE||')) 

             WHEN 1 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||'))
             WHEN 2 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||'))                                                     
             WHEN 3 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||'))                                                     
             WHEN 4 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 5 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 6 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||'))                                                     
             WHEN 7 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||'))                                                     
             WHEN 8 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||'))                                                     
             WHEN 9 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||'))                                                     
             WHEN 10 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))                                                     
             END)) AS AVG_VALUE,

             COUNT(COUNT(CASE ((ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''),'||VECTOR_SIZE||',1) + '||SEED_VALUE||')) 

             WHEN 1 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||'))
             WHEN 2 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||'))                                                     
             WHEN 3 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||'))                                                     
             WHEN 4 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 5 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 6 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||'))                                                     
             WHEN 7 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||'))                                                     
             WHEN 8 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||'))                                                     
             WHEN 9 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||'))                                                     
             WHEN 10 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))                                                     
             END)) AS TOTAL_VALUE

  FROM '||RELATION||'

  WHERE LENGTH(num_to_bin(ELEVATION)) >= '||MSB_COUNT||'              AND LENGTH(num_to_bin(ASPECT)) >= '||MSB_COUNT||' AND
        LENGTH(num_to_bin(SLOPE)) >= '||MSB_COUNT||'                  AND LENGTH(num_to_bin(HOR_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND
        LENGTH(num_to_bin(VERT_DIST_TO_HYDROLOGY)) >= '||MSB_COUNT||' AND LENGTH(num_to_bin(HOR_DIST_TO_ROADWAYS)) >= '||MSB_COUNT||' AND
        LENGTH(num_to_bin(HILLSHADE_9AM)) >= '||MSB_COUNT||'          AND LENGTH(num_to_bin(HILLSHADE_NOON)) >= '||MSB_COUNT||' AND
        LENGTH(num_to_bin(HILLSHADE_3PM)) >= '||MSB_COUNT||'          AND LENGTH(num_to_bin(HOR_DIST_TO_FIRE_POINTS)) >= '||MSB_COUNT||'


  GROUP BY CASE ((ORA_HASH(CREATE_VPK(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||') ||
             SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||') || SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||')),'''||PRIV_KEY||'''),'||VECTOR_SIZE||',1) + '||SEED_VALUE||')) 

             WHEN 1 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ELEVATION)),1,'||MSB_COUNT||'))
             WHEN 2 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(ASPECT)),1,'||MSB_COUNT||'))                                                     
             WHEN 3 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(SLOPE)),1,'||MSB_COUNT||'))                                                     
             WHEN 4 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 5 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),1,'||MSB_COUNT||'))                                                     
             WHEN 6 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),1,'||MSB_COUNT||'))                                                     
             WHEN 7 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_9AM)),1,'||MSB_COUNT||'))                                                     
             WHEN 8 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_NOON)),1,'||MSB_COUNT||'))                                                     
             WHEN 9 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HILLSHADE_3PM)),1,'||MSB_COUNT||'))                                                     
             WHEN 10 THEN BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),1,'||MSB_COUNT||'))                                                     
             END';

  RETURN id_cursor;
END GET_SPREAD_ATTR;


create or replace NONEDITIONABLE FUNCTION   "GET_STATISTICS" (RELATION IN VARCHAR2) RETURN sys_refcursor AS 
  stat_cursor sys_refcursor;
BEGIN
    OPEN stat_cursor FOR 'SELECT AVG(ELEVATION) AS MEAN_ELEV, VARIANCE(ELEVATION) AS STD_ELEV, 
         AVG(ASPECT) AS MEAN_ASP,     STDDEV(ASPECT) AS STD_ASP, 
         AVG(SLOPE) AS MEAN_SLOPE, STDDEV(SLOPE) AS STD_SLOPE,
         AVG(HOR_DIST_TO_HYDROLOGY) AS MEAN_HDH, STDDEV(HOR_DIST_TO_HYDROLOGY) AS STD_HDH, 
         AVG(VERT_DIST_TO_HYDROLOGY) AS MEAN_VDH, STDDEV(VERT_DIST_TO_HYDROLOGY) AS STD_VDH, 
         AVG(HOR_DIST_TO_ROADWAYS) AS MEAN_RW, STDDEV(HOR_DIST_TO_ROADWAYS) AS STD_RW, 
         AVG(HILLSHADE_9AM) AS MEAN_HSIX, STDDEV(HILLSHADE_9AM) AS STD_HSIX,
         AVG(HILLSHADE_NOON) AS MEAN_HSN, STDDEV(HILLSHADE_NOON) AS STD_HSN, 
         AVG(HILLSHADE_3PM) AS MEAN_HSIII, STDDEV(HILLSHADE_3PM) AS STD_HSIII,
         AVG(HOR_DIST_TO_FIRE_POINTS) AS MEAN_FP, STDDEV(HOR_DIST_TO_FIRE_POINTS) AS STD_FP
       FROM '||RELATION;
  RETURN stat_cursor;
END GET_STATISTICS;


create or replace NONEDITIONABLE FUNCTION  "GET_VALUE_OF_ATTR" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2,TUPLE_ID IN INT) RETURN NUMBER AS 
 attr_value NUMBER;
BEGIN
  EXECUTE IMMEDIATE 'SELECT '||ATTR_NAME||' FROM '||RELATION||' WHERE id =:1' INTO attr_value USING TUPLE_ID;
  RETURN attr_value;
END GET_VALUE_OF_ATTR;


create or replace NONEDITIONABLE FUNCTION  "GET_VPK_DATA" (RELATION IN VARCHAR2, MSB_COUNT IN INT, NO_SEGMENTS IN INT, PRIV_KEY IN VARCHAR2) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 
        'SELECT ID, VALUE_VPK, QUARTILE 
         FROM (SELECT ID AS ID, CREATE_VPK(BINARY_CHAR_TO_INTEGER(CONSIDER_VALUE(TO_CHAR(num_to_bin(ELEVATION)),'||MSB_COUNT||')              || CONSIDER_VALUE(TO_CHAR(num_to_bin(ASPECT)),'||MSB_COUNT||') ||
                                                        CONSIDER_VALUE(TO_CHAR(num_to_bin(SLOPE)),'||MSB_COUNT||')                  || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') ||
                                                        CONSIDER_VALUE(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),'||MSB_COUNT||') ||
                                                        CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_9AM)),'||MSB_COUNT||')          || CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_NOON)),'||MSB_COUNT||') ||
                                                        CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_3PM)),'||MSB_COUNT||')          || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),'||MSB_COUNT||')),'''||PRIV_KEY||''') AS VALUE_VPK,

                      NTILE('||NO_SEGMENTS||') OVER (ORDER BY CREATE_VPK(BINARY_CHAR_TO_INTEGER(CONSIDER_VALUE(TO_CHAR(num_to_bin(ELEVATION)),'||MSB_COUNT||')              || CONSIDER_VALUE(TO_CHAR(num_to_bin(ASPECT)),'||MSB_COUNT||') ||
                                                                                CONSIDER_VALUE(TO_CHAR(num_to_bin(SLOPE)),'||MSB_COUNT||')                  || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') ||
                                                                                CONSIDER_VALUE(TO_CHAR(num_to_bin(VERT_DIST_TO_HYDROLOGY)),'||MSB_COUNT||') || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_ROADWAYS)),'||MSB_COUNT||') ||
                                                                                CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_9AM)),'||MSB_COUNT||')          || CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_NOON)),'||MSB_COUNT||') ||
                                                                                CONSIDER_VALUE(TO_CHAR(num_to_bin(HILLSHADE_3PM)),'||MSB_COUNT||')          || CONSIDER_VALUE(TO_CHAR(num_to_bin(HOR_DIST_TO_FIRE_POINTS)),'||MSB_COUNT||')),'''||PRIV_KEY||''')) AS QUARTILE
               FROM '||RELATION ||' ) TEMP_TABLE
         ORDER BY VALUE_VPK';


    RETURN inf_cursor;
END GET_VPK_DATA;


create or replace NONEDITIONABLE FUNCTION          "GET_VPK_HIST" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2, MSB_COUNT IN INT, LSB_COUNT IN INT) RETURN sys_refcursor AS 
  inf_cursor sys_refcursor;
BEGIN
    OPEN inf_cursor FOR 'SELECT COUNT(BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||'))) AS VPK_AMOUNT, BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')) AS VPK_VALUE FROM '||RELATION ||' WHERE LENGTH(num_to_bin('||ATTR_NAME ||')) >= '||MSB_COUNT ||'+'||LSB_COUNT||' GROUP BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||')) ORDER BY BINARY_CHAR_TO_INTEGER(SUBSTR(TO_CHAR(num_to_bin('||ATTR_NAME ||')),1,'||MSB_COUNT||'))';
    RETURN inf_cursor;
END GET_VPK_HIST;

create or replace NONEDITIONABLE FUNCTION  "INC_VARIABILITY" (DECIMAL_VALUE IN VARCHAR2, REDUCT_FAC IN INT) RETURN INT AS 
    value_acum INT;
    value_gen INT;
    inc_step INT;
    j INT;
    num_stored NUMBER_ARRAY;

BEGIN
    value_acum := 0;
    j := 1;
    inc_step := 0;
    num_stored := NUMBER_ARRAY();
    num_stored.EXTEND(LENGTH(DECIMAL_VALUE));

    FOR i IN 1..LENGTH(DECIMAL_VALUE) LOOP
        num_stored(i) := 0;
    END LOOP;

    FOR i IN 1..num_stored.COUNT LOOP
        num_stored(i) := BINARY_CHAR_TO_INTEGER(TO_CHAR(SUBSTR(DECIMAL_VALUE,1,LENGTH(DECIMAL_VALUE)-i+1))) ;
    END LOOP;

    WHILE j <= num_stored.COUNT LOOP
        value_gen := num_stored(j);
        inc_step := MOD(value_gen,REDUCT_FAC)+1;
        value_acum := value_acum + value_gen;         
        j := j+inc_step;
    END LOOP;

    RETURN value_acum;
END INC_VARIABILITY;


create or replace NONEDITIONABLE FUNCTION "NUM_TO_BIN" ( i_num IN NUMBER )
    RETURN VARCHAR2 IS
       l_Num      NUMBER;
       l_bit      PLS_INTEGER;
       l_binary   VARCHAR2(128);
    BEGIN
       --
       l_num := i_num;
      --
      WHILE l_num > 1 LOOP
         l_bit := MOD(l_num,2);
         l_binary := TO_CHAR(l_bit)||l_binary;
         l_num := FLOOR(l_num / 2);
      END LOOP;
      --
      IF l_num = 1 THEN
          l_binary := '1'||l_binary;
      END IF;
      --
      RETURN l_binary;
      --
   END num_to_bin;
   
   
create or replace NONEDITIONABLE FUNCTION "ORA_HASH_UTIL" (VPK IN VARCHAR2, DIM_MEASURE IN INT,PSEED IN INT) RETURN INT AS 
 pos INT;
BEGIN
  SELECT ORA_HASH(VPK,DIM_MEASURE,PSEED) INTO pos FROM dual;
  RETURN pos;
END ORA_HASH_UTIL;


create or replace NONEDITIONABLE FUNCTION  "RTW_ATTR_VALUE" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2,TUPLE_ID IN INT) RETURN CLOB AS 
 attr_value CLOB;
BEGIN
  EXECUTE IMMEDIATE 'SELECT '||ATTR_NAME||' FROM '||RELATION||' WHERE id =:1' INTO attr_value USING TUPLE_ID;
  RETURN attr_value;
END RTW_ATTR_VALUE;


create or replace NONEDITIONABLE FUNCTION  "RTW_CREATE_VPK" (current_id IN INT, SECRET_KEY IN VARCHAR2) RETURN INT AS 
vpk INT;
BEGIN
  SELECT ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => current_id || SECRET_KEY)) AS VPK INTO vpk FROM dual;
  RETURN vpk;
END RTW_CREATE_VPK;



create or replace NONEDITIONABLE FUNCTION  "RTW_GENERATE_AVK" (SECRET_KEY IN VARCHAR2, VPK_TUPL IN INT, LOCK_STREAM IN INT, TUPL_FR IN INT, IMAGE_HEIGHT IN INT, IMAGE_WIDTH IN INT) RETURN sys_refcursor AS  
apk_info sys_refcursor;
BEGIN 
  OPEN apk_info FOR 'SELECT  ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string =>          ''' || SECRET_KEY || ''' ||' || VPK_TUPL || '||' || LOCK_STREAM ||')) AS AVK,  
                             ORA_HASH(ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => ''' || SECRET_KEY || ''' ||' || VPK_TUPL || '||' || LOCK_STREAM ||')), '|| IMAGE_HEIGHT ||', MOD(ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => ''' || SECRET_KEY || ''' ||' || VPK_TUPL || '||' || LOCK_STREAM ||')),' || TUPL_FR || ' + 1 )+1' || ') AS H, 
                             ORA_HASH(ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => ''' || SECRET_KEY || ''' ||' || VPK_TUPL || '||' || LOCK_STREAM ||')), '|| IMAGE_WIDTH  ||', MOD(ORA_HASH(sys.dbms_obfuscation_toolkit.md5(input_string => ''' || SECRET_KEY || ''' ||' || VPK_TUPL || '||' || LOCK_STREAM ||')),' || TUPL_FR || ' + 1 )+2' || ') AS W 
                       FROM dual';

  RETURN apk_info;
END RTW_GENERATE_AVK;



create or replace NONEDITIONABLE FUNCTION  "RTW_GET_ALLDATA" (RELATION IN VARCHAR2) RETURN sys_refcursor AS 
  data_cursor sys_refcursor;
BEGIN
  OPEN data_cursor FOR 'SELECT * FROM '||RELATION ;

  RETURN data_cursor;

END RTW_GET_ALLDATA;



create or replace NONEDITIONABLE FUNCTION  "RTW_GET_EMB_BCHECK" (RELATION IN VARCHAR2, TUPLE_ID IN INT) RETURN CLOB AS 
 attr_value CLOB;
BEGIN
  EXECUTE IMMEDIATE 'SELECT B_EMB_SK FROM '||RELATION||'  WHERE id =:1' INTO attr_value USING TUPLE_ID;
  RETURN attr_value;
END RTW_GET_EMB_BCHECK;


create or replace NONEDITIONABLE FUNCTION  "RTW_GET_EXT_BCHECK" (RELATION IN VARCHAR2, TUPLE_ID IN INT) RETURN CLOB AS 
 attr_value CLOB;
BEGIN
  EXECUTE IMMEDIATE 'SELECT B_EXT_SK FROM '||RELATION||' WHERE id =:1' INTO attr_value USING TUPLE_ID;
  RETURN attr_value;
END RTW_GET_EXT_BCHECK;


create or replace NONEDITIONABLE FUNCTION  "RTW_GET_GENINF" (RELATION IN VARCHAR2, TUPLES_FRACTION IN INT, IMAGE_HEIGHT IN INT, IMAGE_WIDTH IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN

    OPEN id_cursor FOR 'SELECT ID, VPK, TUPL_FACTOR, ORA_HASH(VPK,'||IMAGE_HEIGHT||',MOD(VPK,'||TUPLES_FRACTION||'+1)+1) AS H, 
                                                     ORA_HASH(VPK,'||IMAGE_WIDTH||', MOD(VPK,'||TUPLES_FRACTION||'+1)+2) AS W
                                   FROM '||RELATION ||' ORDER BY ID';

  RETURN id_cursor;

END RTW_GET_GENINF;

create or replace NONEDITIONABLE FUNCTION  "RTW_GET_RANDOM_TUPLES" (RELATION IN VARCHAR2, NUM_TUPL IN INT) RETURN sys_refcursor AS 
  id_cursor sys_refcursor;
BEGIN
    OPEN id_cursor FOR 'WITH aux AS (SELECT id as ID FROM (SELECT ID FROM '||RELATION||' ORDER BY dbms_random.value) WHERE rownum <= '||NUM_TUPL ||' ) SELECT ID FROM aux ORDER BY ID';
    RETURN id_cursor;
END RTW_GET_RANDOM_TUPLES;


create or replace NONEDITIONABLE FUNCTION "RTW_GET_RANDOM_VALUE" (RELATION IN VARCHAR2, ATTR_NAME IN VARCHAR2) RETURN CLOB AS 
    ATTR_VAL CLOB;

BEGIN
    ATTR_VAL := NULL;

    EXECUTE IMMEDIATE 'SELECT '||ATTR_NAME||' FROM (SELECT '||ATTR_NAME||' FROM '||RELATION||' ORDER BY dbms_random.value) WHERE rownum = 1' INTO ATTR_VAL;
    RETURN ATTR_VAL;
END "RTW_GET_RANDOM_VALUE";


create or replace NONEDITIONABLE FUNCTION  "XOR_SIM" (FIRST_DECIMAL IN INT, SECOND_DECIMAL IN INT) RETURN INT AS 
    result_value INT;

BEGIN
    result_value := 0;
    result_value := (FIRST_DECIMAL + SECOND_DECIMAL) - BITAND(FIRST_DECIMAL, SECOND_DECIMAL) * 2; 
    RETURN result_value;
END XOR_SIM;

























































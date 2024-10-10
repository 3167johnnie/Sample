For Fetching file from DataBase 

FILE_STATUS	NUMBER(2-BYTE)				"00 - Initial stage.
01 - File records inserted in to ACHFILE/ACHM Table.
10 - Ready for return file.
20 - return file generated suc.
99 - error while inserting in ACHFILE table"
LAST_STATUS_TIME	VARCHAR2(08-BYTE)				Dissscussion for adding date for this field. 
RETURN_FILENAME	VARCHAR2(100-BYTE)	Added.			

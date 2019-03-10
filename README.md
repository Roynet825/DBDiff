# DBDiff

## Start

1. Create table and init db in it.  
   Refer: resource/sampl_data.sql  
   (Some SQL may have wrong tablespace in your db, please remove tablespace part)
2. Run `DiffChecker#main` to check result, it should looks like:
   ```
   ****************************************************
   Table: [test_a] diff check result:
   ----------------------------------------------------
     * Deleted :
        #3#:{"NAME":"carol","SYN_ID":3}
   ----------------------------------------------------
     * Added :
        #2#:{"NAME":"test","SYN_ID":2}
   ----------------------------------------------------
     * Modified due to multiple DK :
   ----------------------------------------------------
     * Modified:
        [OLD]#1#:{"NAME":"roy","SYN_ID":1}
        [NEW]#1#:{"NAME":"roy_mod","SYN_ID":1}
   ----------------------------------------------------
   ****************************************************
   ```
3. Run `SnapshotCreator` for create snapshot.

## Details

 * `SnapshotCreator`: read data from watch table, and create snapshot, store data. 
 * `DiffChecker`: read data from snapshot datas, and check the diff.


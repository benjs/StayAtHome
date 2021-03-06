package com.example.stayathome.treedatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TreeDao {

    @Insert
    void insert(Tree tree);

    @Update
    void update(Tree tree);

    @Delete
    void delete(Tree tree);

    @Query("SELECT * FROM tree_table WHERE id =:treeId")
    LiveData<Tree> getTree(int treeId);

    @Query("SELECT * FROM tree_table")
    LiveData<List<Tree>> getTrees();
}

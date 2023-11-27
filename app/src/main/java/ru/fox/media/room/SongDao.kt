package ru.fox.media.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM SongEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: SongEntity)

    @Query("UPDATE SongEntity SET playing = :playing WHERE id = :id")
    fun updateContentByUd(id: Long, playing: Boolean)

    fun save(song: SongEntity) = insert(song)

    @Query(
        """
                UPDATE SongEntity SET
                favourite = favourite + CASE WHEN favourite THEN -1 ELSE 1 END,
                favourite = CASE WHEN favourite THEN 0 ELSE 1 END
                WHERE id = :id;    
                """
    )
    fun favouriteById(id: Long)

    @Query("DELETE FROM SongEntity WHERE id = :id")
    fun removeById(id: Long)
}
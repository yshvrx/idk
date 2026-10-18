package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM calendar_events ORDER BY startEpochMillis ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE startEpochMillis >= :start AND startEpochMillis <= :end ORDER BY startEpochMillis ASC")
    fun getEventsBetween(start: Long, end: Long): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CalendarEventEntity>)

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Delete
    suspend fun deleteEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("SELECT COUNT(*) FROM calendar_events")
    suspend fun getCount(): Int

    @Query("SELECT * FROM calendar_events")
    suspend fun getAllEventsSnapshot(): List<CalendarEventEntity>
}

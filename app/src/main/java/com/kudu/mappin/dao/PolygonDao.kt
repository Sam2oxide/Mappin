package com.kudu.mappin.dao

import androidx.room.*
import com.kudu.mappin.model.Polygon

@Dao
interface PolygonDao {
    @Query("SELECT * FROM polygon")
    fun getAllPolygons(): List<Polygon>

   /* @Query("SELECT * FROM polygon WHERE id IN (:polygonIds)")
    fun loadAllByIds(polygonIds: IntArray): List<Polygon>*/

 /*   @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Polygon*/

   /* @Insert
    fun insertAll(vararg polygons: Polygon)*/

    @Insert
    fun insertPolygon(polygon: Polygon)

    @Update
    fun updatePolygon(polygon: Polygon)

    @Delete
    fun delete(polygon: Polygon)

}
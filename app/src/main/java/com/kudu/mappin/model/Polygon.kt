package com.kudu.mappin.model

/*
@Entity(tableName = "polygon")*/
data class Polygon(
    /* @PrimaryKey val id: String,
     @ColumnInfo(name = "nombre") val nombre: String,
     @ColumnInfo(name = "comentario") val comentario: String,*/
    val id: String,
    val nombre: String,
    val comentario: String,
)
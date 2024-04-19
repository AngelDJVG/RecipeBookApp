package com.appsmoviles.proyectomoviles.dominio
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appsmoviles.proyectomoviles.enums.TipoReceta

@Entity
data class Receta(
    val titulo: String,
    val tipo: TipoReceta,
    val preparacion: String,
    val tiempoPreparacion: String,
    val tiempoCocinado: String,
    val imagen: String?,
    val listaIngredientes: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        TipoReceta.valueOf(parcel.readString() ?: ""),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeString(tipo.name)
        parcel.writeString(preparacion)
        parcel.writeString(tiempoPreparacion)
        parcel.writeString(tiempoCocinado)
        parcel.writeString(imagen)
        parcel.writeString(listaIngredientes)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Receta> {
        override fun createFromParcel(parcel: Parcel): Receta {
            return Receta(parcel)
        }

        override fun newArray(size: Int): Array<Receta?> {
            return arrayOfNulls(size)
        }
    }
}

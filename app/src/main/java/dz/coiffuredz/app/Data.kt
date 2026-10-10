package dz.coiffuredz.app

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = ""
)

@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val priceDzd: Int,
    val durationMin: Int = 30
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val clientName: String,
    val serviceId: Long,
    val serviceName: String,
    val priceDzd: Int,
    /** epoch day (days since 1970-01-01) so the book works offline with no date lib */
    val dateEpochDay: Long,
    /** minutes since midnight, e.g. 9:30 -> 570 */
    val timeMin: Int,
    val chair: Int = 1,
    /** BOOKED | DONE | CANCELLED */
    val status: String = "BOOKED",
    val notes: String = ""
)

@Dao
interface ShopDao {
    @Query("SELECT * FROM clients ORDER BY name")
    fun clients(): Flow<List<Client>>

    @Query("SELECT * FROM services ORDER BY name")
    fun services(): Flow<List<Service>>

    @Query("SELECT * FROM appointments WHERE dateEpochDay = :day ORDER BY timeMin")
    fun dayAppointments(day: Long): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY dateEpochDay DESC, timeMin DESC")
    fun clientHistory(clientId: Long): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClient(c: Client): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertService(s: Service): Long

    @Insert
    suspend fun insertAppointment(a: Appointment): Long

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Long, status: String)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Long)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun serviceCount(): Int
}

@Database(entities = [Client::class, Service::class, Appointment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shop(): ShopDao
}

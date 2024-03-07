import android.util.Log
import com.example.tictacparty.GlobalVariables
import com.example.tictacparty.Player
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Function {
    var auth: FirebaseAuth = Firebase.auth

    suspend fun getHighscore(): List<Pair<String, Int>> {
        return suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val playersCollection = db.collection("players")
            playersCollection.get().addOnSuccessListener { documents ->
                    val highScore = mutableListOf<Pair<String, Int>>()
                    for (document in documents) {
                        // Convert the document data to a Player object
                        val userName = document.getString("username") ?: ""
                        val mmrScore = document.getLong("mmrScore")?.toInt() ?: 0
                        val userPair = Pair(userName, mmrScore)
                        highScore.add(userPair)
                    }
                    val sortedList = highScore.sortedByDescending { it.second }
                    continuation.resume(sortedList)
                }.addOnFailureListener { exception ->
                    continuation.resumeWith(Result.failure(exception))
                }
        }
    }

    suspend fun getPlayerObject(userId: String?): Player? {
        return suspendCoroutine { continuation ->
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            if (userId != null) {
                val playersCollection = db.collection("players")
                var player: Player? = null
                playersCollection.whereEqualTo("userId", userId).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Convert the document data to a Player object
                            player = document.toObject(Player::class.java)
                            // Use the player object as needed
                            Log.d("!!!", "Player: $player")
                            if (player != null) {
                                GlobalVariables.player = player
                                continuation.resume(player)
                                return@addOnSuccessListener
                            }
                        }
                        continuation.resume(null)


                        GlobalVariables.loggedInUser = auth.currentUser?.email
                        GlobalVariables.loggedIn = true
                        Log.d("!!!", "Authentication succeeded.")

                    }.addOnFailureListener { exception ->
                        Log.w("!!!", "Error getting documents: ", exception)
                        continuation.resume(null)
                    }
            } else {
                continuation.resume(null)
                return@suspendCoroutine
            }
        }
    }

    fun removeMatchmakingRoom(roomId: String) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("matchmaking_rooms").document(roomId)

        roomRef.delete().addOnSuccessListener {
                Log.d("!!!", "Matchmaking room successful deleted.")
            }.addOnFailureListener { exception ->
                Log.w("!!!", "Matchmaking room failed deletion.", exception)
            }
    }
}
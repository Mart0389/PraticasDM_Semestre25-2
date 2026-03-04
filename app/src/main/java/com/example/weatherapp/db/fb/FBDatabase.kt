package com.example.weatherapp.db.fb

import kotlinx.coroutines.flow.Flow
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class FBDatabase {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val user: Flow<FBUser>
        get() {
            if (auth.currentUser == null) return emptyFlow()
            return db.collection("users")
                .document(auth.currentUser!!.uid)
                .snapshots()
                .map { it.toObject(FBUser::class.java)!! }
        }

    val cities: Flow<List<FBCity>>
        get() {
            if (auth.currentUser == null) return emptyFlow()
            return db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("cities")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FBCity::class.java)
                }
        }


    fun register(user: FBUser) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user)
    }

    fun add(city: FBCity) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        val name = city.name ?: throw RuntimeException("City name is null!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(name).set(city)
    }

    fun remove(city: FBCity) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        val name = city.name ?: throw RuntimeException("City name is null!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(name).delete()
    }

    fun update(city: FBCity) {
        if (auth.currentUser == null) throw RuntimeException("Not logged in!")
        val name = city.name ?: throw RuntimeException("City name is null!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(name).set(city) // Simplificado para usar o objeto todo
    }


}


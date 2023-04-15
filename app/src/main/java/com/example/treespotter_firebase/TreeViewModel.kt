package com.example.treespotter_firebase

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TreeViewModel: ViewModel() {

    private val db = Firebase.firestore
    private val treeCollectionReference = db.collection("trees")

    val latestTrees = MutableLiveData<List<Tree>>()

    private val latestTreeListener = treeCollectionReference
        .orderBy("dateSpotted", Query.Direction.DESCENDING)
        .limit(10)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d(TAG, "Error fetching latest trees", error)
            } else if (snapshot != null) {
                val trees = snapshot.toObjects(Tree::class.java)
                Log.d(TAG, "Trees from firebase: $trees")
                latestTrees.postValue(trees)
            }
        }

}

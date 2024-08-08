package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.seesaw.databinding.ActivityFrag5AuctionBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class Frag5_Auction : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityFrag5AuctionBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        fun newInstance(): Frag5_Auction {
            return Frag5_Auction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityFrag5AuctionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 네비게이션 뷰 설정
        binding.navigationView.setNavigationItemSelectedListener(this)

        // 버튼 클릭 리스너 설정
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnChangeInfo.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                showChangeInfoDialog(currentUser)
            }
        }

        binding.btnExit.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                showReauthenticateDialog(currentUser)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_account -> {
                val intent = Intent(context, AccountActivity::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayoutFrag5Auction.closeDrawer(GravityCompat.END)
        return true
    }

    // 로그아웃 다이얼로그 창 -> 로그아웃 호출
    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                logoutAndRedirectToLogin()
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }

    // 정보 수정 다이얼로그 창 -> 재인증 호출
    private fun showChangeInfoDialog(user: FirebaseUser) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reauthenticate, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("비밀번호를 입력하세요")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val password = passwordEditText.text.toString().trim()
                reauthenticateAndChange(user, password)
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }

    // 계정 탈퇴 다이얼로그 창 -> 재인증 호출
    private fun showReauthenticateDialog(user: FirebaseUser) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reauthenticate, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("비밀번호를 입력하세요")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val password = passwordEditText.text.toString().trim()
                reauthenticateAndDelete(user, password)
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }

    // 재인증이 완료되면 정보 수정 호출
    private fun reauthenticateAndChange(user: FirebaseUser, password: String) {
        val email = user.email
        if (email != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(activity, ChangeInfo::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "재인증에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            showChangeInfoDialog(currentUser)
                        }
                    }
                }
        }
    }

    // 재인증이 완료되면 계정 삭제 호출
    private fun reauthenticateAndDelete(user: FirebaseUser, password: String) {
        val email = user.email
        if (email != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deleteUserAccount(user)
                    } else {
                        Toast.makeText(requireContext(), "재인증에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            showReauthenticateDialog(currentUser)
                        }
                    }
                }
        }
    }

    // 계정 삭제
    private fun deleteUserAccount(user: FirebaseUser) {
        val uid = user.uid
        firestore.collection("id_list").document(uid)
            .delete()
            .addOnSuccessListener {
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            logoutAndRedirectToLogin()
                        } else {
                            Toast.makeText(requireContext(), "계정 탈퇴 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    // 로그아웃 + 자동로그인 삭제
    private fun logoutAndRedirectToLogin() {
        // 로그아웃
        auth.signOut()

        // 공유 프리퍼런스 파일 삭제
        val sharedPrefsFile = File("/data/data/com.example.seesaw/shared_prefs/userID.xml")
        if (sharedPrefsFile.exists()) {
            sharedPrefsFile.delete()
            Log.d(ContentValues.TAG, "자동로그인 파일이 삭제되었습니다.")
        }

        // 로그인 화면으로 전환
        val intent = Intent(activity, Login::class.java)
        startActivity(intent)
        Toast.makeText(requireContext(), "로그아웃", Toast.LENGTH_SHORT).show()
    }
}

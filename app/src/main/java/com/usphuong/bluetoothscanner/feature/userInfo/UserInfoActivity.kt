package com.usphuong.bluetoothscanner.feature.userInfo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.usphuong.bluetoothscanner.Constants.EXTRA_URL
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.feature.webView.WebViewActivity
import com.usphuong.bluetoothscanner.utils.setLinkClickEvent
import com.usphuong.bluetoothscanner.utils.showDialog
import com.usphuong.bluetoothscanner.viewModel.UserViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_userinfo.ivAvatar
import kotlinx.android.synthetic.main.activity_userinfo.refresh
import kotlinx.android.synthetic.main.activity_userinfo.tvAddress
import kotlinx.android.synthetic.main.activity_userinfo.tvCompany
import kotlinx.android.synthetic.main.activity_userinfo.tvEmail
import kotlinx.android.synthetic.main.activity_userinfo.tvName
import kotlinx.android.synthetic.main.activity_userinfo.tvPhone
import kotlinx.android.synthetic.main.activity_userinfo.tvUsername
import kotlinx.android.synthetic.main.activity_userinfo.tvWebsite
import javax.inject.Inject

class UserInfoActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        AndroidInjection.inject(this)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)


        supportActionBar?.title = getString(R.string.user_info)

        initObserver()
        userViewModel.getUserInfo()
        refresh.isRefreshing = true

        refresh.setOnRefreshListener {
            userViewModel.getUserInfo()
        }
    }

    private fun initObserver() {
        userViewModel.userInfoLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            setUserInfo(it)
        })

        userViewModel.errorLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            showDialog(getString(R.string.error), it)
        })
    }

    private fun setUserInfo(user: User) {
        Glide.with(this).load(user.avatar).circleCrop().into(ivAvatar)
        tvName.text = getString(R.string.name, user.name)
        tvUsername.text = getString(R.string.username, user.username)
        tvEmail.text = getString(R.string.email, user.email)
        tvAddress.text = getString(R.string.address, user.address.toString())
        tvPhone.text = getString(R.string.phone, user.phone)
        tvWebsite.text = getString(R.string.website, user.website)
        tvCompany.text = getString(R.string.company, user.company.name)

        tvWebsite.setLinkClickEvent {
            startActivity(
                Intent(this, WebViewActivity::class.java)
                    .putExtra(EXTRA_URL, it)
            )
        }
    }
}

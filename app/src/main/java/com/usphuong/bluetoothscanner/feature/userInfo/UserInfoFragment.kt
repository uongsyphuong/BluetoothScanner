package com.usphuong.bluetoothscanner.feature.userInfo

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.base.BaseFragment
import com.usphuong.bluetoothscanner.data.model.User
import com.usphuong.bluetoothscanner.utils.setLinkClickEvent
import com.usphuong.bluetoothscanner.utils.showDialog
import com.usphuong.bluetoothscanner.viewModel.UserViewModel
import kotlinx.android.synthetic.main.fragment_userinfo.ivAvatar
import kotlinx.android.synthetic.main.fragment_userinfo.refresh
import kotlinx.android.synthetic.main.fragment_userinfo.tvAddress
import kotlinx.android.synthetic.main.fragment_userinfo.tvCompany
import kotlinx.android.synthetic.main.fragment_userinfo.tvEmail
import kotlinx.android.synthetic.main.fragment_userinfo.tvName
import kotlinx.android.synthetic.main.fragment_userinfo.tvPhone
import kotlinx.android.synthetic.main.fragment_userinfo.tvUsername
import kotlinx.android.synthetic.main.fragment_userinfo.tvWebsite

class UserInfoFragment : BaseFragment() {

    private val userViewModel by activityViewModels<UserViewModel> { viewModelFactory }

    override val layoutId: Int
        get() = R.layout.fragment_userinfo

    override fun initObserver() {
        userViewModel.userInfoLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            setUserInfo(it)
        })

        userViewModel.errorLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            activity?.showDialog(getString(R.string.error), it)
        })
    }

    override fun initView() {
        userViewModel.getUserInfo()
        refresh.isRefreshing = true

        refresh.setOnRefreshListener {
            userViewModel.getUserInfo()
        }
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
            navController.navigate(R.id.action_userinfoFragment_to_webViewFragment)
        }
    }
}

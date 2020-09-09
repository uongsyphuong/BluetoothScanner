package com.usphuong.bluetoothscanner.feature.github

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.usphuong.bluetoothscanner.FindQuery
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.base.BaseFragment
import com.usphuong.bluetoothscanner.utils.showDialog
import com.usphuong.bluetoothscanner.viewModel.GithubViewModel
import kotlinx.android.synthetic.main.fragment_github.btnQuery
import kotlinx.android.synthetic.main.fragment_github.edtName
import kotlinx.android.synthetic.main.fragment_github.edtOwner
import kotlinx.android.synthetic.main.fragment_github.refresh
import kotlinx.android.synthetic.main.fragment_github.tvDescription
import kotlinx.android.synthetic.main.fragment_github.tvName
import kotlinx.android.synthetic.main.fragment_github.tvUrl

class GithubFragment : BaseFragment(){

    private val githubViewModel by viewModels<GithubViewModel>()

    override val layoutId: Int
        get() = R.layout.fragment_github

    override fun initObserver() {
        githubViewModel.repoLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            setData(it)
        })

        githubViewModel.errorLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            activity?.showDialog(getString(R.string.error), it)
        })
    }

    override fun initView() {

        btnQuery.setOnClickListener {
            if (!edtName.text.isNullOrEmpty() &&!edtOwner.text.isNullOrEmpty()){
                refresh.isRefreshing = true
                githubViewModel.getRepo(edtOwner.text.toString(), edtName.text.toString())
            }
        }    }

    private fun setData(it: FindQuery.Data?) {
        tvName.text = "Name ${it?.repository?.name}"
        tvDescription.text = "Description ${it?.repository?.description}"
        tvUrl.text = "Url ${it?.repository?.url}"
    }
}

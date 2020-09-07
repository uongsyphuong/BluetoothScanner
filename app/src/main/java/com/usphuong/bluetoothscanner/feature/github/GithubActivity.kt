package com.usphuong.bluetoothscanner.feature.github

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.usphuong.bluetoothscanner.FindQuery
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.utils.showDialog
import com.usphuong.bluetoothscanner.viewModel.GithubViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_github.btnQuery
import kotlinx.android.synthetic.main.activity_github.edtName
import kotlinx.android.synthetic.main.activity_github.edtOwner
import kotlinx.android.synthetic.main.activity_github.refresh
import kotlinx.android.synthetic.main.activity_github.tvDescription
import kotlinx.android.synthetic.main.activity_github.tvName
import kotlinx.android.synthetic.main.activity_github.tvUrl
import javax.inject.Inject

class GithubActivity : AppCompatActivity(){
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var githubViewModel: GithubViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github)
        AndroidInjection.inject(this)

        githubViewModel = ViewModelProvider(this, viewModelFactory).get(GithubViewModel::class.java)

        initObserver()

        btnQuery.setOnClickListener {
            if (!edtName.text.isNullOrEmpty() &&!edtOwner.text.isNullOrEmpty()){
                refresh.isRefreshing = true
                githubViewModel.getUserInfo(edtOwner.text.toString(), edtName.text.toString())
            }
        }
    }

    private fun initObserver() {
        githubViewModel.repoLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            setData(it)
        })

        githubViewModel.errorLiveData.observe(this, Observer {
            refresh.isRefreshing = false
            showDialog(getString(R.string.error), it)
        })
    }

    private fun setData(it: FindQuery.Data?) {
        tvName.text = "Name ${it?.repository?.name}"
        tvDescription.text = "Description ${it?.repository?.description}"
        tvUrl.text = "Url ${it?.repository?.url}"
    }
}
